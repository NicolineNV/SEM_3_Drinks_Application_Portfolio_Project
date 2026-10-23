package app.controllers;

import app.dto.AuthResponseDTO;
import app.dto.LoginRequestDTO;
import app.dto.RegisterRequestDTO;
import app.dto.UserDTO;
import app.security.TokenSecurity;
import app.services.UserService;
import io.javalin.http.HttpStatus;
import jakarta.persistence.EntityManagerFactory;

import io.javalin.http.Context;

public class AuthController {

    private final UserService userService;
    private final TokenSecurity tokenSecurity;

    public AuthController(EntityManagerFactory emf, TokenSecurity tokenSecurity) {
        this.userService = new UserService(emf);
        this.tokenSecurity = tokenSecurity;
    }

    public  void register(Context ctx) {
        RegisterRequestDTO requestDTO = ctx.bodyValidator(RegisterRequestDTO.class)
                .check(r -> r.username() != null && r.username().length() >= 3 &&
                        r.username().length() <= 25, "username must be 3-25 characters")
                .check(r -> r.email() != null && r.email().contains("@") &&
                        r.email().length() <= 255, "email is not valid")
                .check(r -> r.password() != null && r.password().length() >= 8 &&
                        r.password().length() <= 72, "password must be 8-72 characters")
                .get();

        UserDTO created = userService.register(requestDTO.username(), requestDTO.email(), requestDTO.password());
        ctx.status(HttpStatus.CREATED).json(created);
    }

    public void login(Context ctx) {
        LoginRequestDTO requestDTO = ctx.bodyValidator(LoginRequestDTO.class)
                .check(r -> r.username() != null && !r.username().isBlank(),
                        "username is required")
                .check(r -> r.password() != null && !r.password().isEmpty(),
                        "password is required")
                .get();

        UserDTO user = userService.login(requestDTO.username(), requestDTO.password());
        String token = tokenSecurity.createToken(user.getUsername(), user.getRole().name());
        ctx.json(new AuthResponseDTO(token, user));
    }
}
