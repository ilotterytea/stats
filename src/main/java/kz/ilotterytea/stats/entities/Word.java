package kz.ilotterytea.stats.entities;

import jakarta.persistence.*;
import kz.ilotterytea.stats.entities.stats.WordStats;
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
@Table(name = "words")
public class Word {
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

    @OneToMany(mappedBy = "word", cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    private Set<WordStats> wordStats;

    public Word(String name) {
        this.name = name;
        this.usedTimes = 0;
        this.wordStats = new HashSet<>();
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

    public Set<WordStats> getWordStats() {
        return wordStats;
    }

    public void setWordStats(Set<WordStats> wordStats) {
        for (WordStats stats : wordStats) {
            stats.setWord(this);
        }
        this.wordStats = wordStats;
    }

    public boolean addWordStats(WordStats wordStats) {
        wordStats.setWord(this);
        return this.wordStats.add(wordStats);
    }

    public boolean removeWordStats(WordStats wordStats) {
        return this.wordStats.remove(wordStats);
    }
}
