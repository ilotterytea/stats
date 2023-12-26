-- Your SQL goes here
CREATE TABLE IF NOT EXISTS "channels" (
  "id" SERIAL NOT NULL PRIMARY KEY,
  "alias_id" INTEGER NOT NULL UNIQUE,
  "alias_name" VARCHAR NOT NULL UNIQUE,
  "joined_at" TIMESTAMP NOT NULL DEFAULT timezone('utc', now()),
  "opt_outed_at" TIMESTAMP
);

CREATE TABLE IF NOT EXISTS "users" (
  "id" SERIAL NOT NULL PRIMARY KEY,
  "alias_id" INTEGER NOT NULL UNIQUE,
  "alias_name" VARCHAR NOT NULL UNIQUE,
  "joined_at" TIMESTAMP NOT NULL DEFAULT timezone('utc', now()),
  "opt_outed_at" TIMESTAMP
);

CREATE TYPE "emote_provider_type" AS ENUM ('twitch', 'better_ttv', 'franker_face_z' ,'seven_tv');

CREATE TABLE IF NOT EXISTS "emotes" (
  "id" SERIAL NOT NULL PRIMARY KEY,
  "alias_id" VARCHAR NOT NULL,
  "alias_type" emote_provider_type NOT NULL,
  "is_global" BOOLEAN NOT NULL DEFAULT FALSE,
  "spotted_at" TIMESTAMP NOT NULL DEFAULT timezone('utc', now())
);

CREATE TABLE IF NOT EXISTS "channel_emotes" (
  "id" SERIAL NOT NULL PRIMARY KEY,
  "emote_id" INTEGER NOT NULL REFERENCES "emotes"("id"),
  "channel_id" INTEGER NOT NULL REFERENCES "channels"("id"),
  "name" VARCHAR NOT NULL,
  "spotted_at" TIMESTAMP NOT NULL DEFAULT timezone('utc', now()),
  "removed_at" TIMESTAMP
);

CREATE TABLE IF NOT EXISTS "emote_usage" (
  "id" SERIAL NOT NULL PRIMARY KEY,
  "emote_id" INTEGER NOT NULL REFERENCES "emotes"("id"),
  "channel_id" INTEGER NOT NULL REFERENCES "channels"("id"),
  "user_id" INTEGER NOT NULL REFERENCES "users"("id"),
  "usage_count" INTEGER NOT NULL DEFAULT 1,
  "first_use_at" TIMESTAMP NOT NULL DEFAULT timezone('utc', now()),
  "last_use_at" TIMESTAMP NOT NULL DEFAULT timezone('utc', now())
);
