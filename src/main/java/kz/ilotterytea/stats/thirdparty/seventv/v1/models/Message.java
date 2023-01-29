package kz.ilotterytea.stats.thirdparty.seventv.v1.models;

/**
 * SevenTV WebSocket message model.
 * @author ilotterytea
 * @since 1.0
 */
public class Message {
    /** The action. */
    private final String action;
    /** The payload. */
    private final String payload;

    public Message(
            String action,
            String payload
    ) {
        this.action = action;
        this.payload = payload;
    }

    public String getAction() {
        return action;
    }

    public String getPayload() {
        return payload;
    }
}