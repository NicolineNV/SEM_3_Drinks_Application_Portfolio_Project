package app.controllers;

import app.dao.CocktailDAO;
import app.dao.CocktailFamilyDAO;
import app.dao.FlavorTagDAO;
import app.dao.SpiritDAO;
import app.entities.Cocktail;
import app.entities.CocktailFamily;
import app.entities.FlavorTag;
import app.entities.spirit.Spirit;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import java.util.Map;

public class QuizControllerTest extends RestTestBase {

    private Long familyId;
    private Long sweetId;
    private Long sourId;

    @BeforeEach
    void seed() {
        FlavorTag sweet = new FlavorTagDAO(emf).create(new FlavorTag("Sweet"));
        FlavorTag sour = new FlavorTagDAO(emf).create(new FlavorTag("Sour"));
        sweetId = sweet.getId();
        sourId = sour.getId();

        CocktailFamily family = new CocktailFamilyDAO(emf).create(new CocktailFamily("Classics", "x"));
        familyId = family.getId();

        Spirit rum = new Spirit("Rum", "x");
        rum.addFlavorTag(sweet, 5);
        rum.addFlavorTag(sour, 1);
        rum = new SpiritDAO(emf).create(rum);

        Spirit gin = new Spirit("Gin", "x");
        gin.addFlavorTag(sweet, 1);
        gin.addFlavorTag(sour, 5);
        gin = new SpiritDAO(emf).create(gin);

        Cocktail sweetDrink = new Cocktail("Sweet Rum Drink", "d", "i", family);
        sweetDrink.setSpirit(rum);
        new CocktailDAO(emf).create(sweetDrink);

        Cocktail sourDrink = new Cocktail("Sour Gin Drink", "d", "i", family);
        sourDrink.setSpirit(gin);
        new CocktailDAO(emf).create(sourDrink);
    }

    private Map<String, Object> answers(Long family, int sweet, int sour) {
        return Map.of("cocktailFamilyId", family,
                "tasteAnswers", Map.of(sweetId.toString(), sweet, sourId.toString(), sour));
    }

    @Test // Test that a most sweet answer gives the sweet cocktail
    void sweetAnswerSweetCocktail() {
        given().contentType(ContentType.JSON).body(answers(familyId, 5, 1))
                .when().post("/quiz")
                .then().statusCode(200)
                .body("name", equalTo("Sweet Rum Drink"));
    }

    @Test // Test that a most sour answer gives the sour cocktail
    void sourAnswerSourCocktail() {
        given().contentType(ContentType.JSON).body(answers(familyId, 1, 5))
                .when().post("/quiz")
                .then().statusCode(200)
                .body("name", equalTo("Sour Gin Drink"));
    }

    @Test // Test that when family is unknown then it returns 404 (NOT FOUND)
    void unknownFamilyReturns404() {
        given().contentType(ContentType.JSON).body(answers(999999L, 3, 3))
                .when().post("/quiz")
                .then().statusCode(404);
    }

    @Test // Test that rating flavor tags outside 1 to 5 returns 400 (BAD REQUEST)
    void ratingOutsideReturns400() {
        given().contentType(ContentType.JSON).body(answers(familyId, 6, 1))
                .when().post("/quiz")
                .then().statusCode(400);
    }

    @Test // Test that when family is missing it returns 400 (BAD REQUEST)
    void missingFamilyReturns400() {
        given().contentType(ContentType.JSON)
                .body(Map.of("tasteAnswers", Map.of(sweetId.toString(), 3)))
                .when().post("/quiz")
                .then().statusCode(400);
    }
}
