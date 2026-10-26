package app.controllers;

import app.dao.CocktailFamilyDAO;
import app.dao.SpiritDAO;
import app.entities.CocktailFamily;
import app.entities.spirit.Spirit;
import app.entities.user.Role;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;

import java.util.Map;

public class AdminCocktailTest extends RestTestBase {

    private Long familyId;
    private Long spiritId;

    @BeforeEach
    void seed() {
        familyId = new CocktailFamilyDAO(emf).create(new CocktailFamily("Classics", "x")).getId();
        spiritId = new SpiritDAO(emf).create(new Spirit("Rum", "x")).getId();
    }

    private Map<String, Object> newCocktail() {
        return Map.of("name", "Mojito", "description", "Fresh",
                "instructions", "Muddle and stir", "cocktailFamilyId", familyId, "spiritId", spiritId);
    }

    private static String bearer(String token) {
        return "Bearer " + token;
    }

    @Test // Test that ensures that creating a cocktail requires admin to be logged in
    void creatingCocktailRequiresLogin() {
        given().contentType(ContentType.JSON).body(newCocktail())
                .when().post("/cocktails")
                .then().statusCode(401);
    }

    @Test // Test that ensures that ordinary users can not create a cocktail
    void ordinaryUsersAreForbidden() {
        String token = tokenFor("nico", Role.USER);

        given().header("Authorization", bearer(token))
                .contentType(ContentType.JSON).body(newCocktail())
                .when().post("/cocktails")
                .then().statusCode(403);
    }

    @Test
    void adminCanCreateCocktail() {
        String token = tokenFor("admin", Role.ADMIN);

        given().header("Authorization", bearer(token))
                .contentType(ContentType.JSON).body(newCocktail())
                .when().post("/cocktails")
                .then().statusCode(201)
                .body("name", equalTo("Mojito"))
                .body("spirit", equalTo("Rum"));

        given().when().get("/cocktails")
                .then().statusCode(200)
                .body("name", contains("Mojito"));
    }

    @Test // Test that a cocktail without ingredients returns with status code 400 (BAD REQUEST)
    void cocktailWithoutIngredient() {
        String token = tokenFor("admin", Role.ADMIN);

        given().header("Authorization", bearer(token)).contentType(ContentType.JSON)
                .body(Map.of("name", "Water", "description", "d", "instructions", "i",
                        "cocktailFamilyId", familyId))
                .when().post("/cocktails")
                .then().statusCode(400);
    }

    @Test // Test that a cocktail without a name returns with status code 400 (BAD REQUEST)
    void cocktailWithoutName() {
        String token = tokenFor("admin", Role.ADMIN);

        given().header("Authorization", bearer(token)).contentType(ContentType.JSON)
                .body(Map.of("description", "d", "instructions", "i",
                        "cocktailFamilyId", familyId, "spiritId", spiritId))
                .when().post("/cocktails")
                .then().statusCode(400);
    }

    @Test // Test that a cocktail with an unknown cocktail family returns status code 404 (NOT FOUND)
    void unknownFamilyReturns404() {
        String token = tokenFor("admin", Role.ADMIN);

        given().header("Authorization", bearer(token)).contentType(ContentType.JSON)
                .body(Map.of("name", "Mojito", "description", "d", "instructions", "i",
                        "cocktailFamilyId", 999999L, "spiritId", spiritId))
                .when().post("/cocktails")
                .then().statusCode(404);
    }

    @Test // Test that admin only lists are forbidden for users and only open for admins
    void adminOnlyListForAdmins() {
        String user = tokenFor("nico", Role.USER);
        String admin = tokenFor("admin", Role.ADMIN);

        given().header("Authorization", bearer(user))
                .when().get("/liqueurs")
                .then().statusCode(403);

        given().header("Authorization", bearer(admin))
                .when().get("/liqueurs")
                .then().statusCode(200);
    }
}
