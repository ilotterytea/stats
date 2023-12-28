use std::io::Result;

use actix_web::{web, App, HttpServer};

use crate::routes::channel::*;

mod routes;

#[actix_web::main]
async fn main() -> Result<()> {
    dotenvy::dotenv().expect("Failed to load .env");

    println!("Hello, world!");

    HttpServer::new(|| {
        App::new().service(
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
