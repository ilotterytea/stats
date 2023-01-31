package kz.ilotterytea.stats.controllers.api.v1;

import io.micronaut.context.annotation.Parameter;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import kz.ilotterytea.stats.Server;
import kz.ilotterytea.stats.models.Payload;
import kz.ilotterytea.stats.models.Target;

/**
 * @author ilotterytea
 * @since 1.0
 */
@Controller("/api/v1/mentioned/")
public class MentionedUsersController {
    @Get(
            value = "/{id}",
            produces = MediaType.APPLICATION_JSON
    )
    public HttpResponse getMentionedUsers(
            @Parameter("id") String id
    ) {
        Target target = Server.getInstance().getTargetController()
                .get(id);

        if (target == null) {
            return HttpResponse
                    .status(HttpStatus.NOT_FOUND)
                    .body(new Payload<>(
                            HttpStatus.NOT_FOUND.getCode(),
                            "Target ID " + id + " does not exist!",
                            null
                    ));
        }

        return HttpResponse
                .status(HttpStatus.OK)
                .body(new Payload<>(
                        HttpStatus.OK.getCode(),
                        "Success!",
                        target.getMentionedUsersHistory()
                ));
    }
}
