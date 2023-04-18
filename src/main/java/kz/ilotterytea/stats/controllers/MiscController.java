package kz.ilotterytea.stats.controllers;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Part;
import io.micronaut.http.annotation.Post;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import kz.ilotterytea.stats.entities.Channel;
import kz.ilotterytea.stats.entities.emotes.EmoteProvider;
import kz.ilotterytea.stats.schemas.Payload;
import kz.ilotterytea.stats.thirdparty.seventv.SevenTVAPIWrapper;
import kz.ilotterytea.stats.thirdparty.seventv.SevenTVWebsocketClient;
import kz.ilotterytea.stats.thirdparty.seventv.schemas.api.Emote;
import kz.ilotterytea.stats.thirdparty.seventv.schemas.api.EmoteSet;
import kz.ilotterytea.stats.thirdparty.seventv.schemas.api.User;
import kz.ilotterytea.stats.twitchbot.TwitchBot;
import kz.ilotterytea.stats.utils.HibernateUtil;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.util.*;

/**
 * @author ilotterytea
 * @since 1.0
 */
@Controller("/api/v1")
public class MiscController {
    @Operation(
            summary = "Listen the channels",
            description = "Creating a channel, synchronizing its 7TV emotes, subscribing it to 7TV EventAPI.",
            tags = "Miscellaneous"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Returns a HashMap in which the key is the provided ID and the value is the result of the listen: false - could not listen because the wrong ID or already exists; true - everything is ok!"
    )
    @Post(
            value = "/listen",
            consumes = MediaType.MULTIPART_FORM_DATA,
            produces = MediaType.APPLICATION_JSON
    )
    public HttpResponse<Payload<HashMap<String, Boolean>>> listenChannelsById(
            @Part("ids") @Parameter(description = "Comma separated Twitch IDs") String ids
    ) {
        Logger logger = LoggerFactory.getLogger(MiscController.class.getSimpleName());
        HashMap<String, Boolean> success = new HashMap<>();
        Session session = HibernateUtil.getSessionFactory().openSession();

        for (String id : ids.split(",")) {
            // Parse the Twitch ID from string:
            int intId;

            try {
                intId = Integer.parseInt(id);
            } catch (NumberFormatException e) {
                success.put(id, false);
                logger.warn("Can't parse the ID from '" + id + "'!");
                continue;
            }

            List<Channel> channels = session.createQuery("from Channel where aliasId = :aliasId", Channel.class)
                    .setParameter("aliasId", intId)
                    .getResultList();

            if (!channels.isEmpty()) {
                success.put(id, false);
                continue;
            }

            List<com.github.twitch4j.helix.domain.User> twitchUsers = TwitchBot.getInstance()
                    .getClient()
                    .getHelix()
                    .getUsers(
                            TwitchBot.getInstance().getCredential().getAccessToken(),
                            Collections.singletonList(id),
                            null
                    )
                    .execute()
                    .getUsers();

            if (twitchUsers.isEmpty()) {
                logger.debug("No users for ID " + id + "!");
                success.put(id, false);
                continue;
            }

            com.github.twitch4j.helix.domain.User twitchUser = twitchUsers.get(0);

            // Get data from 7TV:
            EmoteSet emoteSet = SevenTVAPIWrapper.getEmoteSet("global");
            User user = SevenTVAPIWrapper.getUser(intId);
            Channel channel = new Channel(intId, twitchUser.getLogin());

            session.getTransaction().begin();

            session.persist(channel);

            if (user != null) {
                logger.debug("Retrieved the data from 7TV for user ID " + id + "!");

                for (Emote emote : user.getEmoteSet().getEmotes()) {
                    kz.ilotterytea.stats.entities.emotes.Emote emote1 = new kz.ilotterytea.stats.entities.emotes.Emote(
                            channel,
                            EmoteProvider.SEVENTV,
                            emote.getId(),
                            emote.getName()
                    );

                    channel.addEmote(emote1);
                    session.persist(emote1);
                }

                logger.debug("Assigned " + user.getEmoteSet().getEmotes().size() + " emotes to channel ID " + channel.getId() + "!");
            }

            if (emoteSet != null) {
                for (Emote emote : emoteSet.getEmotes()) {
                    kz.ilotterytea.stats.entities.emotes.Emote emote1 = new kz.ilotterytea.stats.entities.emotes.Emote(
                            channel,
                            EmoteProvider.SEVENTV,
                            emote.getId(),
                            emote.getName()
                    );

                    channel.addEmote(emote1);
                    session.persist(emote1);
                }
            }

            logger.debug("Saved channel ID " + channel.getId() + "!");
            session.getTransaction().commit();

            SevenTVWebsocketClient.getInstance().joinChannel(channel.getAliasId());
        }

        session.close();

        return HttpResponse
                .ok()
                .body(new Payload<>(success));
    }

    @Operation(
            summary = "Opt-out the channels",
            description = "Opt-out the channels. Parting from Twitch chats, which means the emote count will be paused.",
            tags = "Miscellaneous"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Returns a HashMap in which the key is the provided ID and the value is the result of the opt-out: false - could not opt-out because the wrong ID or already opt-outed; true - successfully opt-outed!"
    )
    @Post(
            value = "/optout",
            consumes = MediaType.MULTIPART_FORM_DATA,
            produces = MediaType.APPLICATION_JSON
    )
    public HttpResponse<Payload<HashMap<String, Boolean>>> optOutChannelsById(
            @Part("ids") @Parameter(description = "Comma separated Twitch IDs") String ids
    ) {
        Logger logger = LoggerFactory.getLogger(MiscController.class.getSimpleName());
        HashMap<String, Boolean> success = new HashMap<>();
        Session session = HibernateUtil.getSessionFactory().openSession();

        for (String id : ids.split(",")) {
            // Parse the Twitch ID from string:
            int intId;

            try {
                intId = Integer.parseInt(id);
            } catch (NumberFormatException e) {
                success.put(id, false);
                logger.warn("Can't parse the ID from '" + id + "'!");
                continue;
            }

            List<Channel> channels = session.createQuery("from Channel where aliasId = :aliasId AND optOutTimestamp is null", Channel.class)
                    .setParameter("aliasId", intId)
                    .getResultList();

            if (channels.isEmpty()) {
                success.put(id, false);
                logger.debug("No channel found for ID " + id + " (or it opt outed already)!");
                continue;
            }

            Channel channel = channels.get(0);

            session.getTransaction().begin();
            channel.setOptOutTimestamp(new Date());
            session.getTransaction().commit();
        }

        session.close();

        return HttpResponse
                .ok()
                .body(new Payload<>(success));
    }

    @Operation(
            summary = "Get the health status of the instance",
            tags = "Miscellaneous"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Returns a HashMap<String, Object> with health info."
    )
    @Get(
            value = "/health",
            produces = MediaType.APPLICATION_JSON
    )
    public HttpResponse<Payload<HashMap<String, Object>>> getHealth() {
        HashMap<String, Object> map = new HashMap<>();

        map.put("uptime_ms", ManagementFactory.getRuntimeMXBean().getUptime());

        Runtime rt = Runtime.getRuntime();
        map.put("used_memory_mb", ((rt.totalMemory() - rt.freeMemory()) / 1024.0) / 1024.0);
        map.put("total_memory_mb", ((rt.totalMemory() / 1024.0) / 1024.0));

        return HttpResponse.ok(new Payload<>(map));
    }
}
