package kz.ilotterytea.stats.entities;

import jakarta.persistence.*;
import kz.ilotterytea.stats.entities.stats.CommandStats;
import kz.ilotterytea.stats.entities.stats.HashtagStats;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author ilotterytea
 * @since 1.0
 */
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "alias_id", updatable = false, unique = true, nullable = false)
    private Integer aliasId;

    @Column(name = "alias_name", nullable = false)
    private String aliasName;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false, nullable = false)
    private Date creationTimestamp;

    @OneToMany(mappedBy = "user", cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    private Set<CommandStats> commandStats;

    @OneToMany(mappedBy = "user", cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    private Set<HashtagStats> hashtagStats;

    public User(Integer aliasId, String aliasName) {
        this.aliasId = aliasId;
        this.aliasName = aliasName;
        this.commandStats = new HashSet<>();
        this.hashtagStats = new HashSet<>();
    }

    public Integer getId() {
        return id;
    }

    public Integer getAliasId() {
        return aliasId;
    }

    public String getAliasName() {
        return aliasName;
    }

    public Date getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

    public Set<CommandStats> getCommandStats() {
        return commandStats;
    }

    public void setCommandStats(Set<CommandStats> commandStats) {
        for (CommandStats stats : commandStats) {
            stats.setUser(this);
        }
        this.commandStats = commandStats;
    }

    public boolean addCommandStats(CommandStats commandStats) {
        commandStats.setUser(this);
        return this.commandStats.add(commandStats);
    }

    public boolean removeCommandStats(CommandStats commandStats) {
        return this.commandStats.remove(commandStats);
    }

    public Set<HashtagStats> getHashtagStats() {
        return hashtagStats;
    }

    public void setHashtagStats(Set<HashtagStats> hashtagStats) {
        for (HashtagStats stats : hashtagStats) {
            stats.setUser(this);
        }
        this.hashtagStats = hashtagStats;
    }

    public boolean addHashtagStats(HashtagStats hashtagStats) {
        hashtagStats.setUser(this);
        return this.hashtagStats.add(hashtagStats);
    }

    public boolean removeHashtagStats(HashtagStats hashtagStats) {
        return this.hashtagStats.remove(hashtagStats);
    }
}
