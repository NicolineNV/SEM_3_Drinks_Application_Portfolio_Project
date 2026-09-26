package app.dao;

import app.entities.CocktailMenu;
import app.entities.CocktailMenuId;
import jakarta.persistence.EntityManager;

import java.util.List;

public class CocktailMenuDAO extends AbstractDAO<CocktailMenu, CocktailMenuId> {

    public CocktailMenuDAO() {
        super(CocktailMenu.class);
    }

    public List<CocktailMenu> getFavoritesByUser(Long userId) {
        try (EntityManager em = emf.createEntityManager()) {
            String jpql = "SELECT cm FROM CocktailMenu cm " +
                    "JOIN FETCH cm.cocktail c " + // JOIN FETCH without LEFT because the relation is NOT NULL - CocktailMenu always has at least one Cocktail
                    "LEFT JOIN FETCH c.cocktailFamily " + // Rest with LEFT because can be NULL
                    "LEFT JOIN FETCH c.spirit " +
                    "LEFT JOIN FETCH c.liqueur " +
                    "LEFT JOIN FETCH c.mixer " +
                    "LEFT JOIN FETCH c.syrup " +
                    "LEFT JOIN FETCH c.garnish " +
                    "WHERE cm.user.id = :userId";
            return em.createQuery(jpql, CocktailMenu.class)
                    .setParameter("userId", userId)
                    .getResultList();
        }
    }
}
