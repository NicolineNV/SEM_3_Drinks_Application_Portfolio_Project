package app.controllers;

import app.dto.CreateCocktailDTO;
import app.dto.UserDTO;
import app.security.AccessHandler;
import app.services.CocktailService;
import app.services.UserService;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import jakarta.persistence.EntityManagerFactory;

public class CocktailController {

    private final CocktailService cocktailService;
    private final UserService userService;

    public CocktailController (EntityManagerFactory emf) {
        this.cocktailService = new CocktailService(emf);
        this.userService = new UserService(emf);
    }

    public void getById(Context ctx) {
        Long id = ctx.pathParamAsClass("id", Long.class).get();
        ctx.json(cocktailService.getById(id));
    }

    public void getAll(Context ctx) {
        ctx.json(cocktailService.getAll());
    }

    public void create(Context ctx) {
        CreateCocktailDTO request = ctx.bodyValidator(CreateCocktailDTO.class)
                .check(r -> r.getName() != null && !r.getName().isBlank(),
                        "name is required")
                .check(r -> r.getInstructions() != null && !r.getInstructions().isBlank(),
                        "instructions are required")
                .check(r -> r.getCocktailFamilyId() != null,
                        "cocktailFamilyId is required")
                .get();

        String username = ctx.attribute(AccessHandler.USERNAME_ATTR);
        UserDTO admin = userService.getByUsername(username);

        ctx.status(HttpStatus.CREATED).json(cocktailService.createCocktail(request, admin));
    }
}
