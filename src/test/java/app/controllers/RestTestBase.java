package app.controllers;

import app.config.ApplicationConfig;
import app.dao.IntegrationTestBase;
import app.routes.Routes;
import app.security.AccessHandler;
import app.security.TokenSecurity;
import io.javalin.Javalin;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

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
}
