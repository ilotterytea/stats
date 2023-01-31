package kz.ilotterytea.stats.models.emotes;

import java.util.ArrayList;
import java.util.Map;

/**
 * @author ilotterytea
 * @since 1.0
 */
public class Emote {
    private final Provider provider;
    private final String providerId;
    private final Map<String, ArrayList<String>> providerNameHistory;
    private String providerName;
    private final Map<String, Integer> countHistory;
    private int count;
    private boolean isGlobal;
    private boolean isDeleted;

    public Emote(
            Provider provider,
            String providerId,
            Map<String, ArrayList<String>> providerNameHistory,
            String providerName,
            Map<String, Integer> countHistory,
            int count,
            boolean isGlobal,
            boolean isDeleted
    ) {
        this.provider = provider;
        this.providerId = providerId;
        this.providerNameHistory = providerNameHistory;
        this.providerName = providerName;
        this.countHistory = countHistory;
        this.count = count;
        this.isGlobal = isGlobal;
        this.isDeleted = isDeleted;
    }

    public Provider getProvider() {
        return provider;
    }

    public String getProviderId() {
        return providerId;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public Map<String, Integer> getCountHistory() {
        return countHistory;
    }

    public Map<String, ArrayList<String>> getProviderNameHistory() {
        return providerNameHistory;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isGlobal() {
        return isGlobal;
    }

    public void setGlobal(boolean global) {
        isGlobal = global;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}
