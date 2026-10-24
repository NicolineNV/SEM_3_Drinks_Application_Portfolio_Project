package app.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TokenSecurityTest {

    private static final String KEY = "0123456789abcdef0123456789abcdef0123456789abcdef";

    @Test
    void validTokenReturnsUserNameAndRole() {
        TokenSecurity ts = new TokenSecurity(KEY, "test", 60_000);
        String token = ts.createToken("nicoline", "ADMIN");

        TokenSecurity.TokenData data = ts.verify(token);

        assertEquals("nicoline", data.username());
        assertEquals("ADMIN", data.role());
    }
}
