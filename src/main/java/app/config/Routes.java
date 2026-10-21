package app.config;

import app.controllers.CocktailController;
import io.javalin.apibuilder.EndpointGroup;
import jakarta.persistence.EntityManagerFactory;

import static io.javalin.apibuilder.ApiBuilder.*;

public class Routes {

    private final CocktailController cocktailController;

    public Routes(EntityManagerFactory emf) {
        this.cocktailController = new CocktailController(emf);
    }

    public EndpointGroup getRoutes() {
        return () -> path("cocktails", () -> {
            get("{id}", cocktailController::getById);
        });
    }
}
