package app.controllers;

import app.services.CocktailService;

import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;

public class CocktailController {

    private final CocktailService cocktailService;

    public CocktailController (EntityManagerFactory emf) {
        this.cocktailService = new CocktailService(emf);
    }

    public void getById(Context ctx) {
        Long id = ctx.pathParamAsClass("id", Long.class).get();
        ctx.json(cocktailService.getById(id));
    }

    public void getAll(Context ctx) {
        ctx.json(cocktailService.getAll());
    }
}
