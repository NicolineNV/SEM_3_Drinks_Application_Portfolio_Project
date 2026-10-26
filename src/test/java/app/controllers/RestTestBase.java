package app.controllers;

import app.config.ApplicationConfig;
import app.dao.IntegrationTestBase;
import app.dao.UserDAO;
import app.entities.user.Role;
import app.entities.user.User;
import app.routes.Routes;
import app.security.AccessHandler;
import app.security.TokenSecurity;
import io.javalin.Javalin;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.util.Map;

import static io.restassured.RestAssured.given;

public abstract class RestTestBase extends IntegrationTestBase {

    protected static final TokenSecurity TEST_TOKENS =
            new TokenSecurity("test-secret-test-secret-test-secret-1234", "test", 60_000);
    private static Javalin app;

    @BeforeAll
    static void setUpServer() {
        Routes routes = new Routes(emf, TEST_TOKENS); // gets emf from IntegrationTestBase
        app = new ApplicationConfig(routes, new AccessHandler(TEST_TOKENS))
                .startServer(0);
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = app.port();
        RestAssured.basePath = "/api/v1";
    }

    @AfterAll
    static void tearDownAll() {
        ApplicationConfig.stopServer(app);
    }

    protected String tokenFor(String username, Role role) {
        given().contentType(ContentType.JSON)
                .body(Map.of("username", username, "email",
                        username + "@test.dk", "password", "secret123"))
                .post("/auth/register");

        if (role == Role.ADMIN) {
            UserDAO userDAO = new UserDAO(emf);
            User user = userDAO.findByUsername(username).orElseThrow();
            user.setRole(Role.ADMIN);
            userDAO.update(user);
        }

        return given().contentType(ContentType.JSON)
                .body(Map.of("username", username, "password", "secret123"))
                .post("/auth/login")
                .then().statusCode(200)
                .extract().path("token");
    }
}
