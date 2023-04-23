package kz.ilotterytea.stats.entities.emotes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import kz.ilotterytea.stats.entities.Channel;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

/**
 * @author ilotterytea
 * @since 1.0
 */
@Entity
@Table(name = "emotes")
public class Emote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JsonProperty("channel_id")
    @Column(name = "channel_id", insertable = false, updatable = false, nullable = false)
    private Integer channelId;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "channel_id", nullable = false)
    private Channel channel;

    @JsonProperty("provider_id")
    @Column(name = "provider_id", updatable = false, nullable = false)
    private String providerId;

    @JsonProperty("provider_type")
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "provider_type", updatable = false, nullable = false)
    private EmoteProvider providerType;

    @Column(nullable = false)
    private String name;

    @JsonProperty("used_times")
    @Column(name = "used_times", nullable = false)
    private Integer usedTimes;

    @JsonProperty("is_global")
    @Column(name = "is_global", nullable = false)
    private Boolean isGlobal;

    @JsonProperty("is_deleted")
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @JsonProperty("created_at")
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false, nullable = false)
    private Date creationTimestamp;

    @JsonProperty("updated_at")
    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "refreshed_at")
    private Date refreshedTimestamp;

    @JsonProperty("deleted_at")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "deleted_at")
    private Date deletionTimestamp;

    public Emote(Channel channel, EmoteProvider providerType, String providerId, String name) {
        this.channel = channel;
        this.providerType = providerType;
        this.providerId = providerId;
        this.name = name;
        this.isGlobal = false;
        this.isDeleted = false;
        this.usedTimes = 0;
    }

    public Emote() {}

    public Integer getId() {
        return id;
    }

    public String getProviderId() {
        return providerId;
    }

    public EmoteProvider getProviderType() {
        return providerType;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public Date getCreationTimestamp() {
        return creationTimestamp;
    }

    public Date getRefreshedTimestamp() {
        return refreshedTimestamp;
    }

    public Date getDeletionTimestamp() {
        return deletionTimestamp;
    }

    public void setDeletionTimestamp(Date deletionTimestamp) {
        this.deletionTimestamp = deletionTimestamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getUsedTimes() {
        return usedTimes;
    }

    public void setUsedTimes(Integer usedTimes) {
        this.usedTimes = usedTimes;
    }

    public Boolean getGlobal() {
        return isGlobal;
    }

    public void setGlobal(Boolean global) {
        isGlobal = global;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }
}
