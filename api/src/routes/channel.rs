use actix_web::{get, http::StatusCode, web, Responder};
use common::{
    establish_connection, models::Channel, schema::channels::dsl as ch, ExpressionMethods,
    QueryDsl, RunQueryDsl,
};

use super::models::Response;

pub async fn get_channel_by_twitch_id(name: web::Path<i32>) -> impl Responder {
    let conn = &mut establish_connection();

    let response = match ch::channels
        .filter(ch::alias_id.eq(&*name))
        .get_result::<Channel>(conn)
    {
        Ok(v) => (
            Response {
                status_code: 200,
                message: None,
                data: Some(v),
            },
            StatusCode::OK,
        ),
        Err(_) => (
            Response {
                status_code: 404,
                message: Some(format!(
                    "Twitch user ID {} doesn't exist in my database",
                    name
                )),
                data: None,
            },
            StatusCode::NOT_FOUND,
        ),
    };

    (web::Json(response.0), response.1)
}
