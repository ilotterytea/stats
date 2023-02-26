package kz.ilotterytea.stats.controllers.api.v1;

import com.github.twitch4j.helix.domain.User;
import io.micronaut.context.annotation.Parameter;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import kz.ilotterytea.stats.Server;
import kz.ilotterytea.stats.models.Payload;
import kz.ilotterytea.stats.models.Target;

import java.util.*;

/**
 * @author ilotterytea
 * @since 1.0
 */
@Controller("/api/v1/")
public class V1Controller {
    private final Server server = Server.getInstance();

    @Post(
            value = "/join",
            consumes = MediaType.MULTIPART_FORM_DATA,
            produces = MediaType.APPLICATION_JSON
    )
    public HttpResponse<Payload<Map<String, Boolean>>> joinChannel(
            String targets
    ) {
        System.out.println(targets);
        ArrayList<String> ids = new ArrayList<>();
        ArrayList<String> names = new ArrayList<>();

        for (String target : targets.split(",")) {
            try {
                Long id = Long.parseLong(target);
                ids.add(target);
            } catch (NumberFormatException e) {
                names.add(target);
            }
        }

        List<User> users = server.getTwitchClient().getHelix().getUsers(
                server.getProperties().getProperty("TTV_ACCESS_TOKEN"),
                (ids.size() > 0) ? ids : null,
                (names.size() > 0) ? names : null
        ).execute().getUsers();

        Map<String, Boolean> joinedUsers = new HashMap<>();

        for (User user : users) {
            System.out.println(server.getTwitchClient().getChat());
            if (server.getTwitchClient().getChat().isChannelJoined(user.getLogin())) {
                joinedUsers.put(user.getLogin(), false);
            } else {
                server.getTwitchClient().getChat().joinChannel(user.getLogin());

                server.getTargetIds().put(user.getLogin(), user.getId());

                Target target = server.getTargetController().getOrDefault(user.getId());

                target.getActiveHistory().put(Calendar.getInstance().get(Calendar.YEAR) + "-" +
                        Calendar.getInstance().get(Calendar.MONTH) + "-" +
                        Calendar.getInstance().get(Calendar.DAY_OF_MONTH), true);

                server.getTargetController().put(
                        user.getId(),
                        target
                );

                server.getTargetController().save();

                joinedUsers.put(user.getLogin(), true);
            }
        }

        return HttpResponse
                .status(HttpStatus.OK)
                .body(new Payload<>(
                        HttpStatus.OK.getCode(),
                        "Success!",
                        joinedUsers
                ));
    }

    @Post(
            value = "/part",
            consumes = MediaType.MULTIPART_FORM_DATA,
            produces = MediaType.APPLICATION_JSON
    )
    public HttpResponse<Payload<Map<String, Boolean>>> partChannel(
            String targets
    ) {
        ArrayList<String> ids = new ArrayList<>();
        ArrayList<String> names = new ArrayList<>();

        for (String target : targets.split(",")) {
            try {
                Long id = Long.parseLong(target);
                ids.add(target);
            } catch (NumberFormatException e) {
                names.add(target);
            }
        }

        List<User> users = server.getTwitchClient().getHelix().getUsers(
                server.getProperties().getProperty("TTV_ACCESS_TOKEN"),
                (ids.size() > 0) ? ids : null,
                (names.size() > 0) ? names : null
        ).execute().getUsers();

        Map<String, Boolean> partedUsers = new HashMap<>();

        for (User user : users) {
            if (!server.getTwitchClient().getChat().isChannelJoined(user.getLogin())) {
                partedUsers.put(user.getLogin(), false);
            } else {
                server.getTwitchClient().getChat().leaveChannel(user.getLogin());
                partedUsers.put(user.getLogin(), true);

                server.getTargetIds().remove(user.getLogin());

                Target target = server.getTargetController().getOrDefault(user.getId());

                target.getActiveHistory().put(Calendar.getInstance().get(Calendar.YEAR) + "-" +
                        Calendar.getInstance().get(Calendar.MONTH) + "-" +
                        Calendar.getInstance().get(Calendar.DAY_OF_MONTH), false);

                server.getTargetController().put(
                        user.getId(),
                        target
                );

                server.getTargetController().save();
            }
        }

        return HttpResponse
                .status(HttpStatus.OK)
                .body(new Payload<>(
                        HttpStatus.OK.getCode(),
                        "Success!",
                        partedUsers
                ));
    }
}
