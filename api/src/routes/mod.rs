use actix_web::{web, HttpResponse, Responder, Result};
use common::{
    delete, establish_connection, insert_into,
    models::{Channel, NewChannel},
    schema::channels::dsl as ch,
    update, ExpressionMethods, QueryDsl, RunQueryDsl, Utc,
};
use twitch_api::types::UserIdRef;

use crate::SharedTwitchData;

use self::models::{JoinPartRequest, Response};

pub(crate) mod channel;
pub(crate) mod models;

pub async fn join_channel(
    data: web::Json<JoinPartRequest>,
    twitch: web::Data<SharedTwitchData>,
) -> Result<HttpResponse> {
    let conn = &mut establish_connection();

    let id = data.twitch_id as i32;

    if let Ok(_) = ch::channels
        .filter(ch::alias_id.eq(&id))
        .get_result::<Channel>(conn)
    {
        return Ok(HttpResponse::Conflict().json(Response {
            status_code: 409,
            message: Some(format!(
                "Attempting to join a channel that is already joined or opted out of."
            )),
            data: None::<i32>,
        }));
    }

    if let Ok(Some(user)) = twitch
        .api_client
        .get_user_from_id(UserIdRef::from_str(id.to_string().as_str()), &twitch.token)
        .await
    {
        insert_into(ch::channels)
            .values([NewChannel {
                alias_id: id,
                alias_name: user.login.clone().take(),
            }])
            .execute(conn)
            .expect("Failed to create a new channel");

        return Ok(HttpResponse::Ok().json(Response {
            status_code: 200,
            message: Some(format!("Successfully joined {}'s channel!", user.login)),
            data: None::<i32>,
        }));
    }

    Ok(HttpResponse::NotFound().json(Response {
        status_code: 404,
        message: Some(format!("Twitch ID {} doesn't exist", data.twitch_id)),
        data: None::<i32>,
    }))
}

pub async fn part_channel(data: web::Json<JoinPartRequest>) -> Result<HttpResponse> {
    let conn = &mut establish_connection();

    let id = data.twitch_id as i32;

    if let Ok(c) = ch::channels
        .filter(ch::alias_id.eq(&id))
        .get_result::<Channel>(conn)
    {
        if c.opt_outed_at.is_some() {
            return Ok(HttpResponse::Ok().json(Response {
                status_code: 200,
                message: Some(format!("Has already parted {}'s channel!", c.alias_name)),
                data: None::<i32>,
            }));
        } else {
            update(ch::channels.find(&c.id))
                .set(ch::opt_outed_at.eq(Utc::now().naive_utc()))
                .execute(conn)
                .expect("Failed to update a channel");

            return Ok(HttpResponse::Ok().json(Response {
                status_code: 200,
                message: Some(format!(
                    "Successfully parted from {}'s channel",
                    c.alias_name
                )),
                data: None::<i32>,
            }));
        }
    }

    Ok(HttpResponse::NotFound().json(Response {
        status_code: 404,
        message: Some(format!(
            "Twitch ID {} doesn't exist or is not joined",
            data.twitch_id
        )),
        data: None::<i32>,
    }))
}
