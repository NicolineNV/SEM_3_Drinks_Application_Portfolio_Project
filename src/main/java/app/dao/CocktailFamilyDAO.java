package app.dao;

import app.entities.CocktailFamily;

public class CocktailFamilyDAO extends AbstractDAO<CocktailFamily, Long> {

    public CocktailFamilyDAO(){
        super(CocktailFamily.class);
    }
}
