pub mod api;
pub(super) mod schema;

use futures::SinkExt;
use serde_json::Value;
use std::{collections::HashSet, sync::Arc};

use eyre::Context;
use reqwest::Url;
use tokio::{net::TcpStream, sync::Mutex};
use tokio_tungstenite::{
    connect_async_with_config,
    tungstenite::{protocol::WebSocketConfig, Message},
    MaybeTlsStream, WebSocketStream,
};
use twitch_api::types::UserId;

use crate::providers::seventv::schema::Payload;

use common::{
    establish_connection, insert_into,
    models::{Channel, ChannelEmote, Emote, EmoteType, NewChannelEmote, NewEmote},
    schema::{channel_emotes::dsl as che, channels::dsl as ch, emotes::dsl as em},
    update, BelongingToDsl, ExpressionMethods, NaiveDateTime, QueryDsl, RunQueryDsl, Utc,
};

use self::{api::SevenTVAPIClient, schema::*};

use super::WebsocketData;

async fn connect(url: Url) -> Result<WebSocketStream<MaybeTlsStream<TcpStream>>, eyre::Error> {
    let config = WebSocketConfig::default();

    let (socket, _) = connect_async_with_config(url, Some(config), false).await?;

    Ok(socket)
}

const SEVENTV_WEBSOCKET_URL: &str = "wss://events.7tv.io/v3";

pub struct SevenTVWebsocketClient {
    socket: WebSocketStream<MaybeTlsStream<TcpStream>>,
    data: Arc<Mutex<WebsocketData>>,
    api: Arc<SevenTVAPIClient>,
    session_id: Option<String>,
    reconnect_url: Url,
}

impl SevenTVWebsocketClient {
    pub async fn new(
        data: Arc<Mutex<WebsocketData>>,
        api: Arc<SevenTVAPIClient>,
    ) -> Result<Self, eyre::Error> {
        let reconnect_url = Url::parse(SEVENTV_WEBSOCKET_URL).unwrap();

        Ok(Self {
            socket: connect(reconnect_url.clone()).await?,
            session_id: None,
            reconnect_url,
            data,
            api,
        })
    }

    pub async fn run(&mut self) -> Result<(), eyre::Error> {
        loop {
            self.process_awaiting_channels().await?;
            tokio::select!(
                Some(msg) = futures::StreamExt::next(&mut self.socket) => {
                    let msg = match msg {
                        Err(tungstenite::Error::Protocol(tungstenite::error::ProtocolError::ResetWithoutClosingHandshake)) => {
                            self.socket = connect(self.reconnect_url.clone()).await.context("when reestablishing connection")?;
                            continue
                        }
                        _ => msg.context("when getting message")?,
                    };
                    self.process_message(msg).await?
                }
            )
        }
    }

    async fn flip_channels(&self) {
        let mut data = self.data.lock().await;

        let mut set_a: HashSet<UserId> =
            HashSet::from_iter(data.awaiting_channel_ids.iter().cloned());
        let set_b: HashSet<UserId> = HashSet::from_iter(data.listening_channel_ids.iter().cloned());

        set_a.extend(set_b);

        data.awaiting_channel_ids = Vec::from_iter(set_a.into_iter());
        data.listening_channel_ids.clear();
    }

    pub async fn process_message(&mut self, msg: Message) -> Result<(), eyre::Report> {
        match msg {
            Message::Text(s) => {
                println!("received 7tv text message: {s}");

                if let Ok(e) = serde_json::from_str::<Payload<Value>>(s.as_str()) {
                    let d = e.d.to_string();
                    let d = d.as_str();

                    match e.op {
                        // Dispatch
                        0 => {
                            if let Ok(d) = serde_json::from_str::<Dispatch>(d) {
                                self.handle_dispatch(d).await?;
                            }
                        }
                        // Hello
                        1 => {
                            if let Ok(d) = serde_json::from_str::<Hello>(d) {
                                if self.session_id.is_none() {
                                    self.session_id = Some(d.session_id);
                                    self.process_awaiting_channels().await?;
                                } else {
                                    self.resume_session().await?;
                                }
                            }
                        }
                        // Heartbeat
                        2 => println!("[7TV EventAPI] Heartbeat!"),
                        // Reconnect
                        4 => {
                            println!("[7TV EventAPI] Reconnect!");

                            self.flip_channels().await;
                            self.socket.close(None).await?;
                            self.socket = connect(self.reconnect_url.clone()).await?;
                        }
                        // Error
                        6 => println!("[7TV EventAPI] Error: {}", e.d),
                        // End of Stream
                        7 => {
                            println!("[7TV EventAPI] The host has closed the connection! Reason: ")
                        }
                        _ => println!(
                            "[7TV EventAPI] Unhandled opcode: {}. Payload: {}",
                            e.op, e.d
                        ),
                    }
                }
            }
            Message::Close(e) => {
                let e = if e.is_some() {
                    let unwrapped_e = e.unwrap();

                    format!("{} {}", unwrapped_e.code, unwrapped_e.reason)
                } else {
                    "No reason".to_string()
                };

                println!("The connection to 7TV EventAPI was refused: {e}");
            }
            _ => {}
        }

        Ok(())
    }

    async fn handle_dispatch(&mut self, body: Dispatch) -> Result<(), eyre::Error> {
        if body.event_type != "emote_set.update".to_string() {
            println!("[7TV EventAPI] Unhandled body type: {}", body.event_type);
            return Ok(());
        }

        if let Some(emote_set) = self.api.get_emote_set(body.body.id).await {
            if let Some(emote_set_owner) = emote_set.owner {
                if let Some(emote_set_owner) = self.api.get_user(emote_set_owner.id).await {
                    if let Some(owner) = emote_set_owner
                        .connections
                        .iter()
                        .find(|x| x.platform.eq("TWITCH"))
                    {
                        let actor_name = if let Some(connection) = body
                            .body
                            .actor
                            .connections
                            .iter()
                            .find(|x| x.platform.eq("TWITCH"))
                        {
                            connection.username.clone()
                        } else {
                            body.body.actor.username
                        };

                        let conn = &mut establish_connection();

                        let channel: Channel = ch::channels
                            .filter(ch::alias_id.eq(owner.id.parse::<i32>().unwrap()))
                            .first(conn)
                            .expect("Failed to get channel");

                        if let Some(pushed) = body.body.pushed {
                            for e in pushed {
                                let emote = e.value.unwrap();
                                let emote_name = emote.name;

                                let emote_dsl: Emote = em::emotes
                                    .filter(em::alias_type.eq(&EmoteType::SevenTV))
                                    .filter(em::alias_id.eq(&emote.id))
                                    .get_result(conn)
                                    .unwrap_or({
                                        insert_into(em::emotes)
                                            .values([NewEmote {
                                                alias_id: emote.id,
                                                alias_type: EmoteType::SevenTV,
                                            }])
                                            .get_result(conn)
                                            .expect("Failed to insert new emote")
                                    });

                                match ChannelEmote::belonging_to(&channel)
                                    .filter(che::emote_id.eq(&emote_dsl.id))
                                    .first::<ChannelEmote>(conn)
                                {
                                    Ok(v) => {
                                        if v.removed_at.is_some() {
                                            update(che::channel_emotes.find(&v.id))
                                                .set(che::removed_at.eq(None::<NaiveDateTime>))
                                                .execute(conn)
                                                .expect("Failed to update channel emote");
                                        }
                                    }
                                    Err(_) => {
                                        insert_into(che::channel_emotes)
                                            .values([NewChannelEmote {
                                                emote_id: emote_dsl.id,
                                                channel_id: channel.id,
                                                name: emote_name,
                                            }])
                                            .execute(conn)
                                            .expect("Failed to insert new channel emote");
                                    }
                                }
                            }
                        }

                        if let Some(pulled) = body.body.pulled {
                            for e in pulled {
                                let emote = e.old_value.unwrap();
                                let emote_name = emote.name;

                                let emote_dsl: Emote = em::emotes
                                    .filter(em::alias_type.eq(&EmoteType::SevenTV))
                                    .filter(em::alias_id.eq(&emote.id))
                                    .get_result(conn)
                                    .unwrap_or({
                                        insert_into(em::emotes)
                                            .values([NewEmote {
                                                alias_id: emote.id,
                                                alias_type: EmoteType::SevenTV,
                                            }])
                                            .get_result(conn)
                                            .expect("Failed to insert new emote")
                                    });

                                match ChannelEmote::belonging_to(&channel)
                                    .filter(che::emote_id.eq(&emote_dsl.id))
                                    .first::<ChannelEmote>(conn)
                                {
                                    Ok(v) => {
                                        if v.removed_at.is_some() {
                                            update(che::channel_emotes.find(&v.id))
                                                .set(che::removed_at.eq(Utc::now().naive_utc()))
                                                .execute(conn)
                                                .expect("Failed to update channel emote");
                                        }
                                    }
                                    Err(_) => {
                                        let v: ChannelEmote = insert_into(che::channel_emotes)
                                            .values([NewChannelEmote {
                                                emote_id: emote_dsl.id,
                                                channel_id: channel.id,
                                                name: emote_name,
                                            }])
                                            .get_result(conn)
                                            .expect("Failed to insert new channel emote");

                                        update(che::channel_emotes.find(&v.id))
                                            .set(che::removed_at.eq(Utc::now().naive_utc()))
                                            .execute(conn)
                                            .expect("Failed to update channel emote");
                                    }
                                }
                            }
                        }

                        if let Some(updated) = body.body.updated {
                            for e in updated {
                                let emote = e.value.unwrap();
                                let emote_name = emote.name;
                                //let old_emote_name = e.old_value.unwrap().name;

                                let emote_dsl: Emote = em::emotes
                                    .filter(em::alias_type.eq(&EmoteType::SevenTV))
                                    .filter(em::alias_id.eq(&emote.id))
                                    .get_result(conn)
                                    .unwrap_or({
                                        insert_into(em::emotes)
                                            .values([NewEmote {
                                                alias_id: emote.id,
                                                alias_type: EmoteType::SevenTV,
                                            }])
                                            .get_result(conn)
                                            .expect("Failed to insert new emote")
                                    });

                                match ChannelEmote::belonging_to(&channel)
                                    .filter(che::emote_id.eq(&emote_dsl.id))
                                    .first::<ChannelEmote>(conn)
                                {
                                    Ok(v) => {
                                        update(che::channel_emotes.find(&v.id))
                                            .set(che::name.eq(emote_name))
                                            .execute(conn)
                                            .expect("Failed to update channel emote");
                                    }
                                    Err(_) => {
                                        insert_into(che::channel_emotes)
                                            .values([NewChannelEmote {
                                                emote_id: emote_dsl.id,
                                                channel_id: channel.id,
                                                name: emote_name,
                                            }])
                                            .execute(conn)
                                            .expect("Failed to insert new channel emote");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Ok(())
    }

    async fn process_awaiting_channels(&mut self) -> Result<(), eyre::Error> {
        let mut data = self.data.lock().await;

        let ids = data
            .awaiting_channel_ids
            .iter()
            .filter(|x| !data.listening_channel_ids.iter().any(|y| y.eq(*x)))
            .cloned()
            .collect::<Vec<UserId>>();

        data.awaiting_channel_ids = ids.clone();

        drop(data);

        if !ids.is_empty() {
            for id in ids {
                self.listen_channel(id).await?;
            }
        }

        Ok(())
    }

    async fn listen_channel(&mut self, channel_id: UserId) -> Result<(), eyre::Error> {
        if let Some(user) = self
            .api
            .get_user_by_twitch_id(channel_id.clone().take())
            .await
        {
            let emote_set_id = user.emote_set.id;

            let data = Payload {
                op: 35,
                d: Subscribe {
                    event_type: "emote_set.update".to_string(),
                    condition: SubscribeCondition {
                        object_id: emote_set_id,
                    },
                },
            };

            println!("{:?}", serde_json::to_string(&data).unwrap());
            self.socket
                .send(Message::Text(serde_json::to_string(&data).unwrap()))
                .await?;

            println!("Listening 7TV events for {}'s emote set", user.username);

            let mut data = self.data.lock().await;
            data.listening_channel_ids.push(channel_id);
        }

        Ok(())
    }

    async fn resume_session(&mut self) -> Result<(), eyre::Error> {
        if self.session_id.is_none() {
            println!("[7TV EventAPI] Failed to resume a session because session_id is none!");

            return Ok(());
        }

        let data = Payload {
            op: 34,
            d: Resume {
                session_id: self.session_id.clone().unwrap(),
            },
        };

        self.socket
            .send(Message::Text(serde_json::to_string(&data)?))
            .await?;

        Ok(())
    }
}
