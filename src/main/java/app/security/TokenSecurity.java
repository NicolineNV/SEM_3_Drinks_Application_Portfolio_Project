package app.security;

import app.exceptions.ApiException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.javalin.http.HttpStatus;

import java.util.Date;

public class TokenSecurity {

    public record TokenData(String username, String role) {}

    private final Algorithm algorithm;
    private final JWTVerifier verifier;
    private final String issuer;
    private final long expireMillis;

    public TokenSecurity(String secretKey, String issuer, long expireMillis) {
        this.algorithm = Algorithm.HMAC256(secretKey);
        this.verifier = JWT.require(algorithm).withIssuer(issuer).build();
        this.issuer = issuer;
        this.expireMillis = expireMillis;
    }

    public String createToken(String username, String role) {
        Date now = new Date();
        return JWT.create()
                .withSubject(username)
                .withIssuer(issuer)
                .withClaim("role", role)
                .withIssuedAt(now)
                .withExpiresAt(new Date(now.getTime() + expireMillis))
                .sign(algorithm);
    }

    public TokenData verify(String token) {
        try {
            DecodedJWT jwt = verifier.verify(token);
            return new TokenData(jwt.getSubject(), jwt.getClaim("role").toString());
        } catch (TokenExpiredException e) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Token expired");
        } catch (JWTVerificationException e) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }
    }
}
