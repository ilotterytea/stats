package kz.ilotterytea.stats;

import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.info.*;
import kz.ilotterytea.stats.entities.Channel;
import kz.ilotterytea.stats.twitchbot.TwitchBot;
import kz.ilotterytea.stats.utils.HibernateUtil;
import org.hibernate.Session;

@OpenAPIDefinition(
    info = @Info(
            title = "stats",
            version = "1.0"
    )
)
public class Application {

    public static void main(String[] args) {
        Micronaut.run(Application.class, args);

        TwitchBot bot = new TwitchBot();
        bot.run();
    }
}