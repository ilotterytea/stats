package kz.ilotterytea.stats.entities.emotes;

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

    @Column(name = "provider_id", updatable = false, nullable = false)
    private String providerId;

    @Enumerated(EnumType.ORDINAL)
    @Column(updatable = false, nullable = false)
    private EmoteProvider provider;

    @Column(name = "channel_id", insertable = false, updatable = false, nullable = false)
    private Integer channelId;

    @ManyToOne
    @JoinColumn(name = "channel_id")
    private Channel channel;

    @Column(nullable = false)
    private String name;

    @Column(name = "used_times", nullable = false)
    private Integer usedTimes;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @Column(name = "is_global", nullable = false)
    private Boolean isGlobal;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false, nullable = false)
    private Date creationTimestamp;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "deleted_at")
    private Date deletionTimestamp;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updateTimestamp;

    public Emote(String providerId, EmoteProvider provider, String name) {
        this.providerId = providerId;
        this.provider = provider;
        this.name = name;
        this.usedTimes = 0;
        this.isDeleted = false;
        this.isGlobal = false;
    }

    public Integer getId() {
        return id;
    }

    public String getProviderId() {
        return providerId;
    }

    public EmoteProvider getProvider() {
        return provider;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public Channel getChannel() {
        return channel;
    }

    public String getName() {
        return name;
    }

    public Integer getUsedTimes() {
        return usedTimes;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public Boolean getGlobal() {
        return isGlobal;
    }

    public Date getCreationTimestamp() {
        return creationTimestamp;
    }

    public Date getDeletionTimestamp() {
        return deletionTimestamp;
    }

    public Date getUpdateTimestamp() {
        return updateTimestamp;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUsedTimes(Integer usedTimes) {
        this.usedTimes = usedTimes;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public void setGlobal(Boolean global) {
        isGlobal = global;
    }

    public void setDeletionTimestamp(Date deletionTimestamp) {
        this.deletionTimestamp = deletionTimestamp;
    }
}
