package app.dao;

import app.entities.CocktailFamily;
import jakarta.persistence.EntityManagerFactory;

public class CocktailFamilyDAO extends AbstractDAO<CocktailFamily, Long> {

    public CocktailFamilyDAO(){
        super(CocktailFamily.class);
    }

    public CocktailFamilyDAO (EntityManagerFactory emf) {
        super(CocktailFamily.class, emf);
    }
}
