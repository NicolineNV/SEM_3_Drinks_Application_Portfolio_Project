package app.dao;

import app.entities.Cocktail;

public class CocktailDAO extends AbstractDAO<Cocktail, Long> {

    public CocktailDAO(){
        super(Cocktail.class);
    }
}
