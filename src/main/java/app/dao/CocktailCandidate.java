package app.dao;

import app.entities.Cocktail;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;


@AllArgsConstructor
@Getter
public class CocktailCandidate {

    private final Cocktail cocktail;
    private final Map<Long, Integer> flavorProfile;

}
