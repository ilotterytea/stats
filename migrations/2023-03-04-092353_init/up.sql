-- Your SQL goes here
CREATE TABLE "emotes" (
	"id"	INTEGER NOT NULL UNIQUE,
	"provider_id"	TEXT NOT NULL,
	"provider"	INTEGER NOT NULL,
	"channel_id"	INTEGER NOT NULL,
	"name"	TEXT NOT NULL,
	"used_times"	INTEGER NOT NULL DEFAULT 0,
	"is_deleted"	INTEGER NOT NULL DEFAULT 0,
	"is_global"	INTEGER NOT NULL DEFAULT 0,
	"creation_timestamp" INTEGER NOT NULL,
	"last_modified_timestamp" INTEGER NOT NULL,
	PRIMARY KEY("id" AUTOINCREMENT)
);

CREATE TABLE "name_history" (
	"id"	INTEGER NOT NULL UNIQUE,
	"emote_id"	INTEGER NOT NULL,
	"previous_name"	TEXT NOT NULL,
	"name"	TEXT NOT NULL,
	"creation_timestamp"	INTEGER NOT NULL,
	PRIMARY KEY("id" AUTOINCREMENT)
);

CREATE TABLE "channels" (
	"id"	INTEGER NOT NULL UNIQUE,
	"alias_id"	TEXT NOT NULL,
	"platform_id"	INTEGER NOT NULL,
	"creation_timestamp"	INTEGER NOT NULL,
	"last_modified_timestamp"	INTEGER NOT NULL,
	PRIMARY KEY("id" AUTOINCREMENT)
);

CREATE TABLE "stats" (
	"id"	INTEGER NOT NULL UNIQUE,
	"channel_id"	INTEGER NOT NULL,
	"user_id"	INTEGER NOT NULL,
	"chat_lines"	INTEGER NOT NULL DEFAULT 0,
	PRIMARY KEY("id" AUTOINCREMENT)
);

CREATE TABLE "users" (
	"id"	INTEGER NOT NULL UNIQUE,
	"alias_id"	TEXT NOT NULL,
	"platform_id"	INTEGER NOT NULL,
	PRIMARY KEY("id" AUTOINCREMENT)
);
