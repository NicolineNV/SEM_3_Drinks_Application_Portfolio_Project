package app.dto;

import app.entities.Cocktail;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor

public class CocktailDTO {

    private Long id;
    private String name;
    private String description;
    private String instructions;
    private String cocktailFamily;
    private String spirit;
    private String liqueur;
    private String mixer;
    private String syrup;
    private String garnish;

    public static CocktailDTO fromEntity (Cocktail c) {
        return new CocktailDTO (
                c.getId(),
                c.getName(),
                c.getDescription(),
                c.getInstructions(),
                c.getCocktailFamily() != null ? c.getCocktailFamily().getName() : null,
                c.getSpirit() != null ? c.getSpirit().getName() : null,
                c.getLiqueur() != null ? c.getLiqueur().getName() : null,
                c.getMixer() != null ? c.getMixer().getName() : null,
                c.getSyrup() != null ? c.getSyrup().getName() : null,
                c.getGarnish() != null ? c.getGarnish().getName() : null
        );
    }
}
