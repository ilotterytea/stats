package kz.ilotterytea.stats.models;

import kz.ilotterytea.stats.models.emotes.Emote;
import kz.ilotterytea.stats.models.emotes.Provider;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ilotterytea
 * @since 1.0
 */
public class Target {
    private final String aliasId;
    private final Map<Provider, Map<String, Emote>> emotes;

    private final Map<String, Integer> mentionedUsers;
    private final Map<String, Integer> usedCommands;
    private long totalMessagesCount;

    public Target(
            String aliasId,
            Map<Provider, Map<String, Emote>> emotes,
            Map<String, Integer> mentionedUsers,
            Map<String, Integer> usedCommands,
            long totalMessagesCount
    ) {
        this.aliasId = aliasId;
        this.emotes = emotes;
        this.mentionedUsers = mentionedUsers;
        this.usedCommands = usedCommands;
        this.totalMessagesCount = totalMessagesCount;
    }

    public Target(String aliasId) {
        this.aliasId = aliasId;

        this.emotes = new HashMap<>();
        this.mentionedUsers = new HashMap<>();
        this.usedCommands = new HashMap<>();
        this.totalMessagesCount = 0;
    }

    public String getAliasId() {
        return aliasId;
    }

    public Map<Provider, Map<String, Emote>> getEmotes() {
        return emotes;
    }

    public Map<String, Integer> getMentionedUsers() {
        return mentionedUsers;
    }

    public Map<String, Integer> getUsedCommands() {
        return usedCommands;
    }

    public long getTotalMessagesCount() {
        return totalMessagesCount;
    }

    public void setTotalMessagesCount(long totalMessagesCount) {
        this.totalMessagesCount = totalMessagesCount;
    }
}
