package kz.ilotterytea.stats.thirdparty.seventv;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import kz.ilotterytea.stats.SharedConstants;
import kz.ilotterytea.stats.entities.Channel;
import kz.ilotterytea.stats.entities.emotes.Emote;
import kz.ilotterytea.stats.entities.emotes.EmoteProvider;
import kz.ilotterytea.stats.thirdparty.seventv.schemas.api.EmoteSet;
import kz.ilotterytea.stats.thirdparty.seventv.schemas.api.SevenTVUser;
import kz.ilotterytea.stats.thirdparty.seventv.schemas.api.User;
import kz.ilotterytea.stats.thirdparty.seventv.schemas.api.UserConnection;
import kz.ilotterytea.stats.thirdparty.seventv.schemas.wss.*;
import kz.ilotterytea.stats.thirdparty.seventv.schemas.wss.emoteset.EmoteSetBody;
import kz.ilotterytea.stats.thirdparty.seventv.schemas.wss.emoteset.EmoteSetBodyObject;
import kz.ilotterytea.stats.utils.HibernateUtil;
import org.hibernate.Session;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * @author ilotterytea
 * @since 1.0
 */
public class SevenTVWebsocketClient extends WebSocketClient {
    private final Logger LOGGER = LoggerFactory.getLogger(SevenTVWebsocketClient.class.getName());
    private String sessionId;
    private boolean tryingToResume = false;

    private static SevenTVWebsocketClient instance;

    public static SevenTVWebsocketClient getInstance() {
        return instance;
    }

    public SevenTVWebsocketClient() throws URISyntaxException {
        super(new URI(SharedConstants.STV_WEBSOCKET_BASE));

        instance = this;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        LOGGER.info("Successfully connected to the 7TV EventAPI: " + handshakedata.getHttpStatus() + " " + handshakedata.getHttpStatusMessage());
    }

    @Override
    public void onMessage(String message) {
        Gson gson = new Gson();
        Payload payload = gson.fromJson(message, Payload.class);

        Session session = HibernateUtil.getSessionFactory().openSession();

        // Handling 'dispatch' events.
        // Here while the changes in the emote sets are being processed.
        if (payload.getOperation() == 0) {
            Payload<PayloadData<EmoteSetBody>> emoteSetPayload = gson.fromJson(message, new TypeToken<Payload<PayloadData<EmoteSetBody>>>(){}.getType());
            EmoteSetBody body = emoteSetPayload.getData().getBody();

            // Getting information about the emote set:
            EmoteSet emoteSet = SevenTVAPIWrapper.getEmoteSet(body.getId());

            if (emoteSet == null) {
                LOGGER.debug("No emotesets for ID " + body.getId() + "! There will be no further processing!");
                return;
            }

            // Getting information about the 7tv user:
            SevenTVUser sevenTVUser = SevenTVAPIWrapper.getSevenTVUser(emoteSet.getOwner().getId());

            if (sevenTVUser == null || sevenTVUser.getConnections().isEmpty()) {
                LOGGER.debug("No SevenTV users for ID " + emoteSet.getOwner().getId() + " (or has no connections)! There will be no further processing!");
                return;
            }

            // Obtaining a Twitch connection only:
            Optional<UserConnection> connection = sevenTVUser.getConnections().stream().filter(p -> p.getPlatform().equals("TWITCH")).findFirst();

            if (connection.isEmpty()) {
                LOGGER.debug("No Twitch connections for SevenTV user ID " + sevenTVUser.getId() + "! There will be no further processing!");
                return;
            }

            // Obtaining the channel from the database:
            List<Channel> channels = session.createQuery("from Channel where aliasName = :aliasName", Channel.class)
                    .setParameter("aliasName", connection.get().getUsername())
                    .getResultList();

            if (channels.isEmpty()) {
                LOGGER.debug("No channel for alias name " + connection.get().getUsername() + "! There will be no further processing!");
                return;
            }

            Channel channel = channels.get(0);

            session.getTransaction().begin();

            // Handling new emotes:
            if (body.getPushed() != null) {
                for (EmoteSetBodyObject object : body.getPushed()) {
                    List<Emote> emotes = session
                            .createQuery("from Emote where providerId = :providerId AND providerType = :providerType AND channel = :channel", Emote.class)
                            .setParameter("providerId", object.getValue().getId())
                            .setParameter("providerType", EmoteProvider.SEVENTV)
                            .setParameter("channel", channel)
                            .getResultList();

                    if (emotes.isEmpty()) {
                        Emote emote = new Emote(channel, EmoteProvider.SEVENTV, object.getValue().getId(), object.getValue().getName());
                        channel.addEmote(emote);
                        session.persist(channel);
                        session.persist(emote);
                    } else {
                        for (Emote emote : emotes) {
                            emote.setName(object.getValue().getName());
                            emote.setDeleted(false);
                            emote.setDeletionTimestamp(null);
                            session.persist(emote);
                        }
                    }
                }
            }

            // Processing of deleted emotes:
            if (body.getPulled() != null) {
                for (EmoteSetBodyObject object : body.getPulled()) {
                    List<Emote> emotes = session
                            .createQuery("from Emote where providerId = :providerId AND providerType = :providerType AND channel = :channel", Emote.class)
                            .setParameter("providerId", object.getOldValue().getId())
                            .setParameter("providerType", EmoteProvider.SEVENTV)
                            .setParameter("channel", channel)
                            .getResultList();

                    if (emotes.isEmpty()) {
                        Emote emote = new Emote(channel, EmoteProvider.SEVENTV, object.getOldValue().getId(), object.getOldValue().getName());
                        channel.addEmote(emote);
                        session.persist(channel);
                        session.persist(emote);
                    } else {
                        for (Emote emote : emotes) {
                            emote.setName(object.getOldValue().getName());
                            emote.setDeleted(true);
                            emote.setDeletionTimestamp(new Date());
                            session.persist(emote);
                        }
                    }
                }
            }

            // Handling of emote update (name update):
            if (body.getUpdated() != null) {
                for (EmoteSetBodyObject object : body.getUpdated()) {
                    List<Emote> emotes = session
                            .createQuery("from Emote where providerId = :providerId AND providerType = :providerType AND channel = :channel", Emote.class)
                            .setParameter("providerId", object.getOldValue().getId())
                            .setParameter("providerType", EmoteProvider.SEVENTV)
                            .setParameter("channel", channel)
                            .getResultList();

                    if (emotes.isEmpty()) {
                        Emote emote = new Emote(channel, EmoteProvider.SEVENTV, object.getValue().getId(), object.getValue().getName());
                        channel.addEmote(emote);
                        session.persist(channel);
                        session.persist(emote);
                    } else {
                        for (Emote emote : emotes) {
                            emote.setName(object.getValue().getName());
                            session.persist(emote);
                        }
                    }
                }
            }

            session.getTransaction().commit();
        }

        // Handling 'hello' events.
        // This event is triggered when you connect to the server (I think).
        // Sending requests to subscribe changes of channel emotes from the database.
        else if (payload.getOperation() == 1) {
            handleHelloEvent(message, session);
        }

        // Handling 'heartbeat' events.
        else if (payload.getOperation() == 2) {
            LOGGER.debug("Received the heartbeat event!");
        }

        // Handling 'acknowledge' events.
        else if (payload.getOperation() == 5) {
            Payload<AcknowledgeData> ackPayload = gson.fromJson(message, new TypeToken<Payload<AcknowledgeData>>(){}.getType());

            String command = ackPayload.getData().getCommand();

            if (Objects.equals(command, "SUBSCRIBE")) {
                LOGGER.debug("Successfully subscribed!");
            } else if (Objects.equals(command, "RESUME") && tryingToResume) {
                Payload<AcknowledgeData<ResumeData>> acknowledgeDataPayload = gson.fromJson(message, new TypeToken<Payload<AcknowledgeData<ResumeData>>>(){}.getType());
                if (acknowledgeDataPayload.getData().getData().getSuccess()) {
                    LOGGER.debug("Successfully resumed the session!");
                    tryingToResume = false;
                } else {
                    LOGGER.debug("Can't resume the session! Maybe, session ID is invalid...");
                    tryingToResume = false;
                    sessionId = null;

                    handleHelloEvent(message, session);
                }
            }
        }

        // Handling other events.
        else {
            LOGGER.debug(String.format(
                    "Received the event (%s), but no handler found for it: %s",
                    payload.getOperation(),
                    message
            ));
        }

        session.close();
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        LOGGER.debug("7TV closed the connection! Reason: " + code + " " + reason + " (Remote: " + remote + ").");

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                LOGGER.debug("Trying to reconnect to 7TV...");
                try {
                    reconnectBlocking();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                if (isOpen()) {
                    LOGGER.debug("Successfully reconnected to 7TV!");
                    Session session = HibernateUtil.getSessionFactory().openSession();
                    if (!resumeSession()) {
                        handleHelloEvent("{\"op\": 2, \"d\": null}", session);
                    }
                    session.close();

                    super.cancel();
                } else {
                    LOGGER.debug("Couldn't reconnect to 7TV!");
                }
            }
        }, 300000, 300000);
    }

    @Override
    public void onError(Exception ex) {
        throw new RuntimeException(ex);
    }

    private boolean resumeSession() {
        if (sessionId == null) {
            LOGGER.debug("Can't resume because sessionId is null!");
            return false;
        }

        HashMap<String, String> map = new HashMap<>();
        map.put("session_id", sessionId);

        Payload<HashMap<String, String>> payload = new Payload<>(
                34,
                map
        );

        super.send(new Gson().toJson(payload));

        tryingToResume = true;
        return true;
    }

    private void handleHelloEvent(String message, Session session) {
        Gson gson = new Gson();

        Payload payload = gson.fromJson(message, Payload.class);

        if (payload.getOperation() == 1) {
            Payload<HelloData> helloDataPayload = gson.fromJson(message, new TypeToken<Payload<HelloData>>(){}.getType());
            sessionId = helloDataPayload.getData().getSessionId();
        }

        List<Channel> channels = session.createQuery("from Channel where optOutTimestamp is null", Channel.class).getResultList();

        for (Channel channel : channels) {
            User stvUser = SevenTVAPIWrapper.getUser(channel.getAliasId());

            if (stvUser != null) {
                String json = new Gson().toJson(
                        new Payload<>(
                                35,
                                new PayloadData<>(
                                        "emote_set.update",
                                        new ConditionData(stvUser.getEmoteSet().getId())
                                )
                        )
                );

                super.send(json);
            }
        }
    }
}
