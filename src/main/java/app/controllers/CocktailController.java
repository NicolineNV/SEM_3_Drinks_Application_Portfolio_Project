package app.controllers;

import app.dto.CocktailDTO;
import app.services.CocktailService;

import io.javalin.http.Context;

public class CocktailController {

    private final CocktailService cocktailService = new CocktailService();

    public void getById(Context ctx){
        Long id = ctx.pathParamAsClass("id", Long.class).get();
        CocktailDTO cocktail = cocktailService.getById(id);
        ctx.json(cocktail);
    }
}
