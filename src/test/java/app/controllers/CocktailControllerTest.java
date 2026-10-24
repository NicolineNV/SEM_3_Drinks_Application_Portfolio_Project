package app.controllers;

import app.config.ApplicationConfig;
import app.dao.CocktailDAO;
import app.dao.CocktailFamilyDAO;
import app.dao.IntegrationTestBase;
import app.dao.SpiritDAO;
import app.entities.Cocktail;
import app.entities.CocktailFamily;
import app.entities.Spirit;
import app.routes.Routes;
import app.security.AccessHandler;
import app.security.TokenSecurity;
import io.javalin.Javalin;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class CocktailControllerTest extends IntegrationTestBase {

    private static Javalin app;

    @BeforeAll
    static void setUpAll () {
        TokenSecurity ts = new TokenSecurity("test-secret-test-secret-test-secret-1234", "test", 60_000);
        Routes routes = new Routes(emf, ts); // gets emf from IntegrationTestBase
        app = new ApplicationConfig(routes, new AccessHandler(ts)).startServer(7777);

        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 7777;
        RestAssured.basePath = "/api/v1";
    }

    @AfterAll
    static void tearDownAll() {
        ApplicationConfig.stopServer(app);
    }

    @Test // Test that get cocktail by id request returns a cocktail
    void getCocktailByIdReturnsCocktail() {
        Long id = createCocktail("Old Fashioned");

        given().when().get("/cocktails/{id}", id)
                .then().statusCode(200)
                .body("name", equalTo("Old Fashioned"));
    }

    @Test // Test that request for an unknown cocktail returns 404
    void getUnknownCocktailReturns404() {
        given().when().get("/cocktails/{id}", 999999)
                .then().statusCode(404).body("status", equalTo(404));
    }

    @Test // Test that fetching a cocktail with invalid id returns 400
    void getCocktailInvalidIdReturns400() {
        given().when().get("/cocktails/{id}", "abc")
                .then().statusCode(400);
    }

    // ---------- Helping method for testdata ----------

    private Long createCocktail(String name) {
        CocktailFamily family = new CocktailFamilyDAO(emf)
                .create(new CocktailFamily("Classics", "test family"));

        Spirit spirit = new SpiritDAO(emf)
                .create(new Spirit("Whiskey", "test spirit"));

        Cocktail cocktail = new Cocktail(name, "test description", "test instructions", family);
        cocktail.setSpirit(spirit);

        return new CocktailDAO(emf).create(cocktail).getId();
    }
}
