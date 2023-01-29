package kz.ilotterytea.stats.models.emotes;

/**
 * @author ilotterytea
 * @since 1.0
 */
public class Emote {
    private final Provider provider;
    private final String providerId;
    private String providerName;
    private int count;
    private boolean isGlobal;
    private boolean isDeleted;

    public Emote(
            Provider provider,
            String providerId,
            String providerName,
            int count,
            boolean isGlobal,
            boolean isDeleted
    ) {
        this.provider = provider;
        this.providerId = providerId;
        this.providerName = providerName;
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
