package kz.ilotterytea.stats;

import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.info.*;
import kz.ilotterytea.stats.entities.Channel;
import kz.ilotterytea.stats.entities.emotes.EmoteProvider;
import kz.ilotterytea.stats.thirdparty.seventv.SevenTVAPIWrapper;
import kz.ilotterytea.stats.thirdparty.seventv.SevenTVWebsocketClient;
import kz.ilotterytea.stats.thirdparty.seventv.schemas.api.Emote;
import kz.ilotterytea.stats.thirdparty.seventv.schemas.api.User;
import kz.ilotterytea.stats.twitchbot.TwitchBot;
import kz.ilotterytea.stats.utils.HibernateUtil;
import org.hibernate.Session;

import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@OpenAPIDefinition(
    info = @Info(
            title = "stats",
            version = "1.0"
    )
)
public class Application {

    public static void main(String[] args) {
        // Sync emotes:
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Channel> channels = session.createQuery("from Channel where optOutTimestamp is null", Channel.class).getResultList();

        for (Channel channel : channels) {
            User user = SevenTVAPIWrapper.getUser(channel.getAliasId());

            if (user == null || user.getEmoteSet() == null) {
                continue;
            }

            session.getTransaction().begin();

            // Sync new emotes:
            for (Emote emote : user.getEmoteSet().getEmotes()) {
                Optional<kz.ilotterytea.stats.entities.emotes.Emote> optionalEmote = channel
                        .getEmotes()
                        .stream().filter(
                                p -> p.getProviderType().equals(EmoteProvider.SEVENTV) &&
                                        p.getProviderId().equals(emote.getId())
                        )
                        .findFirst();

                if (optionalEmote.isEmpty()) {
                    kz.ilotterytea.stats.entities.emotes.Emote newEmote = new kz.ilotterytea.stats.entities.emotes.Emote(
                            channel,
                            EmoteProvider.SEVENTV,
                            emote.getId(),
                            emote.getName()
                    );

                    channel.addEmote(newEmote);
                    session.persist(channel);
                    session.persist(newEmote);
                } else {
                    kz.ilotterytea.stats.entities.emotes.Emote unwrapedEmote = optionalEmote.get();

                    unwrapedEmote.setName(emote.getName());
                    unwrapedEmote.setDeletionTimestamp(null);
                    unwrapedEmote.setDeleted(false);
                    session.persist(unwrapedEmote);
                }
            }

            // Sync removed emotes:
            for (kz.ilotterytea.stats.entities.emotes.Emote emote : channel
                    .getEmotes()
                    .stream()
                    .filter(p -> p.getProviderType().equals(EmoteProvider.SEVENTV) && p.getDeletionTimestamp() == null)
                    .collect(Collectors.toSet())
            ) {
                Optional<Emote> optionalEmote = user
                        .getEmoteSet()
                        .getEmotes()
                        .stream().filter(
                                p -> p.getId().equals(emote.getProviderId())
                        )
                        .findFirst();

                if (optionalEmote.isEmpty()) {
                    emote.setDeleted(true);
                    emote.setDeletionTimestamp(new Date());
                    session.persist(emote);
                }
            }

            session.getTransaction().commit();
        }

        session.close();

        try {
            SevenTVWebsocketClient client = new SevenTVWebsocketClient();
            client.connectBlocking();
        } catch (InterruptedException | URISyntaxException e) {
            throw new RuntimeException(e);
        }

        Micronaut.run(Application.class, args);

        TwitchBot bot = new TwitchBot();
        bot.run();
    }
}