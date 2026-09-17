package app.services;

import app.dao.UserDAO;
import app.entities.Role;
import app.entities.User;
import app.exceptions.ApiException;

import org.mindrot.jbcrypt.BCrypt;

public class UserService {

    private final UserDAO userDAO;

    public UserService(){
        this.userDAO = new UserDAO();
    }

    public User register(String username, String email, String rawPassword) {
        if (userDAO.findByUsername(username).isPresent()) {
            throw new ApiException(409, "Dette brugernavn er allerede i brug: " + username);
        }

        String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
        User user = new User(username, email, hashedPassword);
        return userDAO.create(user);
    }

    public User login(String username, String rawPassword) {
        User user = userDAO.findByUsername(username)
                .orElseThrow(() -> new ApiException(401, "Forkert brugernavn eller adgangskode"));

        if (!BCrypt.checkpw(rawPassword, user.getPasswordHash())) {
            throw new ApiException(401, "Forkert brugernavn eller adgangskode");
        }

        return user;
    }

    public boolean isAdmin(User user) {
        return user.getRole() == Role.ADMIN;
    }
}
