package kz.ilotterytea.stats.thirdparty.seventv.v1;

import com.google.gson.Gson;
import kz.ilotterytea.stats.Server;
import kz.ilotterytea.stats.SharedConstants;

import kz.ilotterytea.stats.models.Target;
import kz.ilotterytea.stats.models.emotes.Emote;
import kz.ilotterytea.stats.models.emotes.Provider;
import kz.ilotterytea.stats.thirdparty.seventv.v1.models.EmoteEventUpdate;
import kz.ilotterytea.stats.thirdparty.seventv.v1.models.Message;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 7TV WebSocket client.
 * @author ilotterytea
 * @since 1.0
 */
public class SevenTVWebsocketClient extends WebSocketClient {
    private final Logger LOG = LoggerFactory.getLogger(SevenTVWebsocketClient.class);

    public SevenTVWebsocketClient() throws URISyntaxException {
        super(new URI(SharedConstants.STV_EVENTAPI_WSS));
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        LOG.debug(
                String.format(
                        "Connected to the 7TV EventAPI! (%s -> %s %s)",
                        SharedConstants.STV_EVENTAPI_WSS,
                        handshakedata.getHttpStatus(),
                        handshakedata.getHttpStatusMessage()
                )
        );
    }

    @Override
    public void onMessage(String message) {
        Server server = Server.getInstance();
        Message msg = new Gson().fromJson(message, Message.class);

        if (Objects.equals(msg.getAction(), "update")) {
            EmoteEventUpdate update = new Gson().fromJson(
                    msg.getPayload(),
                    EmoteEventUpdate.class
            );

            Target target = server.getTargetController().get(
                    server.getTargetIds().getOrDefault(update.getChannel(), null)
            );

            if (target == null) {
                LOG.debug("Target Name " + update.getChannel() + " does not exist in system!");
                return;
            }

            Map<String, Emote> emotes = target.getEmotes().get(Provider.SEVENTV);

            switch (update.getAction()) {
                case "ADD": {
                    if (emotes.containsKey(update.getEmoteId())) {
                        if (emotes.get(update.getEmoteId()).isDeleted()) {
                            emotes.get(update.getEmoteId()).setDeleted(false);
                        }

                        LOG.debug("The Emote ID " + update.getEmoteId() + " already exist! Updated the deletion status (" + emotes.get(update.getEmoteId()).isDeleted() + ")!");
                        break;
                    }

                    emotes.put(
                            update.getEmoteId(),
                            new Emote(
                                    Provider.SEVENTV,
                                    update.getEmoteId(),
                                    update.getName(),
                                    0,
                                    false,
                                    false
                            )
                    );
                    LOG.debug("Added a new Emote ID " + update.getEmoteId() + " for Target ID " + server.getTargetIds().get(update.getChannel()) + "!");
                    break;
                }
                case "REMOVE": {
                    if (emotes.containsKey(update.getEmoteId())) {
                        emotes.get(update.getEmoteId()).setDeleted(true);
                        LOG.debug("Flagged the Emote ID " + update.getEmoteId() + " as deleted for Target ID " + server.getTargetIds().get(update.getChannel()) + "!");
                    }
                    break;
                }
                case "UPDATE": {
                    if (emotes.containsKey(update.getEmoteId())) {
                        emotes.get(update.getEmoteId()).setProviderName(update.getName());
                        LOG.debug("Updated the name for Emote ID " + update.getEmoteId() + " for Target ID " + server.getTargetIds().get(update.getChannel()) + "!");
                    }
                    break;
                }
                default: break;
            }

            server.getTargetController().get(
                    target.getAliasId()
            ).getEmotes().put(Provider.SEVENTV, emotes);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        LOG.error(
                String.format(
                        "Connection to the 7TV EventAPI has been closed! Reason: %s %s (%s)",
                        code,
                        reason,
                        (remote) ? "by the remote host" : "by the client"
                )
        );

        new Timer().schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            SevenTVWebsocketClient.super.reconnectBlocking();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                10000,
                10000
        );
    }

    @Override
    public void onError(Exception ex) {
        throw new RuntimeException(ex);
    }
}
