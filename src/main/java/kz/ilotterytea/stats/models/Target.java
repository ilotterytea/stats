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

    private final Map<String, Map<String, Integer>> mentionedUsersHistory;
    private final Map<String, Map<String, Integer>> usedCommandsHistory;
    private final Map<String, Integer> messageCountHistory;

    private final Map<String, Boolean> activeHistory;

    public Target(
            String aliasId,
            Map<Provider, Map<String, Emote>> emotes,
            Map<String, Map<String, Integer>> mentionedUsersHistory,
            Map<String, Map<String, Integer>> usedCommandsHistory,
            Map<String, Integer> messageCountHistory,
            Map<String, Boolean> activeHistory
    ) {
        this.aliasId = aliasId;
        this.emotes = emotes;
        this.mentionedUsersHistory = mentionedUsersHistory;
        this.usedCommandsHistory = usedCommandsHistory;
        this.messageCountHistory = messageCountHistory;
        this.activeHistory = activeHistory;
    }

    public Target(String aliasId) {
        this.aliasId = aliasId;

        this.emotes = new HashMap<>();
        this.messageCountHistory = new HashMap<>();
        this.usedCommandsHistory = new HashMap<>();
        this.mentionedUsersHistory = new HashMap<>();
        this.activeHistory = new HashMap<>();
    }

    public String getAliasId() {
        return aliasId;
    }

    public Map<Provider, Map<String, Emote>> getEmotes() {
        return emotes;
    }

    public Map<String, Map<String, Integer>> getMentionedUsersHistory() {
        return mentionedUsersHistory;
    }

    public Map<String, Map<String, Integer>> getUsedCommandsHistory() {
        return usedCommandsHistory;
    }

    public Map<String, Integer> getMessageCountHistory() {
        return messageCountHistory;
    }

    public Map<String, Boolean> getActiveHistory() {
        return activeHistory;
    }
}
