package kz.ilotterytea.stats;

import java.io.File;

/**
 * @author ilotterytea
 * @since 1.0
 */
public class SharedConstants {
    public static final String PROPERTIES_PATH = "./config.properties";
    public static final String TARGETS_PATH = "./targets";
    public static final File TARGETS_FILE = new File(TARGETS_PATH);

    public static final String STV_EVENTAPI_WSS = "wss://events.7tv.app/v1/channel-emotes";
    public static final String STV_CHANNEL_EMOTES_URL = "https://api.7tv.app/v2/users/%s/emotes";
    public static final String STV_GLOBAL_EMOTES_URL = "https://api.7tv.app/v2/emotes/global";
}
