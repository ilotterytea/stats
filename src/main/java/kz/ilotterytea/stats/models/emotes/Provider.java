package kz.ilotterytea.stats.models.emotes;

/**
 * @author ilotterytea
 * @since 1.0
 */
public enum Provider {
    TWITCH(0),
    FRANKERFACEZ(1),
    BETTERTTV(2),
    SEVENTV(3);

    final int id;
    Provider(int id) { this.id = id; }
    public int getId() { return id; }
}
