package kz.ilotterytea.stats.entities;

import jakarta.persistence.*;
import kz.ilotterytea.stats.entities.stats.MentionStats;
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
@Table(name = "mentions")
public class Mention {
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

    @OneToMany(mappedBy = "mention", cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    private Set<MentionStats> mentionStats;

    public Mention(String name) {
        this.name = name;
        this.usedTimes = 0;
        this.mentionStats = new HashSet<>();
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

    public Set<MentionStats> getMentionStats() {
        return mentionStats;
    }

    public void setMentionStats(Set<MentionStats> mentionStats) {
        for (MentionStats stats : mentionStats) {
            stats.setMention(this);
        }
        this.mentionStats = mentionStats;
    }

    public boolean addMentionStats(MentionStats mentionStats) {
        mentionStats.setMention(this);
        return this.mentionStats.add(mentionStats);
    }

    public boolean removeMentionStats(MentionStats mentionStats) {
        return this.mentionStats.remove(mentionStats);
    }
}
