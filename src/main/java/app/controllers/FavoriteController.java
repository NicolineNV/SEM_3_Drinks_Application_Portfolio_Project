package app.controllers;

import app.exceptions.ApiException;
import app.security.AccessHandler;
import app.services.CocktailMenuService;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import jakarta.persistence.EntityManagerFactory;

public class FavoriteController {

    private final CocktailMenuService service;

    public FavoriteController(EntityManagerFactory emf) {
        this.service = new CocktailMenuService(emf);
    }

    public void getAll(Context ctx) {
        ctx.json(service.getFavorites(currentUsername(ctx)));
    }

    public void save(Context ctx) {
        Long cocktailId = ctx.pathParamAsClass("cocktailId", Long.class).get();
        ctx.status(HttpStatus.CREATED).json(service.saveFavorite(currentUsername(ctx), cocktailId));
    }

    public void remove(Context ctx) {
        Long cocktailId = ctx.pathParamAsClass("cocktailId", Long.class).get();
        service.removeFavorite(currentUsername(ctx), cocktailId);
        ctx.status(HttpStatus.NO_CONTENT);
    }

    // Username comes from the verified token (set by AccessHandler), never from the request body or URL
    private String currentUsername(Context ctx) {
        String username = ctx.attribute(AccessHandler.USERNAME_ATTR);
        if (username == null) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Not logged in");
        }
        return username;
    }
}
