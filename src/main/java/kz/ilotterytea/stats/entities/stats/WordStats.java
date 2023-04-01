package kz.ilotterytea.stats.entities.stats;

import jakarta.persistence.*;
import kz.ilotterytea.stats.entities.User;
import kz.ilotterytea.stats.entities.Word;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

/**
 * @author ilotterytea
 * @since 1.0
 */
@Entity
@Table(name = "word_stats")
public class WordStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", insertable = false, updatable = false, nullable = false)
    private Integer userId;
    @Column(name = "word_id", insertable = false, updatable = false, nullable = false)
    private Integer wordId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne
    @JoinColumn(name = "word_id", nullable = false)
    private Word word;

    @Column(name = "used_times", nullable = false)
    private Integer usedTimes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private Date creationTimestamp;

    @UpdateTimestamp
    @Column(name = "last_used_at", nullable = false)
    private Date lastUsageTimestamp;

    public WordStats() {
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

    public Integer getWordId() {
        return wordId;
    }

    public Word getWord() {
        return word;
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

    public void setWord(Word word) {
        this.word = word;
    }

    public void setUsedTimes(Integer usedTimes) {
        this.usedTimes = usedTimes;
    }
}
