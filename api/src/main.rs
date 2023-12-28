use std::{env, io::Result};

use actix_web::{web, App, HttpServer};
use twitch_api::{
    twitch_oauth2::{AccessToken, UserToken},
    HelixClient,
};

use crate::routes::channel::*;

mod routes;

pub struct SharedTwitchData {
    pub token: UserToken,
    pub api_client: HelixClient<'static, reqwest::Client>,
}

#[actix_web::main]
async fn main() -> Result<()> {
    dotenvy::dotenv().expect("Failed to load .env");

    let reqwest_client = reqwest::Client::default();

    let helix_token = match UserToken::from_token(
        &reqwest_client,
        AccessToken::new(
            env::var("BOT_ACCESS_TOKEN")
                .unwrap_or_else(|_| panic!("No BOT_ACCESS_TOKEN value specified in .env file!")),
        ),
    )
    .await
    {
        Ok(token) => token,
        Err(e) => panic!("Failed to construct user token: {}", e),
    };

    let helix_client = HelixClient::with_client(reqwest_client.clone());

    let shared_twitch_data = web::Data::new(SharedTwitchData {
        token: helix_token,
        api_client: helix_client,
    });

    println!("Hello, world!");

    HttpServer::new(move || {
        App::new().app_data(shared_twitch_data.clone()).service(
            web::scope("/api").service(
                web::scope("/v1").service(
                    web::scope("/channel").service(
                        web::scope("/twitch").service(
                            web::scope("/{name}")
                                .service(web::resource("").to(get_channel_by_twitch_id))
                                .service(
                                    web::scope("/emotes")
                                        .service(
                                            web::resource("").to(get_channel_emotes_by_twitch_id),
                                        )
                                        .service(
                                            web::resource("/usage")
                                                .to(get_emote_usage_by_twitch_id),
                                        ),
                                ),
                        ),
                    ),
                ),
            ),
        )
    })
    .bind(("0.0.0.0", 8080))?
    .run()
    .await
}
