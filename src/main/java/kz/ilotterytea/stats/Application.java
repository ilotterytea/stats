package kz.ilotterytea.stats;

import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.info.*;
import kz.ilotterytea.stats.thirdparty.seventv.SevenTVWebsocketClient;
import kz.ilotterytea.stats.twitchbot.TwitchBot;

import java.net.URISyntaxException;

@OpenAPIDefinition(
    info = @Info(
            title = "stats",
            version = "1.0"
    )
)
public class Application {

    public static void main(String[] args) {
        try {
            SevenTVWebsocketClient client = new SevenTVWebsocketClient();
            client.connectBlocking();
        } catch (InterruptedException | URISyntaxException e) {
            throw new RuntimeException(e);
        }

        Micronaut.run(Application.class, args);

        TwitchBot bot = new TwitchBot();
        bot.run();
    }
}