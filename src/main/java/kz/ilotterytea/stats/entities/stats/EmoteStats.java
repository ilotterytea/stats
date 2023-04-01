package kz.ilotterytea.stats.entities.stats;

import jakarta.persistence.*;
import kz.ilotterytea.stats.entities.User;
import kz.ilotterytea.stats.entities.emotes.Emote;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

/**
 * @author ilotterytea
 * @since 1.0
 */
@Entity
@Table(name = "emote_stats")
public class EmoteStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", insertable = false, updatable = false, nullable = false)
    private Integer userId;
    @Column(name = "emote_id", insertable = false, updatable = false, nullable = false)
    private Integer emoteId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne
    @JoinColumn(name = "emote_id", nullable = false)
    private Emote emote;

    @Column(name = "used_times", nullable = false)
    private Integer usedTimes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private Date creationTimestamp;

    @UpdateTimestamp
    @Column(name = "last_used_at", nullable = false)
    private Date lastUsageTimestamp;

    public EmoteStats() {
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

    public Integer getEmoteId() {
        return emoteId;
    }

    public Emote getEmote() {
        return emote;
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

    public void setEmote(Emote emote) {
        this.emote = emote;
    }

    public void setUsedTimes(Integer usedTimes) {
        this.usedTimes = usedTimes;
    }
}
