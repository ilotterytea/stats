use crate::schema::*;
use diesel::prelude::*;

#[derive(Queryable)]
pub struct Channel {
    pub id: i32,
    pub alias_id: String,
    pub platform_id: i32,
    pub creation_timestamp: i32,
    pub last_modified_timestamp: i32,
}

#[derive(Insertable)]
#[diesel(table_name = channels)]
pub struct NewChannel<'a> {
    pub alias_id: &'a str,
    pub platform_id: i32,
    pub creation_timestamp: i32,
    pub last_modified_timestamp: i32,
}

#[derive(Queryable)]
pub struct Emote {
    pub id: i32,
    pub provider_id: String,
    pub provider: i32,
    pub channel_id: i32,
    pub name: String,
    pub used_times: i32,
    pub is_deleted: i32,
    pub is_global: i32,
    pub creation_timestamp: i32,
    pub last_modified_timestamp: i32,
}

#[derive(Insertable)]
#[diesel(table_name = emotes)]
pub struct NewEmote<'a> {
    pub provider_id: &'a str,
    pub provider: i32,
    pub channel_id: i32,
    pub name: &'a str,
    pub creation_timestamp: i32,
    pub last_modified_timestamp: i32,
}

#[derive(Queryable)]
pub struct NameHistory {
    pub id: i32,
    pub emote_id: i32,
    pub previous_name: String,
    pub name: String,
    pub creation_timestamp: i32,
}

#[derive(Insertable)]
#[diesel(table_name = name_history)]
pub struct NewNameHistory<'a> {
    pub emote_id: i32,
    pub previous_name: &'a str,
    pub name: &'a str,
    pub creation_timestamp: i32,
}

#[derive(Queryable)]
pub struct Stats {
    pub id: i32,
    pub channel_id: i32,
    pub user_id: i32,
    pub chat_lines: i32,
}

#[derive(Insertable)]
#[diesel(table_name = stats)]
pub struct NewStats {
    pub channel_id: i32,
    pub user_id: i32,
}

#[derive(Queryable)]
pub struct User {
    pub id: i32,
    pub alias_id: String,
    pub platform_id: i32,
}

#[derive(Insertable)]
#[diesel(table_name = users)]
pub struct NewUser<'a> {
    pub alias_id: &'a str,
    pub platform_id: i32,
}
