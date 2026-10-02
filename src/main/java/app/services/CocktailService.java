package app.services;

import app.dao.*;
import app.dto.CocktailDTO;
import app.dto.CreateCocktailDTO;
import app.dto.UserDTO;
import app.entities.*;
import app.exceptions.ApiException;
import io.javalin.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;

public class CocktailService {

    private final CocktailDAO cocktailDAO;
    private final CocktailFamilyDAO cocktailFamilyDAO;
    private final SpiritDAO spiritDAO;
    private final LiqueurDAO liqueurDAO;
    private final MixerDAO mixerDAO;
    private final SyrupDAO syrupDAO;
    private final GarnishDAO garnishDAO;
    private final UserService userService;

    public CocktailService() {

        this.cocktailDAO = new CocktailDAO();
        this.cocktailFamilyDAO = new CocktailFamilyDAO();
        this.spiritDAO = new SpiritDAO();
        this.liqueurDAO = new LiqueurDAO();
        this.mixerDAO = new MixerDAO();
        this.syrupDAO = new SyrupDAO();
        this.garnishDAO = new GarnishDAO();
        this.userService = new UserService();

    }


    public CocktailDTO getById (Long id) {
        Cocktail cocktail = cocktailDAO.getByIdWithDetails(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Cocktail does not exist: " + id));

        return CocktailDTO.fromEntity(cocktail);
    }


    public List<CocktailDTO> getAll() {
        return cocktailDAO.getAllWithDetails().stream()
                .map(CocktailDTO :: fromEntity)
                .collect(Collectors.toList());
    }


    // Admin method - for admin to create new cocktail in database
    public CocktailDTO createCocktail(CreateCocktailDTO request, UserDTO admin) {

        if (!userService.isAdmin(admin)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Only admin can create new cocktails");
        }

        if (request.getSpiritId() == null && request.getLiqueurId() == null
                && request.getMixerId() == null && request.getSyrupId() == null) {

            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "A cocktail shall have at least one ingredient (spirit, liqueur, mixer or syrup)");
        }

        CocktailFamily family = cocktailFamilyDAO.getById(request.getCocktailFamilyId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
                        "Cocktail-family does not exist: " + request.getCocktailFamilyId()));

        Cocktail cocktail = new Cocktail(request.getName(), request.getDescription(),
                request.getInstructions(), family);

        if (request.getSpiritId() != null) {
            cocktail.setSpirit(spiritDAO.getById(request.getSpiritId())
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
                            "Spirit does not exist: " + request.getSpiritId())));
        }

        if (request.getLiqueurId() != null) {
            cocktail.setLiqueur(liqueurDAO.getById(request.getLiqueurId())
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
                            "Liqueur does not exist: " + request.getLiqueurId())));
        }

        if (request.getMixerId() != null) {
            cocktail.setMixer(mixerDAO.getById(request.getMixerId())
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
                            "Mixer does not exist: " + request.getMixerId())));
        }

        if (request.getSyrupId() != null) {
            cocktail.setSyrup(syrupDAO.getById(request.getSyrupId())
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
                            "Syrup does not exist: " + request.getSyrupId())));
        }

        if (request.getGarnishId() != null) {
            cocktail.setGarnish(garnishDAO.getById(request.getGarnishId())
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
                            "Garnish does not exist: " + request.getGarnishId())));
        }

        Cocktail saved = cocktailDAO.create(cocktail);
        return CocktailDTO.fromEntity(saved);
    }


}
