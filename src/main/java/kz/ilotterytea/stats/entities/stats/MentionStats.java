package kz.ilotterytea.stats.entities.stats;

import jakarta.persistence.*;
import kz.ilotterytea.stats.entities.Mention;
import kz.ilotterytea.stats.entities.User;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.Set;

/**
 * @author ilotterytea
 * @since 1.0
 */
@Entity
@Table(name = "mention_stats")
public class MentionStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", insertable = false, updatable = false, nullable = false)
    private Integer userId;
    @Column(name = "mention_id", insertable = false, updatable = false, nullable = false)
    private Integer mentionId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne
    @JoinColumn(name = "mention_id", nullable = false)
    private Mention mention;

    @Column(name = "used_times", nullable = false)
    private Integer usedTimes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private Date creationTimestamp;

    @UpdateTimestamp
    @Column(name = "last_used_at", nullable = false)
    private Date lastUsageTimestamp;

    public MentionStats() {
        this.usedTimes = 0;
    }

    public Integer getId() {
        return id;
    }

    public Integer getUserId() {
        return userId;
    }

    public User getUser() {
        return user;
    }

    public Integer getMentionId() {
        return mentionId;
    }

    public Mention getMention() {
        return mention;
    }

    public Integer getUsedTimes() {
        return usedTimes;
    }

    public Date getCreationTimestamp() {
        return creationTimestamp;
    }

    public Date getLastUsageTimestamp() {
        return lastUsageTimestamp;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setMention(Mention mention) {
        this.mention = mention;
    }

    public void setUsedTimes(Integer usedTimes) {
        this.usedTimes = usedTimes;
    }
}
