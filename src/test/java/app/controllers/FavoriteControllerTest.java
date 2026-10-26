package app.controllers;

import app.dao.CocktailDAO;
import app.dao.CocktailFamilyDAO;
import app.dao.SpiritDAO;
import app.entities.Cocktail;
import app.entities.CocktailFamily;
import app.entities.spirit.Spirit;
import app.entities.user.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;

public class FavoriteControllerTest extends RestTestBase {

    private CocktailFamily family;
    private Spirit spirit;

    @BeforeEach
    void seed() {
        family = new CocktailFamilyDAO(emf).create(new CocktailFamily("Classics", "x"));
        spirit = new SpiritDAO(emf).create(new Spirit("Rum", "x"));
    }

    private Long createCocktail(String name) {
        Cocktail cocktail = new Cocktail(name, "d", "i", family);
        cocktail.setSpirit(spirit);
        return new CocktailDAO(emf).create(cocktail).getId();
    }

    private static String bearer(String token) {
        return "Bearer " + token;
    }

    @Test
    void favoriteRequireLogin() {
        given().when().get("/favorites").then().statusCode(401);
        given().when().post("/favorites/{id}", 1).then().statusCode(401);
        given().when().delete("/favorites/{id}", 1).then().statusCode(401);
    }

    @Test // Test that saving a cocktail puts it on the users list
    void savedFavoriteToList() {
        Long id = createCocktail("Mojito");
        String token = tokenFor("nico", Role.USER);

        given().header("Authorization", bearer(token))
                .when().post("/favorites/{id}", id)
                .then().statusCode(201)
                .body("name", equalTo("Mojito"));

        given().header("Authorization", bearer(token))
                .when().get("/favorites")
                .then().statusCode(200)
                .body("name", contains("Mojito"))
                .body("spirit", contains("Rum"));
    }

    @Test // Test that saving the same favorite twice returns with status code 409 (CONFLICT)
    void savingFavoriteTwiceReturns409() {
        Long id =  createCocktail("Mojito");
        String token = tokenFor("nico", Role.USER);

        given().header("Authorization", bearer(token))
                .post("/favorites/{id}", id)
                .then().statusCode(201);

        given().header("Authorization", bearer(token))
                .post("/favorites/{id}", id)
                .then().statusCode(409);
    }

    @Test // Test that a cocktail with an invalid id returns status code 400 (BAD REQUEST)
    void invalidCocktailId() {
        String token = tokenFor("nico", Role.USER);

        given().header("Authorization", bearer(token))
                .when().post("/favorites/{id}", "abc")
                .then().statusCode(400);
    }

    @Test // Test that removing/deleting a favorite makes it disappear from the list
    void removeFavoriteFromList() {
        Long id = createCocktail("Mojito");
        String token = tokenFor("nico", Role.USER);

        given().header("Authorization", bearer(token))
                .post("/favorites/{id}", id);

        given().header("Authorization", bearer(token))
                .when().delete("/favorites/{id}", id)
                .then().statusCode(204);

        given().header("Authorization", bearer(token))
                .when().get("/favorites")
                .then().statusCode(200)
                .body("size()", equalTo(0));
    }

    @Test // Test that removing a not saved cocktail from favorites returns 404 (NOT FOUND)
    void removingANotSavedFavorite() {
        Long id = createCocktail("Mojito");
        String token = tokenFor("nico", Role.USER);

        given().header("Authorization", bearer(token))
                .when().delete("/favorites/{id}", id)
                .then().statusCode(404);
    }

    @Test // Test that ensures that users can only see their own favorites
    void onlySeeOwnFavorites() {
        Long id = createCocktail("Mojito");
        String marie = tokenFor("marie", Role.USER);
        String tim = tokenFor("tim", Role.USER);

        given().header("Authorization", bearer(marie))
                .post("/favorites/{id}", id)
                .then().statusCode(201);

        given().header("Authorization", bearer(tim))
                .when().get("/favorites")
                .then().statusCode(200)
                .body("size()", equalTo(0));
    }
}
