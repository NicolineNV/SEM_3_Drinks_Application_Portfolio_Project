package app.dao;

import app.entities.*;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CocktailDAOIntegrationTest extends IntegrationTestBase{

    private CocktailDAO cocktailDAO;
    private CocktailFamilyDAO familyDAO;
    private SpiritDAO spiritDAO;
    private SyrupDAO syrupDAO;
    private FlavorTagDAO flavorTagDAO;

    @BeforeEach
    void setup() {
        cocktailDAO = new CocktailDAO(emf);
        familyDAO = new CocktailFamilyDAO(emf);
        spiritDAO = new SpiritDAO(emf);
        syrupDAO = new SyrupDAO(emf);
        flavorTagDAO = new FlavorTagDAO(emf);
    }

    // -------------- Helping methods for testdata --------------

    private CocktailFamily createFamily(String name) {
        return familyDAO.create(new CocktailFamily(name, "test family"));
    }

    private Spirit createSpirit(String name) {
        return spiritDAO.create(new Spirit(name, "test spirit"));
    }

    private Cocktail createCocktail(String name, CocktailFamily family, Spirit spirit) {
        Cocktail cocktail = new Cocktail(name, "test description", "test instructions", family);
        cocktail.setSpirit(spirit);
        return cocktailDAO.create(cocktail);
    }

    // -------------- Optional relations and JOIN FETCH --------------

    @Test // Test that cocktails without optional ingredients is fetched regardless by getByIdWithDetails()
    void cocktailNoOptionalIngredients() {
        CocktailFamily family = createFamily("Classics");
        Spirit whiskey = createSpirit("Whiskey");
        Cocktail saved = createCocktail("Old Fashioned", family, whiskey);

        Optional<Cocktail> found = cocktailDAO.getByIdWithDetails(saved.getId());

        assertTrue(found.isPresent());
        Cocktail cocktail = found.get();

        // Works without exceptions because JOIN FETCH fetched the relations
        assertEquals("Whiskey", cocktail.getSpirit().getName());
        assertEquals("Classics", cocktail.getCocktailFamily().getName());
        assertNull(cocktail.getLiqueur());
        assertNull(cocktail.getMixer());
        assertNull(cocktail.getSyrup());
        assertNull(cocktail.getGarnish());
    }

    @Test // Test that getAllWithDetails() includes cocktails missing optional relations
    void getAllWithDetails() {
        CocktailFamily family = createFamily("Mixed");
        createCocktail("Old Fashioned", family, createSpirit("Whiskey"));

        // A mocktail without spirit, only syrup
        Syrup syrup = syrupDAO.create(new Syrup("Simple Syrup", "test syrup"));
        Cocktail mocktail = new Cocktail("Virgin Mojito", "description", "instructions", family);
        mocktail.setSyrup(syrup);
        cocktailDAO.create(mocktail);

        List<Cocktail> all = cocktailDAO.getAllWithDetails();

        // Proves that LEFT JOIN FETCH does not disregard cocktails with missing relations
        assertEquals(2, all.size());
    }

    @Test // Test that shows why using JOIN FETCH is necessary
    void plainGetByIdLeavesRelationsLazy() {
        CocktailFamily family = createFamily("Classics");
        Cocktail saved = createCocktail("Old Fashioned", family, createSpirit("Whiskey"));

        Cocktail found = cocktailDAO.getById(saved.getId()).orElseThrow();

        // EntityManager is closed, and spirit were never fetched
        assertThrows(LazyInitializationException.class, () -> found.getSpirit().getName());

    }

    // -------------- Quiz-candidates: filtering --------------

    @Test // Test that the family filter is hard = only chosen family is selected
    void candidatesFilteredByFamily() {
        CocktailFamily sours = createFamily("Sours");
        CocktailFamily tiki = createFamily("Tiki");
        Spirit rum = createSpirit("Rum");
        createCocktail("Whiskey Sour", sours, rum);
        createCocktail("Mai Tai", tiki, rum);

        List<CocktailCandidate> candidates = cocktailDAO.getCandidatesForQuiz(sours.getId(), null);

        assertEquals(1, candidates.size());
        assertEquals("Whiskey Sour", candidates.get(0).getCocktail().getName());
    }

    @Test // Test the "can choose preferred spirit" logic - users choice
    void spiritFilterIsOptional() {
        CocktailFamily family = createFamily("Classics");
        Spirit whiskey = createSpirit("Whiskey");
        Spirit gin = createSpirit("Gin");
        createCocktail("Old Fashioned", family, whiskey);
        createCocktail("Martini", family, gin);

        List<CocktailCandidate> onlyWhiskey = cocktailDAO.getCandidatesForQuiz(family.getId(), whiskey.getId());
        List<CocktailCandidate> anySpirit = cocktailDAO.getCandidatesForQuiz(family.getId(), null);

        assertEquals(1, onlyWhiskey.size());
        assertEquals("Old Fashioned", onlyWhiskey.get(0).getCocktail().getName());
        assertEquals(2, anySpirit.size());
    }

    // -------------- Quiz-candidates: flavor-profiles --------------

    @Test // Checks that candidates flavor profile sums intensities and ranks dimensions
    void candidateFlavorProfile () {

        // Important - flavor tags needs to be created in this order!
        FlavorTag sweet = flavorTagDAO.create(new FlavorTag("Sweet"));
        FlavorTag sour = flavorTagDAO.create(new FlavorTag("Sour"));
        FlavorTag bitter = flavorTagDAO.create(new FlavorTag("Bitter"));
        FlavorTag salty = flavorTagDAO.create(new FlavorTag("Salty"));
        FlavorTag spicy = flavorTagDAO.create(new FlavorTag("Spicy"));

        Spirit spirit = new Spirit("Test Spirit", "description");
        spirit.addFlavorTag(sweet, 1);
        spirit.addFlavorTag(sour, 2);
        spirit.addFlavorTag(bitter, 4);
        spirit.addFlavorTag(salty, 1);
        spirit.addFlavorTag(spicy, 1);
        spiritDAO.create(spirit);

        Syrup syrup = new Syrup("Test Syrup", "description");
        syrup.addFlavorTag(sweet, 5);
        syrup.addFlavorTag(sour, 1);
        syrup.addFlavorTag(bitter, 1);
        syrup.addFlavorTag(salty, 1);
        syrup.addFlavorTag(spicy, 1);
        syrupDAO.create(syrup);

        CocktailFamily family = createFamily("Test family");
        Cocktail cocktail = new Cocktail("Test Cocktail", "description", "instructions", family);
        cocktail.setSpirit(spirit);
        cocktail.setSyrup(syrup);
        cocktailDAO.create(cocktail);

        List<CocktailCandidate> candidates = cocktailDAO.getCandidatesForQuiz(family.getId(), null);

        assertEquals(1, candidates.size());
        Map<Long, Integer> profile = candidates.get(0).getFlavorProfile();

        // Sums: Sweet 6, Bitter 5, Sour 3, Salty 2, Spicy 2
        // Ranking: Sweet 5, Bitter 4, Sour 3, Salty 2, Spicy 1
        // (It is a draw between Salty and Spicy, so the one with the lowest id wins. Salty was created first = lowest id)
        assertEquals(5, profile.get(sweet.getId()));
        assertEquals(4, profile.get(bitter.getId()));
        assertEquals(3, profile.get(sour.getId()));
        assertEquals(2, profile.get(salty.getId()));
        assertEquals(1, profile.get(spicy.getId()));
    }
}
