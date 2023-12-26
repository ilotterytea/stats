use actix_web::{get, http::StatusCode, web, HttpResponse, Responder};
use common::{
    establish_connection,
    models::{Channel, ChannelEmote, EmoteUsage},
    schema::channels::dsl as ch,
    BelongingToDsl, ExpressionMethods, QueryDsl, RunQueryDsl,
};

use super::models::{EmoteUsage as WebEmoteUsage, Response};

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

pub async fn get_channel_emotes_by_twitch_id(name: web::Path<i32>) -> impl Responder {
    let conn = &mut establish_connection();

    let channel: Channel = match ch::channels
        .filter(ch::alias_id.eq(&*name))
        .get_result(conn)
    {
        Ok(v) => v,
        Err(_) => {
            return (
                web::Json(Response {
                    status_code: 404,
                    message: Some(format!(
                        "Twitch user ID {} doesn't exist in my database",
                        name
                    )),
                    data: None::<Vec<ChannelEmote>>,
                }),
                StatusCode::NOT_FOUND,
            );
        }
    };

    let response: (Response<Vec<ChannelEmote>>, StatusCode) =
        match ChannelEmote::belonging_to(&channel).get_results(conn) {
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
                    message: Some(format!("No channel emotes for Twitch user ID {}", name)),
                    data: None,
                },
                StatusCode::NOT_FOUND,
            ),
        };

    (web::Json(response.0), response.1)
}

pub async fn get_emote_usage_by_twitch_id(name: web::Path<i32>) -> HttpResponse {
    let conn = &mut establish_connection();

    let channel: Channel = match ch::channels
        .filter(ch::alias_id.eq(&*name))
        .get_result(conn)
    {
        Ok(v) => v,
        Err(_) => {
            return HttpResponse::NotFound().json(Response {
                status_code: 404,
                message: Some(format!(
                    "Twitch user ID {} doesn't exist in my database",
                    name
                )),
                data: None::<Channel>,
            });
        }
    };

    let emote_usage: Vec<EmoteUsage> = match EmoteUsage::belonging_to(&channel).get_results(conn) {
        Ok(v) => v,
        Err(_) => {
            return HttpResponse::NotFound().json(Response {
                status_code: 404,
                message: Some(format!("No emote usages for Twitch user ID {}", name)),
                data: None::<Vec<EmoteUsage>>,
            })
        }
    };

    let mut new_emote_usage: Vec<WebEmoteUsage> = Vec::new();

    for usage in emote_usage {
        if let Some(e_usage) = new_emote_usage
            .iter_mut()
            .find(|x| x.emote_id == usage.emote_id)
        {
            e_usage.usage_count += usage.usage_count;

            if usage.first_use_at.timestamp() < e_usage.first_use_at.timestamp() {
                e_usage.first_use_at = usage.first_use_at;
            }

            if usage.last_use_at.timestamp() > e_usage.last_use_at.timestamp() {
                e_usage.last_use_at = usage.last_use_at;
            }
        } else {
            new_emote_usage.push(WebEmoteUsage {
                emote_id: usage.emote_id,
                usage_count: usage.usage_count,
                first_use_at: usage.first_use_at,
                last_use_at: usage.last_use_at,
            });
        }
    }

    HttpResponse::Ok().json(Response {
        status_code: 200,
        message: None,
        data: Some(new_emote_usage),
    })
}
