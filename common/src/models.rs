use crate::schema::*;
use chrono::NaiveDateTime;
use diesel::prelude::*;

#[derive(Queryable, Identifiable)]
pub struct Channel {
    pub id: i32,
    pub alias_id: i32,
    pub alias_name: String,
    pub joined_at: NaiveDateTime,
    pub opt_outed_at: Option<NaiveDateTime>,
}

#[derive(Queryable, Identifiable)]
pub struct User {
    pub id: i32,
    pub alias_id: i32,
    pub alias_name: String,
    pub joined_at: NaiveDateTime,
    pub opt_outed_at: Option<NaiveDateTime>,
}

#[derive(diesel_derive_enum::DbEnum, Debug, PartialEq)]
#[ExistingTypePath = "crate::schema::sql_types::EmoteProviderType"]
pub enum EmoteType {
    Twitch,
    BetterTTV,
    FrankerFaceZ,
    SevenTV,
}

#[derive(Queryable, Identifiable)]
pub struct Emote {
    pub id: i32,
    pub alias_id: String,
    pub alias_type: EmoteType,
    pub is_global: bool,
    pub spotted_at: NaiveDateTime,
}

#[derive(Queryable, Identifiable, Associations)]
#[diesel(belongs_to(Channel, foreign_key = channel_id))]
#[diesel(belongs_to(Emote, foreign_key = emote_id))]
#[diesel(table_name = channel_emotes)]
pub struct ChannelEmote {
    pub id: i32,
    pub emote_id: i32,
    pub channel_id: i32,
    pub name: String,
    pub spotted_at: NaiveDateTime,
    pub removed_at: Option<NaiveDateTime>,
}

#[derive(Queryable, Identifiable, Associations)]
#[diesel(belongs_to(Channel, foreign_key = channel_id))]
#[diesel(belongs_to(User, foreign_key = user_id))]
#[diesel(belongs_to(Emote, foreign_key = emote_id))]
#[diesel(table_name = emote_usage)]
pub struct EmoteUsage {
    pub id: i32,
    pub emote_id: i32,
    pub channel_id: i32,
    pub user_id: i32,
    pub usage_count: i32,
    pub first_use_at: NaiveDateTime,
    pub last_use_at: NaiveDateTime,
}
