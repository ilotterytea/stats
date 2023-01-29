package kz.ilotterytea.stats;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;
import com.github.twitch4j.helix.domain.User;
import kz.ilotterytea.stats.models.Target;
import kz.ilotterytea.stats.providers.twitch.TTVMessageHandlers;
import kz.ilotterytea.stats.storage.TargetController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ilotterytea
 * @since 1.0
 */
public class Server {
    private final Logger log = LoggerFactory.getLogger(Server.class);

    private static Server instance;
    public static Server getInstance() { return instance; }

    private Properties properties;
    public Properties getProperties() { return properties; }

    private TwitchClient twitchClient;
    public TwitchClient getTwitchClient() { return twitchClient; }

    public OAuth2Credential twitchCredential;
    public OAuth2Credential getTwitchCredential() { return twitchCredential; }

    private TargetController targetController;
    public TargetController getTargetController() { return targetController; }

    private Map<String, String> targetIds;
    public Map<String, String> getTargetIds() { return targetIds; }

    public Server() {
        instance = this;
    }

    public void init() {
        properties = new Properties();

        // Load properties:
        if (new File(SharedConstants.PROPERTIES_PATH).exists()) {
            try (Reader reader = new FileReader(SharedConstants.PROPERTIES_PATH)) {
                properties.load(reader);
                log.debug("The properties file loaded! Total keys: " + properties.keySet().size());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // Targets:
        if (!SharedConstants.TARGETS_FILE.exists()) {
            SharedConstants.TARGETS_FILE.mkdirs();
        }
        targetController = new TargetController(SharedConstants.TARGETS_PATH);

        if (
                !Objects.equals(properties.getProperty("TTV_OAUTH2_TOKEN", null), null) &&
                        !Objects.equals(properties.getProperty("TTV_CLIENT_ID", null), null) &&
                        !Objects.equals(properties.getProperty("TTV_ACCESS_TOKEN", null), null)
        ) {
            twitchCredential = new OAuth2Credential("twitch", properties.getProperty("TTV_OAUTH2_TOKEN"));
            twitchClient = TwitchClientBuilder.builder()
                    .withClientId(properties.getProperty("TTV_CLIENT_ID"))
                    .withChatAccount(twitchCredential)
                    .withEnableHelix(true)
                    .withEnableChat(true)
                    .build();

            twitchClient.getChat().connect();

            List<User> helixTargets = new ArrayList<>();

            helixTargets.addAll(
                    twitchClient.getHelix().getUsers(
                            properties.getProperty("TTV_ACCESS_TOKEN"),
                            targetController.getTargets().values()
                                    .stream().map(Target::getAliasId)
                                    .collect(Collectors.toList()),
                            null
                    ).execute().getUsers()
            );

            for (User user : helixTargets) {
                twitchClient.getChat().joinChannel(user.getLogin());

                targetIds.put(user.getLogin(), user.getId());
            }

            twitchClient.getEventManager().onEvent(IRCMessageEvent.class, TTVMessageHandlers::ircMessageEvent);
        }


    }

    public void dispose() {
        if (twitchClient != null) {
            twitchClient.close();
        }

        targetController.save();
    }
}