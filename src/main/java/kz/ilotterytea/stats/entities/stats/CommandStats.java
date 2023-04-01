package kz.ilotterytea.stats.entities.stats;

import jakarta.persistence.*;
import kz.ilotterytea.stats.entities.User;
import kz.ilotterytea.stats.entities.Command;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

/**
 * @author ilotterytea
 * @since 1.0
 */
@Entity
@Table(name = "command_stats")
public class CommandStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", insertable = false, updatable = false, nullable = false)
    private Integer userId;
    @Column(name = "command_id", insertable = false, updatable = false, nullable = false)
    private Integer commandId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "command_id", nullable = false)
    private Command command;

    @Column(name = "used_times", nullable = false)
    private Integer usedTimes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private Date creationTimestamp;

    @UpdateTimestamp
    @Column(name = "last_used_at", nullable = false)
    private Date lastUsageTimestamp;

    public CommandStats() {
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

    public Integer getCommandId() {
        return commandId;
    }

    public Command getCommand() {
        return command;
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

    public void setCommand(Command command) {
        this.command = command;
    }

    public void setUsedTimes(Integer usedTimes) {
        this.usedTimes = usedTimes;
    }
}
