package app.config;

import app.exceptions.ApiException;
import app.routes.Routes;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ApplicationConfig {

    private final Routes routes;
    private static final Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);

    public ApplicationConfig(Routes routes) {
        this.routes = routes;
    }

    public Javalin startServer (int port) {
        return Javalin.create(config -> {
            config.showJavalinBanner = false;
            config.router.apiBuilder(routes.getRoutes());
            config.router.contextPath = "/api/v1";
            config.bundledPlugins.enableRouteOverview("/routes");
        })
                .exception(ApiException.class, ApplicationConfig::apiExceptionHandler)
                .exception(Exception.class, ApplicationConfig::generalExceptionHandler)
                .exception(ValidationException.class, ApplicationConfig::validationExceptionHandler)
                .start(port);
    }

    public static void stopServer(Javalin app) {
        app.stop();
    }

    private static void apiExceptionHandler(ApiException e, Context ctx){
        // ApiException already logs itself in constructor
        ctx.status(e.getCode());
        ctx.json(errorBody(e.getCode(), e.getMessage()));
    }

    private static void validationExceptionHandler(ValidationException e, Context ctx) {
        logger.warn("Invalid request on {} {}: {}", ctx.method(), ctx.path(), e.getErrors());
        ctx.status(HttpStatus.BAD_REQUEST);
        ctx.json(errorBody(400, "Invalid request"));
    }

    private static void generalExceptionHandler(Exception e, Context ctx) {
        logger.error("Unhandled exception on {} {}", ctx.method(), ctx.path(), e);
        ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
        ctx.json(errorBody(500, "Internal server error"));
    }

    private static Map<String, Object> errorBody(int status, String message) {
        return Map.of("status", status, "message", message);
    }
}
