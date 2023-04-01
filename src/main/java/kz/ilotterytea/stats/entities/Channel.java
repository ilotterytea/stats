package kz.ilotterytea.stats.entities;

import jakarta.persistence.*;
import kz.ilotterytea.stats.entities.emotes.Emote;
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
@Table(name = "channels")
public class Channel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "alias_id", updatable = false, nullable = false, unique = true)
    private Integer aliasId;

    @Column(name = "alias_name", nullable = false)
    private String aliasName;

    @Column(name = "is_parted", nullable = false)
    private Boolean isParted;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false, nullable = false)
    private Date creationTimestamp;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_active_at", nullable = false)
    private Date lastActiveTimestamp;

    @OneToMany(mappedBy = "channel", cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    private Set<Emote> emotes;

    public Channel(Integer aliasId, String aliasName) {
        this.aliasName = aliasName;
        this.aliasId = aliasId;
        this.isParted = false;
        this.emotes = new HashSet<>();
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

    public Boolean getParted() {
        return isParted;
    }

    public Date getCreationTimestamp() {
        return creationTimestamp;
    }

    public Date getLastActiveTimestamp() {
        return lastActiveTimestamp;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

    public void setLastActiveTimestamp(Date lastActiveTimestamp) {
        this.lastActiveTimestamp = lastActiveTimestamp;
    }

    public void setParted(Boolean parted) {
        isParted = parted;
    }

    public Set<Emote> getEmotes() {
        return emotes;
    }

    public void setEmotes(Set<Emote> emotes) {
        for (Emote emote : emotes) {
            emote.setChannel(this);
        }

        this.emotes = emotes;
    }

    public boolean addEmote(Emote emote) {
        emote.setChannel(this);
        return this.emotes.add(emote);
    }

    public boolean removeEmote(Emote emote) {
        return this.emotes.remove(emote);
    }
}
