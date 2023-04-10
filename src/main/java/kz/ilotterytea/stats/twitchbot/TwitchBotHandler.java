package kz.ilotterytea.stats.twitchbot;

import com.github.twitch4j.chat.events.channel.IRCMessageEvent;
import jakarta.persistence.Query;
import kz.ilotterytea.stats.entities.Channel;
import kz.ilotterytea.stats.entities.emotes.Emote;
import kz.ilotterytea.stats.utils.HibernateUtil;
import org.hibernate.Session;

import java.util.List;

/**
 * @author ilotterytea
 * @since 1.0
 */
public class TwitchBotHandler {
    public static void ircMessageEvent(IRCMessageEvent event) {
        if (event.getMessage().isEmpty()) {
            return;
        }

        Session session = HibernateUtil.getSessionFactory().openSession();
        session.getTransaction().begin();

        // Get event channel from the database:
        Channel channel;
        List<Channel> channelList = session.createQuery("from Channel where aliasId = :aliasId", Channel.class)
                .setParameter("aliasId", event.getChannel().getId()).getResultList();

        // Create a new channel if it doesn't exist.
        // (This doesn't happen, but is best left to avoid unexpected NPEs)
        if (channelList.isEmpty()) {
            channel = new Channel(Integer.parseInt(event.getChannel().getId()), event.getChannel().getName());

            session.persist(channel);
        } else {
            channel = channelList.get(0);
            channel.setAliasName(event.getChannel().getName());
            session.persist(channel);
        }

        String[] words = event.getMessage().get().split(" ");

        for (String word : words) {
            // Obtaining emotes.
            Query emoteQuery = session.createQuery("from Emote where channel = :channel AND name = :name", Emote.class);
            emoteQuery.setParameter("channel", channel);
            emoteQuery.setParameter("name", word);

            List<Emote> emotes = emoteQuery.getResultList();

            for (Emote emote : emotes) {
                emote.setUsedTimes(emote.getUsedTimes() + 1);
                session.persist(emote);
            }
        }

        session.getTransaction().commit();
        session.close();
    }
}
