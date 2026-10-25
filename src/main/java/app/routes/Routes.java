package app.routes;

import app.controllers.AuthController;
import app.controllers.CocktailController;
import app.controllers.LookupController;
import app.controllers.QuizController;
import app.security.AccessRole;
import app.security.TokenSecurity;
import io.javalin.apibuilder.EndpointGroup;
import jakarta.persistence.EntityManagerFactory;

import static io.javalin.apibuilder.ApiBuilder.*;

public class Routes {

    private final CocktailController cocktailController;
    private final AuthController authController;
    private final LookupController lookupController;
    private final QuizController quizController;

    public Routes(EntityManagerFactory emf, TokenSecurity tokenSecurity) {
        this.cocktailController = new CocktailController(emf);
        this.authController = new AuthController(emf, tokenSecurity);
        this.lookupController = new LookupController(emf);
        this.quizController = new QuizController(emf);
    }

    public EndpointGroup getRoutes() {
        return () -> {
            path("auth", () -> {
                post("register", authController::register, AccessRole.ANYONE);
                post("login", authController::login, AccessRole.ANYONE);
            });
            path("cocktails", () -> {
                get(cocktailController::getAll, AccessRole.ANYONE);
                get("{id}", cocktailController::getById, AccessRole.ANYONE);
            });
            path("families", () -> get(lookupController::getFamilies, AccessRole.ANYONE));
            path("spirits", () -> get(lookupController::getSpirits, AccessRole.ANYONE));
            path("flavor-tags", () -> get(lookupController::getFlavorTags, AccessRole.ANYONE));

            path("liqueurs", () -> get(lookupController::getLiqueurs, AccessRole.ADMIN));
            path("mixers", () -> get(lookupController::getMixers, AccessRole.ADMIN));
            path("syrups", () -> get(lookupController::getSyrups, AccessRole.ADMIN));
            path("garnishes", () -> get(lookupController::getGarnishes, AccessRole.ADMIN));

            post("quiz", quizController::findMatch, AccessRole.ANYONE);
        };
    }
}
