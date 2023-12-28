use common::NaiveDateTime;
use serde::{Deserialize, Serialize};

#[derive(Serialize)]
pub struct Response<T> {
    pub status_code: i32,
    pub message: Option<String>,
    pub data: Option<T>,
}

#[derive(Serialize)]
pub struct EmoteUsage {
    pub emote_id: i32,
    pub usage_count: i32,
    pub first_use_at: NaiveDateTime,
    pub last_use_at: NaiveDateTime,
}

#[derive(Deserialize)]
pub struct JoinPartRequest {
    pub twitch_id: u32,
}
