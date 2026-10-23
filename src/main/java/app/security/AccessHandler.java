package app.security;

import app.exceptions.ApiException;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.security.RouteRole;

import java.util.Set;

public class AccessHandler {

    public static final String USERNAME_ATTR = "username";
    public static final String ROLE_ATTR = "role";

    private final TokenSecurity tokenSecurity;

    public AccessHandler(TokenSecurity tokenSecurity) {
        this.tokenSecurity = tokenSecurity;
    }

    // Runs before every matched route and checks the role declared on that route
    public void handle(Context ctx) {
        Set<RouteRole> allowed = ctx.routeRoles();

        // Public endpoint
        if (allowed.contains(AccessRole.ANYONE)) {
            return;
        }

        String header = ctx.header("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Missing token");
        }

        TokenSecurity.TokenData data = tokenSecurity.verify(header.substring("Bearer ".length()));

        AccessRole userRole;
        try {
            userRole = AccessRole.valueOf(data.role());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }

        // An admin may use everything. A route without roles just requires being logged in.
        boolean permitted = allowed.isEmpty() || userRole == AccessRole.ADMIN || allowed.contains(userRole);
        if (!permitted) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Insufficient permissions");
        }

        ctx.attribute(USERNAME_ATTR, data.username());
        ctx.attribute(ROLE_ATTR, userRole);
    }
}
