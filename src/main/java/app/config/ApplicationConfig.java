package app.config;

import app.exceptions.ApiException;
import io.javalin.Javalin;
import io.javalin.validation.ValidationException;
import jakarta.persistence.EntityManagerFactory;

import java.util.Map;

public class ApplicationConfig {

    public static Javalin startServer (int port, EntityManagerFactory emf) {
        Javalin app = Javalin.create(config -> {
            config.router.contextPath = "/api/v1";
            config.router.apiBuilder(new Routes(emf).getRoutes());
        });

        // All ApiExceptions written as one similar JSON-answer
        app.exception(ApiException.class, (e, ctx) ->
                ctx.status(e.getCode()).json(Map.of("status", e.getCode(), "message", e.getMessage())));

        // Validate client request = example: /cocktails/abc, id not a number, client error (400), not server
        app.exception(ValidationException.class, (e, ctx) ->
                ctx.status(400).json(Map.of("status", 400, "message", "Invalid request")));

        // All other exceptions are unexpected errors: log them, but hide details for client
        app.exception(Exception.class, (e, ctx) -> {
            ctx.status(500).json(Map.of("status", 500, "message", "Internal server error"));
        });

        app.start(port);
        return app;
    }
}
