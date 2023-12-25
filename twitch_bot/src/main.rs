use twitch_irc::{
    login::StaticLoginCredentials, message::ServerMessage, ClientConfig, SecureTCPTransport,
    TwitchIRCClient,
};

use crate::handlers::handle_chat_messages;

mod handlers;

#[tokio::main]
pub async fn main() {
    println!("Hello, world!");

    let config = ClientConfig::default();
    let (mut incoming_messages, irc_client) =
        TwitchIRCClient::<SecureTCPTransport, StaticLoginCredentials>::new(config);

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

    tokio::join!(irc_handle);
}
