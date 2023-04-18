package kz.ilotterytea.stats.controllers;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import kz.ilotterytea.stats.entities.Channel;
import kz.ilotterytea.stats.entities.emotes.Emote;
import kz.ilotterytea.stats.schemas.Payload;
import kz.ilotterytea.stats.utils.HibernateUtil;
import org.hibernate.Session;

import java.util.List;
import java.util.Set;

/**
 * @author ilotterytea
 * @version 1.0
 */
@Controller("/api/v1/channel")
public class ChannelController {
    @Operation(
            summary = "Get channel emotes",
            description = "Get the emotes that are tracked by the instance.",
            tags = "Channel"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Returns a list with emotes."
    )
    @ApiResponse(
            responseCode = "404",
            description = "The channel does not exist or opt outed."
    )
    @Get(
            value = "/{id}/emotes",
            produces = MediaType.APPLICATION_JSON
    )
    public HttpResponse<Payload<Set<Emote>>> getChannelEmotes(String id) {
        Session session = HibernateUtil.getSessionFactory().openSession();

        List<Channel> channels = session.createQuery("from Channel where aliasId = :aliasId and optOutTimestamp is null", Channel.class)
                .setParameter("aliasId", id)
                .getResultList();

        if (channels.isEmpty()) {
            return HttpResponse.notFound(
                    new Payload<>(
                            404,
                            "No users for ID " + id,
                            null
                    )
            );
        }

        Channel channel = channels.get(0);

        session.close();

        return HttpResponse
                .ok(
                        new Payload<>(
                                channel.getEmotes()
                        )
                );
    }

    @Operation(
            summary = "Get channel internal info",
            description = "Get the internal info about the channel.",
            tags = "Channel"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Returns the channel."
    )
    @ApiResponse(
            responseCode = "404",
            description = "The channel does not exist or opt outed."
    )
    @Get(
            value = "/{id}",
            produces = MediaType.APPLICATION_JSON
    )
    public HttpResponse<Payload<Channel>> getChannel(String id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Channel> channels = session.createQuery("from Channel where aliasId = :aliasId", Channel.class)
                .setParameter("aliasId", id)
                .getResultList();

        if (channels.isEmpty()) {
            return HttpResponse.notFound(new Payload<>(
                    404,
                    "No user for ID " + id,
                    null
            ));
        }

        Channel channel = channels.get(0);

        session.close();

        return HttpResponse.ok(new Payload<>(channel));
    }
}
