use std::io::Result;

use actix_web::{App, HttpServer};

mod routes;

#[actix_web::main]
async fn main() -> Result<()> {
    dotenvy::dotenv().expect("Failed to load .env");

    println!("Hello, world!");

    HttpServer::new(|| App::new().service(crate::routes::channel::get_channel_by_twitch_id))
        .bind(("127.0.0.1", 8080))?
        .run()
        .await
}
