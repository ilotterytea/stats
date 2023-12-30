use std::{str::FromStr, sync::Arc};

use common::{
    establish_connection, insert_into,
    models::{Channel, ChannelEmote, EmoteUsage, NewEmoteUsage, NewUser, User},
    schema::{channels::dsl as ch, emote_usage::dsl as emu, users::dsl as us},
    update, BelongingToDsl, ExpressionMethods, QueryDsl, RunQueryDsl, Utc,
};
use tokio::sync::Mutex;
use twitch_api::types::UserId;
use twitch_irc::{
    login::StaticLoginCredentials, message::PrivmsgMessage, SecureTCPTransport, TwitchIRCClient,
};

use crate::providers::WebsocketData;

pub async fn handle_chat_messages(message: PrivmsgMessage) {
    let conn = &mut establish_connection();

    let channel_alias_id = message.channel_id.parse::<i32>().unwrap();

    let channel = ch::channels
        .filter(ch::alias_id.eq(&channel_alias_id))
        .first::<Channel>(conn)
        .expect(
            format!(
                "Failed to load channel with ID {}",
                channel_alias_id.to_string()
            )
            .as_str(),
        );

    let user_alias_id = message.sender.id.parse::<i32>().unwrap();

    let user = us::users
        .filter(us::alias_id.eq(&user_alias_id))
        .first::<User>(conn)
        .unwrap_or_else(|_| {
            insert_into(us::users)
                .values([NewUser {
                    alias_id: user_alias_id,
                    alias_name: message.sender.login,
                }])
                .get_result::<User>(conn)
                .expect(
                    format!("Failed to load user with ID {}", user_alias_id.to_string()).as_str(),
                )
        });

    let mut emote_usages: Vec<EmoteUsage> = EmoteUsage::belonging_to(&channel)
        .filter(emu::user_id.eq(user.id))
        .get_results(conn)
        .expect("Failed to load emote usage and emotes");

    let mut new_emote_usages: Vec<NewEmoteUsage> = Vec::new();

    let channel_emotes = ChannelEmote::belonging_to(&channel)
        .load::<ChannelEmote>(conn)
        .expect("Failed to load channel emotes");

    let message_split = message.message_text.split(" ").collect::<Vec<&str>>();

    for word in message_split {
        let channel_emotes = channel_emotes
            .iter()
            .filter(|x| x.name.eq(&word))
            .collect::<Vec<&ChannelEmote>>();

        for channel_emote in channel_emotes {
            if let Some(usage) = emote_usages
                .iter_mut()
                .find(|x| x.emote_id.eq(&channel_emote.emote_id))
            {
                usage.usage_count += 1;
                usage.last_use_at = Utc::now().naive_utc();
            } else {
                new_emote_usages.push(NewEmoteUsage {
                    emote_id: channel_emote.emote_id,
                    channel_id: channel.id,
                    user_id: user.id,
                });
            }
        }
    }

    for usage in emote_usages {
        update(emu::emote_usage.find(usage.id))
            .set((
                emu::usage_count.eq(usage.usage_count),
                emu::last_use_at.eq(usage.last_use_at),
            ))
            .execute(conn)
            .expect("Failed to update an emote usage");
    }

    insert_into(emu::emote_usage)
        .values(new_emote_usages)
        .execute(conn)
        .expect("Failed to create new emote usages");
}

pub async fn handle_new_channels(
    data: Arc<Mutex<WebsocketData>>,
    irc_client: Arc<TwitchIRCClient<SecureTCPTransport, StaticLoginCredentials>>,
) {
    let conn = &mut establish_connection();

    if let Ok(channels) = ch::channels.get_results::<Channel>(conn) {
        let mut data = data.lock().await;

        let alive_channels = channels
            .iter()
            .filter(|x| x.opt_outed_at.is_none())
            .filter(|x| {
                let x = UserId::new(x.alias_id.to_string());

                !data.listening_channel_ids.iter().any(|y| y.eq(&x))
                    && !data.awaiting_channel_ids.iter().any(|y| y.eq(&x))
            })
            .collect::<Vec<&Channel>>();

        for channel in alive_channels {
            data.awaiting_channel_ids
                .push(UserId::new(channel.alias_id.to_string()));

            if let Ok(_) = irc_client.join(channel.alias_name.clone()) {
                println!(
                    "[TWITCH IRC] Successfully joined {}'s chat room!",
                    channel.alias_name
                );
            }
        }
    }
}
