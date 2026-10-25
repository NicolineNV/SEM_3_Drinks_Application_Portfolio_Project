package app.services;

import app.config.HibernateConfig;
import app.dao.CocktailDAO;
import app.dao.CocktailMenuDAO;
import app.dao.UserDAO;
import app.dto.CocktailDTO;
import app.entities.Cocktail;
import app.entities.cocktailmenu.CocktailMenu;
import app.entities.cocktailmenu.CocktailMenuId;
import app.entities.user.User;
import app.exceptions.ApiException;
import io.javalin.http.HttpStatus;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class CocktailMenuService {

    private final UserDAO userDAO;
    private final CocktailDAO cocktailDAO;
    private final CocktailMenuDAO cocktailMenuDAO;

    public CocktailMenuService() {
        this(HibernateConfig.getEntityManagerFactory());
    }

    // Dependency injection: the REST layer and the tests give the EntityManagerFactory
    public CocktailMenuService(EntityManagerFactory emf) {
        this.userDAO = new UserDAO(emf);
        this.cocktailDAO = new CocktailDAO(emf);
        this.cocktailMenuDAO = new CocktailMenuDAO(emf);
    }

    // ----- Methods that take the username from the token (used by controller) -----

    public CocktailDTO saveFavorite(String username, Long cocktailId) {
        return saveFavorite(userIdOf(username), cocktailId);
    }

    public List<CocktailDTO> getFavorites(String username) {
        return getFavorites(userIdOf(username));
    }

    public void removeFavorite(String username, Long cocktailId) {
        removeFavorite(userIdOf(username), cocktailId);
    }

    private Long userIdOf(String username) {
        return userDAO.findByUsername(username)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "User no longer exists"))
                .getId();
    }

    // ----- Original methods, unchanged -----

    public CocktailDTO saveFavorite(Long userId, Long cocktailId) {
        CocktailMenuId id = new CocktailMenuId(userId, cocktailId);
        if (cocktailMenuDAO.getById(id).isPresent()) {
            throw new ApiException(HttpStatus.CONFLICT, "The cocktail is already saved as a favorite");
        }
        User user = userDAO.getById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User does not exist: " + userId));
        Cocktail cocktail = cocktailDAO.getByIdWithDetails(cocktailId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Cocktail does not exist: " + cocktailId));

        cocktailMenuDAO.create(new CocktailMenu(user, cocktail));
        return CocktailDTO.fromEntity(cocktail);
    }

    public List<CocktailDTO> getFavorites(Long userId) {
        return cocktailMenuDAO.getFavoritesByUser(userId).stream()
                .map(cm -> CocktailDTO.fromEntity(cm.getCocktail()))
                .collect(Collectors.toList());
    }

    public void removeFavorite(Long userId, Long cocktailId) {
        CocktailMenuId id = new CocktailMenuId(userId, cocktailId);
        cocktailMenuDAO.getById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "The favorite cocktail does not exist"));
        cocktailMenuDAO.delete(id);
    }

}
