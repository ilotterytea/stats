// @generated automatically by Diesel CLI.

diesel::table! {
    channels (id) {
        id -> Integer,
        alias_id -> Text,
        platform_id -> Integer,
        creation_timestamp -> Integer,
        last_modified_timestamp -> Integer,
    }
}

diesel::table! {
    emotes (id) {
        id -> Integer,
        provider_id -> Text,
        provider -> Integer,
        channel_id -> Integer,
        name -> Text,
        used_times -> Integer,
        is_deleted -> Integer,
        is_global -> Integer,
        creation_timestamp -> Integer,
        last_modified_timestamp -> Integer,
    }
}

diesel::table! {
    name_history (id) {
        id -> Integer,
        emote_id -> Integer,
        previous_name -> Text,
        name -> Text,
        creation_timestamp -> Integer,
    }
}

diesel::table! {
    stats (id) {
        id -> Integer,
        channel_id -> Integer,
        user_id -> Integer,
        chat_lines -> Integer,
    }
}

diesel::table! {
    users (id) {
        id -> Integer,
        alias_id -> Text,
        platform_id -> Integer,
    }
}

diesel::allow_tables_to_appear_in_same_query!(
    channels,
    emotes,
    name_history,
    stats,
    users,
);
