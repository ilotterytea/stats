package kz.ilotterytea.stats;

import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.info.*;

@OpenAPIDefinition(
    info = @Info(
            title = "stats",
            version = "1.0"
    )
)
public class Application {

    public static void main(String[] args) {
        Micronaut.run(Application.class, args);

        Server server = new Server();
        Runtime.getRuntime().addShutdownHook(new Thread(server::dispose));
        server.init();
    }
}