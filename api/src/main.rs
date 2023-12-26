use std::io::Result;

use actix_web::{App, HttpServer};

mod routes;

#[actix_web::main]
async fn main() -> Result<()> {
    println!("Hello, world!");

    HttpServer::new(|| App::new().service(crate::routes::channel::index))
        .bind(("127.0.0.1", 8080))?
        .run()
        .await
}
