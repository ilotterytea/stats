package kz.ilotterytea.stats.entities.stats;

import jakarta.persistence.*;
import kz.ilotterytea.stats.entities.User;
import kz.ilotterytea.stats.entities.Hashtag;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

/**
 * @author ilotterytea
 * @since 1.0
 */
@Entity
@Table(name = "hashtag_stats")
public class HashtagStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", insertable = false, updatable = false, nullable = false)
    private Integer userId;
    @Column(name = "hashtag_id", insertable = false, updatable = false, nullable = false)
    private Integer hashtagId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne
    @JoinColumn(name = "hashtag_id", nullable = false)
    private Hashtag hashtag;

    @Column(name = "used_times", nullable = false)
    private Integer usedTimes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private Date creationTimestamp;

    @UpdateTimestamp
    @Column(name = "last_used_at", nullable = false)
    private Date lastUsageTimestamp;

    public HashtagStats() {
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

    public Integer getHashtagId() {
        return hashtagId;
    }

    public Hashtag getHashtag() {
        return hashtag;
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

    public void setHashtag(Hashtag hashtag) {
        this.hashtag = hashtag;
    }

    public void setUsedTimes(Integer usedTimes) {
        this.usedTimes = usedTimes;
    }
}
