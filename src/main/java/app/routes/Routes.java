package app.routes;

import app.controllers.AuthController;
import app.controllers.CocktailController;
import app.security.AccessRole;
import app.security.TokenSecurity;
import io.javalin.apibuilder.EndpointGroup;
import jakarta.persistence.EntityManagerFactory;

import static io.javalin.apibuilder.ApiBuilder.*;

public class Routes {

    private final CocktailController cocktailController;
    private final AuthController authController;

    public Routes(EntityManagerFactory emf, TokenSecurity tokenSecurity) {
        this.cocktailController = new CocktailController(emf);
        this.authController = new AuthController(emf, tokenSecurity);
    }

    public EndpointGroup getRoutes() {
        return () -> {
            path("auth", () -> {
                post("register", authController::register, AccessRole.ANYONE);
                post("login", authController::login, AccessRole.ANYONE);
            });
            path("cocktails", () -> {
                get("{id}", cocktailController::getById, AccessRole.ANYONE);
            });
        };
    }
}
