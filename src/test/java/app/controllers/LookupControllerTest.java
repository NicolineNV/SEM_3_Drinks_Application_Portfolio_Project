package app.controllers;

import app.dao.CocktailFamilyDAO;
import app.dao.FlavorTagDAO;
import app.entities.CocktailFamily;
import app.entities.FlavorTag;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.contains;

public class LookupControllerTest extends RestTestBase {

    @Test // Test that cocktail families are sorted by name = Classics first, then Sours
    void familiesSortedByName() {
        new CocktailFamilyDAO(emf).create(new CocktailFamily("Sours", "x"));
        new CocktailFamilyDAO(emf).create(new CocktailFamily("Classics", "x"));

        given().when().get("/families")
                .then().statusCode(200)
                .body("name", contains("Classics", "Sours"));
    }

    @Test // Test that flavor tags are open to everyone
    void flavorTagsOpenToANYONE() {
        new FlavorTagDAO(emf).create(new FlavorTag("Sweet"));

        given().when().get("/flavor-tags")
                .then().statusCode(200)
                .body("name", contains("Sweet"));
    }

    @Test // Test that admin list of liqueurs are rejected without an admin token
    void adminListRejectNoToken() {
        given().when().get("/liqueurs")
                .then().statusCode(401);
    }
}
