package app.config;

import app.exceptions.ApiException;
import io.javalin.Javalin;

import java.util.Map;

public class ApplicationConfig {

    public static Javalin startServer (int port) {
        Javalin app = Javalin.create(config -> {
            config.router.contextPath = "/api/v1";
            config.router.apiBuilder(new Routes().getRoutes());
        });

        // All ApiExceptions written as one similar JSON-answer
        app.exception(ApiException.class, (e, ctx) ->
                ctx.status(e.getCode()).json(Map.of("status", e.getCode(), "message", e.getMessage())));

        // All other exceptions are unexpected errors: log them, but hide details for client
        app.exception(Exception.class, (e, ctx) -> {
            ctx.status(500).json(Map.of("status", 500, "message", "Internal server error"));
        });

        app.start(port);
        return app;
    }
}
