package kz.ilotterytea.stats.thirdparty.seventv.schemas.api;

import java.util.ArrayList;

/**
 * @author ilotterytea
 * @since 1.0
 */
public class EmoteSet {
    private String id;
    private String name;
    private ArrayList<Emote> emotes;

    public EmoteSet() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Emote> getEmotes() {
        return emotes;
    }

    public void setEmotes(ArrayList<Emote> emotes) {
        this.emotes = emotes;
    }
}
