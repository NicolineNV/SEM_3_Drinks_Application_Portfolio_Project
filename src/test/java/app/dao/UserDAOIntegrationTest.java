package app.dao;

import app.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserDAOIntegrationTest extends IntegrationTestBase {

    private UserDAO userDAO;

    @BeforeEach
    void setup() {
        userDAO = new UserDAO(emf);
    }

    @Test
    void createAndRetrieveUser() {
        User saved = userDAO.create(new User("nicoline", "nico@test.com", "hashed_pw"));

        Optional<User> found = userDAO.getById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("nicoline", found.get().getUsername());
    }

    @Test // checks that it can actually find an existing user
    void findByUsernameIfExist() {
        userDAO.create(new User("nicoline", "nico@exist_test.com", "hashed_pw_existing"));

        Optional<User> found = userDAO.findByUsername("nicoline");

        assertTrue(found.isPresent());
        assertEquals("nico@exist_test.com", found.get().getEmail());
    }

    @Test // checks that it returns empty when user does not exist
    void findByUsernameIfNotExist() {
        Optional<User> found = userDAO.findByUsername("findes-ikke");

        assertTrue(found.isEmpty());
    }

    @Test
    void deleteRemovesUser() {
        User saved = userDAO.create(new User("tempUser", "temp@test.com", "temp_hashed_pw"));

        userDAO.delete(saved.getId());

        assertTrue(userDAO.getById(saved.getId()).isEmpty());
    }

    @Test
    void updatePersistsChanges() {
        User saved = userDAO.create(new User("nicolineTwo", "old_mail@test.com", "hashed_pw_two"));

        saved.setEmail("newMail@test.com");
        userDAO.update(saved);

        Optional<User> found = userDAO.getById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("newMail@test.com", found.get().getEmail());
    }


}
