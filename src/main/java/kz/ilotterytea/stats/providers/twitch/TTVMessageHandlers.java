package kz.ilotterytea.stats.providers.twitch;

import com.github.twitch4j.chat.events.channel.IRCMessageEvent;
import kz.ilotterytea.stats.Server;
import kz.ilotterytea.stats.models.Target;
import kz.ilotterytea.stats.models.emotes.Emote;
import kz.ilotterytea.stats.models.emotes.Provider;

import java.util.*;

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
        final String date = Calendar.getInstance().get(Calendar.YEAR) + "-" +
                Calendar.getInstance().get(Calendar.MONTH) + "-" +
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        // Command
        if (
                sMsg.get(0).startsWith("!") ||
                        // Supibot support:
                        sMsg.get(0).startsWith("$")
        ) {
            Map<String, Integer> record = target.getUsedCommandsHistory().getOrDefault(sMsg.get(0), new HashMap<>());
            record.put(date, record.getOrDefault(date, 0) + 1);
            target.getUsedCommandsHistory().put(sMsg.get(0), record);
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
                emote.getCountHistory().put(date, emote.getCountHistory().getOrDefault(date, 0) + 1);
                emote.setCount(emote.getCount() + 1);
                target.getEmotes().get(Provider.SEVENTV).put(emote.getProviderId(), emote);
            }

            // Mention
            if (w.startsWith("@")) {
                Map<String, Integer> record = target.getMentionedUsersHistory().getOrDefault(w, new HashMap<>());
                record.put(date, record.getOrDefault(date, 0) + 1);
                target.getMentionedUsersHistory().put(w, record);
            }
        }

        target.getMessageCountHistory().put(date, target.getMessageCountHistory().getOrDefault(date, 0) + 1);
    }
}
