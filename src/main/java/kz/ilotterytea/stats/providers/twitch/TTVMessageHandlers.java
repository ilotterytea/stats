package kz.ilotterytea.stats.providers.twitch;

import com.github.twitch4j.chat.events.channel.IRCMessageEvent;
import kz.ilotterytea.stats.Server;
import kz.ilotterytea.stats.models.Target;
import kz.ilotterytea.stats.models.emotes.Emote;
import kz.ilotterytea.stats.models.emotes.Provider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author ilotterytea
 * @since 1.0
 */
public class TTVMessageHandlers {
    private static final Server server = Server.getInstance();

    public static void ircMessageEvent(IRCMessageEvent event) {
        if (!event.getMessage().isPresent()) {
            return;
        }

        Target target = server.getTargetController().getOrDefault(event.getChannel().getId());
        String msg = event.getMessage().get();
        ArrayList<String> sMsg = new ArrayList<>(Arrays.asList(msg.split(" ")));

        // Command
        if (
                sMsg.get(0).startsWith("!") ||
                        // Supibot support:
                        sMsg.get(0).startsWith("$")
        ) {
            target.getUsedCommands().put(
                    sMsg.get(0),
                    target.getUsedCommands().getOrDefault(sMsg.get(0), 0) + 1
            );
        }

        for (String w : sMsg) {
            // Emote block:
            Emote emote = target.getEmotes()
                    .get(Provider.SEVENTV)
                    .values()
                    .stream()
                    .filter(e -> Objects.equals(e.getProviderName(), w))
                    .findFirst().orElse(null);

            if (emote != null) {
                emote.setCount(emote.getCount() + 1);
                target.getEmotes().get(Provider.SEVENTV).put(emote.getProviderId(), emote);
            }

            // Mention
            if (w.startsWith("@")) {
                target.getMentionedUsers().put(
                        w,
                        target.getMentionedUsers().getOrDefault(w, 0) + 1
                );
            }
        }

        target.setTotalMessagesCount(target.getTotalMessagesCount() + 1);
    }
}
