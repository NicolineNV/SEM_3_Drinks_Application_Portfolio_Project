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
            String jpql = "SELECT cm FROM CocktailMenu cm WHERE cm.user.id = :userId";
            return em.createQuery(jpql, CocktailMenu.class)
                    .setParameter("userId", userId)
                    .getResultList();
        }
    }
}
