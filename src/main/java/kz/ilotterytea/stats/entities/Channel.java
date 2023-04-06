package kz.ilotterytea.stats.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

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

    public Channel(Integer aliasId, String aliasName) {
        this.aliasId = aliasId;
        this.aliasName = aliasName;
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
}
