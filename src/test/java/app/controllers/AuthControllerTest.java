package app.controllers;

import app.config.ApplicationConfig;
import app.dao.IntegrationTestBase;
import app.routes.Routes;
import app.security.AccessHandler;
import app.security.TokenSecurity;
import io.javalin.Javalin;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AuthControllerTest extends IntegrationTestBase {

    private static Javalin app;

    @BeforeAll
    static void startServer() {
        TokenSecurity ts = new TokenSecurity("test-secret-test-secret-test-secret-1234", "test", 60_000);
        app = new ApplicationConfig(new Routes(emf, ts), new AccessHandler(ts)).startServer(0);
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = app.port();
        RestAssured.basePath = "/api/v1";
    }

    @AfterAll
    static void stopServer() {
        if (app != null) {
            app.stop();
        }
    }

    private void register(String username, String email, String password) {
        given().contentType(ContentType.JSON)
                .body(Map.of("username", username, "email", email, "password", password))
                .post("/auth/register");
    }

    @Test // Test that password are not returnet when a user gets registered
    void registerReturns201AndNoPassword() {
        given().contentType(ContentType.JSON)
                .body(Map.of("username", "nicoline", "email", "nico@test.dk", "password", "secret123"))
                .when().post("/auth/register")
                .then().statusCode(201)
                .body("username", equalTo("nicoline"))
                .body("role", equalTo("USER"))
                .body("password", nullValue())
                .body("passwordHash", nullValue());
    }

    @Test // Test it returns 409 (Conflict) if user tries to register with an already existing username
    void registerDuplicateUsername() {
        register("nicoline", "nico@test.dk", "secret123");
        given().contentType(ContentType.JSON)
                    .body(Map.of("username", "nicoline", "email", "other@test.dk", "password", "secret123"))
                .when().post("/auth/register")
                .then().statusCode(409);
    }

    @Test // Test it returns 409 (Conflict) if user tries to register with an already existing email
    void registerDuplicateEmail() {
        register("nicoline", "nico@test.dk", "secret123");
        given().contentType(ContentType.JSON)
                    .body(Map.of("username", "Other", "email", "nico@test.dk", "password", "secret123"))
                .when().post("/auth/register")
                .then().statusCode(409);
    }

    @Test // Test that passwords can not be shorter than 8 characters, returns 400 (Bad Request) if tried
    void registerWithShortPassword() {
        given().contentType(ContentType.JSON)
                    .body(Map.of("username", "nicoline", "email", "nico@test.dk", "password", "1234567"))
                .when().post("/auth/register")
                .then().statusCode(400);
    }

    @Test // Test that when a user logs in, the token is returnet
    void loginReturnsToken() {
        register("nicoline", "nico@test.dk", "secret123");
        given().contentType(ContentType.JSON)
                    .body(Map.of("username", "nicoline", "password", "secret123"))
                .when().post("/auth/login")
                .then().statusCode(200)
                    .body("token", not(emptyOrNullString()))
                    .body("user.username", equalTo("nicoline"));
    }

    @Test // Test that wrong password and unknown user gives the same answer 401 (Unauthorized),
            // so a possible hacker can not see where the login failed
    void wrongPasswordUnknownUserSameAnswer() {
        register("nicoline", "nico@test.dk", "secret123");

        String wrongPassword = given().contentType(ContentType.JSON)
                    .body(Map.of("username", "nicoline", "password", "wrong-password"))
                .post("/auth/login").then().statusCode(401)
                .extract().path("message");

        String unknownUser = given().contentType(ContentType.JSON)
                    .body(Map.of("username", "nobody", "password", "secret123"))
                .post("/auth/login").then().statusCode(401)
                .extract().path("message");

        assertEquals(wrongPassword, unknownUser);
    }
}
