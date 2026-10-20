package app.config;

import app.controllers.CocktailController;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class Routes {

    private final CocktailController cocktailController = new CocktailController();

    public EndpointGroup getRoutes() {
        return () -> path("cocktails", () -> {
            get("{id}", cocktailController::getById);
        });
    }
}
