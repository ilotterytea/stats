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

    @OneToMany(mappedBy = "channel", cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    private Set<Command> commands;

    @OneToMany(mappedBy = "channel", cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    private Set<Hashtag> hashtags;

    @OneToMany(mappedBy = "channel", cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    private Set<Word> words;

    @OneToMany(mappedBy = "channel", cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    private Set<Mention> mentions;

    public Channel(Integer aliasId, String aliasName) {
        this.aliasName = aliasName;
        this.aliasId = aliasId;
        this.isParted = false;
        this.emotes = new HashSet<>();
        this.commands = new HashSet<>();
        this.hashtags = new HashSet<>();
        this.words = new HashSet<>();
        this.mentions = new HashSet<>();
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

    public Set<Command> getCommands() {
        return commands;
    }

    public void setCommands(Set<Command> commands) {
        for (Command command : commands) {
            command.setChannel(this);
        }
        this.commands = commands;
    }

    public boolean addCommand(Command command) {
        command.setChannel(this);
        return this.commands.add(command);
    }

    public boolean removeCommand(Command command){
        return this.commands.remove(command);
    }

    public Set<Hashtag> getHashtags() {
        return hashtags;
    }

    public void setHashtags(Set<Hashtag> hashtags) {
        for (Hashtag hashtag : hashtags) {
            hashtag.setChannel(this);
        }
        this.hashtags = hashtags;
    }

    public boolean addHashtag(Hashtag hashtag) {
        hashtag.setChannel(this);
        return this.hashtags.add(hashtag);
    }

    public boolean removeHashtag(Hashtag hashtag) {
        return this.hashtags.remove(hashtag);
    }

    public Set<Word> getWords() {
        return words;
    }

    public void setWords(Set<Word> words) {
        for (Word word : words) {
            word.setChannel(this);
        }
        this.words = words;
    }

    public boolean addWord(Word word) {
        word.setChannel(this);
        return this.words.add(word);
    }

    public boolean removeWord(Word word) {
        return this.words.remove(word);
    }

    public Set<Mention> getMentions() {
        return mentions;
    }

    public void setMentions(Set<Mention> mentions) {
        for (Mention mention : mentions) {
            mention.setChannel(this);
        }
        this.mentions = mentions;
    }

    public boolean addMention(Mention mention) {
        mention.setChannel(this);
        return this.mentions.add(mention);
    }

    public boolean removeMention(Mention mention) {
        return this.mentions.remove(mention);
    }
}
