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

    @Column(name = "alias_id", nullable = false, unique = true, updatable = false)
    private Integer aliasId;

    @Column(name = "alias_name", nullable = false)
    private String aliasName;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date creationTimestamp;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "refreshed_at")
    private Date refreshedTimestamp;

    @Column(name = "opt_outed_at")
    private Date optOutTimestamp;

    @OneToMany(mappedBy = "channel", cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    private Set<Emote> emotes;

    public Channel(Integer aliasId, String aliasName) {
        this.aliasId = aliasId;
        this.aliasName = aliasName;
        this.emotes = new HashSet<>();
    }

    public Channel() {}

    public Integer getId() {
        return id;
    }

    public Integer getAliasId() {
        return aliasId;
    }

    public String getAliasName() {
        return aliasName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

    public Date getCreationTimestamp() {
        return creationTimestamp;
    }

    public Date getRefreshedTimestamp() {
        return refreshedTimestamp;
    }

    public Date getOptOutTimestamp() {
        return optOutTimestamp;
    }

    public void setOptOutTimestamp(Date optOutTimestamp) {
        this.optOutTimestamp = optOutTimestamp;
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

    public void addEmote(Emote emote) {
        emote.setChannel(this);
        this.emotes.add(emote);
    }

    public void removeEmote(Emote emote) {
        this.emotes.remove(emote);
    }
}
