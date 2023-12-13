use twitch_irc::{
    login::StaticLoginCredentials, ClientConfig, SecureTCPTransport, TwitchIRCClient,
};

#[tokio::main]
pub async fn main() {
    println!("Hello, world!");

    let config = ClientConfig::default();
    let (mut incoming_messages, irc_client) =
        TwitchIRCClient::<SecureTCPTransport, StaticLoginCredentials>::new(config);

    let irc_handle = tokio::spawn(async move {
        while let Some(message) = incoming_messages.recv().await {
            println!("received message: {:?}", message);
        }
    });

    tokio::join!(irc_handle);
}
