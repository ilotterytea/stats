package kz.ilotterytea.stats;

public class SharedConstants {
    public static final String BOT_CLIENT_ID = System.getProperty("twitch_client_id");
    public static final String BOT_ACCESS_TOKEN = System.getProperty("twitch_access_token");
    public static final String BOT_OAUTH2_TOKEN = System.getProperty("twitch_oauth2_token");

    public static final String STV_WEBSOCKET_BASE = "wss://events.7tv.io/v3";
    public static final String STV_API_BASE = "https://7tv.io/v3";
    public static final String STV_API_USER_ENDPOINT = STV_API_BASE + "/users/twitch/%s";
    public static final String STV_API_SEVENTV_USER_ENDPOINT = STV_API_BASE + "/users/%s";
    public static final String STV_API_EMOTESET_ENDPOINT = STV_API_BASE + "/emote-sets/%s";
}