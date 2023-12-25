use std::sync::Arc;

use common::{establish_connection, models::Channel};
use tokio::sync::Mutex;
use twitch_api::types::UserId;
use twitch_irc::{
    login::StaticLoginCredentials, message::ServerMessage, ClientConfig, SecureTCPTransport,
    TwitchIRCClient,
};

use crate::{
    handlers::handle_chat_messages,
    providers::{
        seventv::{api::SevenTVAPIClient, SevenTVWebsocketClient},
        WebsocketData,
    },
};

use common::{schema::channels::dsl as ch, ExpressionMethods, QueryDsl, RunQueryDsl};

mod handlers;
mod providers;

#[tokio::main]
pub async fn main() {
    println!("Hello, world!");

    let config = ClientConfig::default();
    let (mut incoming_messages, irc_client) =
        TwitchIRCClient::<SecureTCPTransport, StaticLoginCredentials>::new(config);

    let conn = &mut establish_connection();

    let channel_ids: Vec<i32> = ch::channels
        .filter(ch::opt_outed_at.is_null())
        .select(ch::alias_id)
        .get_results(conn)
        .expect("Failed to get channel IDs");

    let channel_ids = channel_ids
        .iter()
        .map(|x| UserId::new(x.to_string()))
        .collect::<Vec<UserId>>();

    let reqwest_client = reqwest::Client::default();

    let seventv_api_client = Arc::new(SevenTVAPIClient::new(reqwest_client));

    let seventv_websocket_data = Arc::new(Mutex::new({
        let mut data = WebsocketData::default();
        data.awaiting_channel_ids = channel_ids.clone();
        data
    }));

    let mut seventv_websocket =
        SevenTVWebsocketClient::new(seventv_websocket_data.clone(), seventv_api_client.clone())
            .await
            .unwrap();

    let seventv_websocket_handle = tokio::spawn(async move {
        seventv_websocket.run().await.unwrap();
    });

    let irc_handle = tokio::spawn({
        async move {
            while let Some(message) = incoming_messages.recv().await {
                println!("received message: {:?}", message);

                if let ServerMessage::Privmsg(msg) = message {
                    handle_chat_messages(msg).await;
                }
            }
        }
    });

    tokio::join!(irc_handle, seventv_websocket_handle);
}
