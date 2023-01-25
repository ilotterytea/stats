package kz.ilotterytea.stats;

import io.micronaut.http.annotation.*;

@Controller("/stats")
public class StatsController {

    @Get(uri="/", produces="text/plain")
    public String index() {
        return "Example Response";
    }
}