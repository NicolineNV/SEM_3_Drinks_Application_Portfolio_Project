package app.dao;

import app.entities.*;
import app.entities.cocktailmenu.CocktailMenu;
import app.entities.cocktailmenu.CocktailMenuId;
import app.entities.spirit.Spirit;
import app.entities.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CocktailMenuDAOIntegrationTest extends IntegrationTestBase {

    private CocktailMenuDAO menuDAO;
    private UserDAO userDAO;
    private CocktailDAO cocktailDAO;
    private CocktailFamily family;
    private Spirit spirit;

    @BeforeEach
    void setup() {
        menuDAO = new CocktailMenuDAO(emf);
        userDAO = new UserDAO(emf);
        cocktailDAO = new CocktailDAO(emf);
        family = new CocktailFamilyDAO(emf).create(new CocktailFamily("Classics", "test family"));
        spirit = new SpiritDAO(emf).create(new Spirit("Whiskey", "test spirit"));
    }

    // -------------- Helping methods for testdata --------------

    private User createUser(String username) {
        return userDAO.create(new User(username, username + "@test.dk", "hashed_pw"));
    }

    private Cocktail createCocktail (String name) {
        Cocktail cocktail = new Cocktail(name, "description", "instructions", family);
        cocktail.setSpirit(spirit);
        return cocktailDAO.create(cocktail);
    }

    // -------------- Test --------------

    @Test // Test to check that saved favorites can be found by a composite key
    void savedFavoriteCompositeKey() {
        User user = createUser("nicoline");
        Cocktail cocktail = createCocktail("Old Fashioned");

        menuDAO.create(new CocktailMenu(user, cocktail));

        Optional<CocktailMenu> found = menuDAO.getById(new CocktailMenuId(user.getId(), cocktail.getId()));

        assertTrue(found.isPresent());
        assertNotNull(found.get().getSavedAt()); // set automatically at @PrePersist
    }

    @Test // Test that getFavoritesByUser() returns cocktails with all details fetched
    void getFavoritesReturnsCocktails () {
        User user = createUser("nicoline");
        menuDAO.create(new CocktailMenu(user, createCocktail("Old Fashioned")));
        menuDAO.create(new CocktailMenu(user, createCocktail("Manhattan")));

        List<CocktailMenu> favorites = menuDAO.getFavoritesByUser(user.getId());

        assertEquals(2, favorites.size());
        // Only works without exceptions, because JOIN FETCH fetched cocktail and spirit
        assertEquals("Whiskey", favorites.getFirst().getCocktail().getSpirit().getName());
    }

    @Test
    void favoritesAreSeparatedPerUser() {
        User nicoline = createUser("nicoline");
        User other = createUser("other");
        menuDAO.create(new CocktailMenu(nicoline, createCocktail("Old Fashioned")));

        assertEquals(1, menuDAO.getFavoritesByUser(nicoline.getId()).size());
        assertTrue(menuDAO.getFavoritesByUser(other.getId()).isEmpty());
    }

    @Test // Test that ensures that delete only removes the link and not the user or cocktail
    void deleteRemovesOnlyLink () {
        User user = createUser("nicoline");
        Cocktail cocktail = createCocktail("Old Fashioned");
        CocktailMenuId id = new CocktailMenuId(user.getId(), cocktail.getId());
        menuDAO.create(new CocktailMenu(user, cocktail));

        menuDAO.delete(id);

        assertTrue(menuDAO.getById(id).isEmpty());
        assertTrue(userDAO.getById(user.getId()).isPresent());
        assertTrue(cocktailDAO.getById(cocktail.getId()).isPresent());
    }

    @Test // Test that ensures that saving the same cocktail twice is rejected by the database
    void savingTheSameTwice () {
        User user = createUser("nicoline");
        Cocktail cocktail = createCocktail("Old Fashioned");
        menuDAO.create(new CocktailMenu(user, cocktail));

        assertThrows(RuntimeException.class,
                () -> menuDAO.create(new CocktailMenu(user, cocktail)));
    }
}
