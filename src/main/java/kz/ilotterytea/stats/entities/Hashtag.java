package kz.ilotterytea.stats.entities;

import jakarta.persistence.*;
import kz.ilotterytea.stats.entities.stats.HashtagStats;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author ilotterytea
 * @since 1.0
 */
@Entity
@Table(name = "hashtags")
public class Hashtag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "channel_id", insertable = false, updatable = false, nullable = false)
    private Integer channelId;

    @ManyToOne
    @JoinColumn(name = "channel_id")
    private Channel channel;

    @Column(updatable = false, nullable = false)
    private String name;

    @Column(name = "used_times", nullable = false)
    private Integer usedTimes;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false, nullable = false)
    private Date creationTimestamp;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_used_at", nullable = false)
    private Date lastUsageTimestamp;

    @OneToMany(mappedBy = "hashtag", cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    private Set<HashtagStats> hashtagStats;

    public Hashtag(String name) {
        this.name = name;
        this.usedTimes = 0;
        this.hashtagStats = new HashSet<>();
    }

    public Integer getId() {
        return id;
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

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public void setUsedTimes(Integer usedTimes) {
        this.usedTimes = usedTimes;
    }

    public Set<HashtagStats> getHashtagStats() {
        return hashtagStats;
    }

    public void setHashtagStats(Set<HashtagStats> hashtagStats) {
        for (HashtagStats stats : hashtagStats) {
            stats.setHashtag(this);
        }
        this.hashtagStats = hashtagStats;
    }

    public boolean addHashtagStats(HashtagStats hashtagStats) {
        hashtagStats.setHashtag(this);
        return this.hashtagStats.add(hashtagStats);
    }

    public boolean removeHashtagStats(HashtagStats hashtagStats) {
        return this.hashtagStats.remove(hashtagStats);
    }
}
