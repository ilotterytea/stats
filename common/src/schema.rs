// @generated automatically by Diesel CLI.

pub mod sql_types {
    #[derive(diesel::query_builder::QueryId, diesel::sql_types::SqlType)]
    #[diesel(postgres_type(name = "emote_provider_type"))]
    pub struct EmoteProviderType;
}

diesel::table! {
    channel_emotes (id) {
        id -> Int4,
        emote_id -> Int4,
        channel_id -> Int4,
        name -> Varchar,
        spotted_at -> Timestamp,
        removed_at -> Nullable<Timestamp>,
    }
}

diesel::table! {
    channels (id) {
        id -> Int4,
        alias_id -> Int4,
        alias_name -> Varchar,
        joined_at -> Timestamp,
        opt_outed_at -> Nullable<Timestamp>,
    }
}

diesel::table! {
    emote_usage (id) {
        id -> Int4,
        emote_id -> Int4,
        channel_id -> Int4,
        user_id -> Int4,
        usage_count -> Int4,
        first_use_at -> Timestamp,
        last_use_at -> Timestamp,
    }
}

diesel::table! {
    use diesel::sql_types::*;
    use super::sql_types::EmoteProviderType;

    emotes (id) {
        id -> Int4,
        alias_id -> Varchar,
        alias_type -> EmoteProviderType,
        is_global -> Bool,
        spotted_at -> Timestamp,
    }
}

diesel::table! {
    users (id) {
        id -> Int4,
        alias_id -> Int4,
        alias_name -> Varchar,
        joined_at -> Timestamp,
        opt_outed_at -> Nullable<Timestamp>,
    }
}

diesel::joinable!(channel_emotes -> channels (channel_id));
diesel::joinable!(channel_emotes -> emotes (emote_id));
diesel::joinable!(emote_usage -> channels (channel_id));
diesel::joinable!(emote_usage -> emotes (emote_id));
diesel::joinable!(emote_usage -> users (user_id));

diesel::allow_tables_to_appear_in_same_query!(
    channel_emotes,
    channels,
    emote_usage,
    emotes,
    users,
);
