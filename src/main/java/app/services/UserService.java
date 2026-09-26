package app.services;

import app.dao.UserDAO;
import app.dto.UserDTO;
import app.entities.Role;
import app.entities.User;
import app.exceptions.ApiException;
import io.javalin.http.HttpStatus;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserDAO userDAO;

    public UserService(){
        this.userDAO = new UserDAO();
    }

    public UserDTO register(String username, String email, String rawPassword) {
        if (userDAO.findByUsername(username).isPresent()) {
            throw new ApiException(HttpStatus.CONFLICT, "Username already in use: " + username);
        }

        String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
        User savedUser = userDAO.create(new User(username, email, hashedPassword));
        logger.info("New user created: {}", username);
        return UserDTO.fromEntity(savedUser);
    }

    public UserDTO login(String username, String rawPassword) {
        User user = userDAO.findByUsername(username)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Wrong username or password"));

        if (!BCrypt.checkpw(rawPassword, user.getPasswordHash())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Wrong username or password"); // Gives message about wrong username or password no matter which is wrong
        }

        return UserDTO.fromEntity(user);
    }

    public boolean isAdmin(UserDTO user) {
        return user.getRole() == Role.ADMIN;
    }
}
