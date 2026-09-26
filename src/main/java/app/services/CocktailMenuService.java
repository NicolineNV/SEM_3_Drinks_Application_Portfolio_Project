package app.services;

import app.dao.CocktailDAO;
import app.dao.CocktailMenuDAO;
import app.dao.UserDAO;
import app.dto.CocktailDTO;
import app.entities.Cocktail;
import app.entities.CocktailMenu;
import app.entities.CocktailMenuId;
import app.entities.User;
import app.exceptions.ApiException;
import io.javalin.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;

public class CocktailMenuService {

    private final UserDAO userDAO = new UserDAO();
    private final CocktailDAO cocktailDAO = new CocktailDAO();
    private final CocktailMenuDAO cocktailMenuDAO = new CocktailMenuDAO();

    public CocktailDTO saveFavorite (Long userId, Long cocktailId) {
        CocktailMenuId id = new CocktailMenuId(userId, cocktailId);
        if (cocktailMenuDAO.getById(id).isPresent()) {
            throw new ApiException(HttpStatus.CONFLICT, "The cocktail is already saved as a favorit");
        }
        User user = userDAO.getById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User does not exist: " + userId));
        Cocktail cocktail = cocktailDAO.getByIdWithDetails(cocktailId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Cocktail does not exist: " + cocktailId));

        cocktailMenuDAO.create(new CocktailMenu(user, cocktail));
        return CocktailDTO.fromEntity(cocktail);
    }

    public List<CocktailDTO> getFavorites (Long userId) {
        return cocktailMenuDAO.getFavoritesByUser(userId).stream()
                .map(cm -> CocktailDTO.fromEntity(cm.getCocktail()))
                .collect(Collectors.toList());
    }

    public void removeFavorite (Long userId, Long cocktailId) {
        CocktailMenuId id = new CocktailMenuId(userId, cocktailId);
        cocktailMenuDAO.getById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "The favorite cocktail does not exist"));
        cocktailMenuDAO.delete(id);
    }

}
