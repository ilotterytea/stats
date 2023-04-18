package kz.ilotterytea.stats.twitchbot;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;
import com.github.twitch4j.helix.domain.User;
import com.github.twitch4j.helix.domain.UserList;
import kz.ilotterytea.stats.SharedConstants;
import kz.ilotterytea.stats.entities.Channel;
import kz.ilotterytea.stats.utils.HibernateUtil;
import org.hibernate.Session;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ilotterytea
 * @since 1.0
 */
public class TwitchBot {
    private static TwitchBot instance;
    private TwitchClient client;
    private OAuth2Credential credential;

    public TwitchClient getClient() {
        return client;
    }

    public OAuth2Credential getCredential() {
        return credential;
    }

    public static TwitchBot getInstance() {
        return instance;
    }

    public TwitchBot() {
        instance = this;
    }

    public void run() {
        credential = new OAuth2Credential("twitch", SharedConstants.BOT_OAUTH2_TOKEN);

        client = TwitchClientBuilder.builder()
                .withEnableHelix(true)
                .withChatAccount(credential)
                .withEnableChat(true)
                .build();

        client.getChat().connect();

        Session session = HibernateUtil.getSessionFactory().openSession();

        // Getting channels whose chats to join.
        List<Channel> channels = session.createQuery("from Channel where optOutTimestamp is null", Channel.class).getResultList();

        if (!channels.isEmpty()) {
            // Getting info from Twitch about these channels.
            // This is needed in case a channel has changed its username while the application was turned off.
            UserList parsedChannels = client.getHelix()
                    .getUsers(credential.getAccessToken(), channels.stream().map(p -> p.getAliasId().toString()).collect(Collectors.toList()), null)
                    .execute();

            if (!parsedChannels.getUsers().isEmpty()) {
                List<User> users = parsedChannels.getUsers();

                for (User user : users) {
                    client.getChat().joinChannel(user.getLogin());
                }
            }
        }

        session.close();

        client.getEventManager().onEvent(IRCMessageEvent.class, TwitchBotHandler::ircMessageEvent);
    }
}
