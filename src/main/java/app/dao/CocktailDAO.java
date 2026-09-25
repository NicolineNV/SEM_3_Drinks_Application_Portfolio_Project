package app.dao;

import app.entities.Cocktail;
import jakarta.persistence.EntityManager;

import java.util.Optional;

public class CocktailDAO extends AbstractDAO<Cocktail, Long> {

    public CocktailDAO(){
        super(Cocktail.class);
    }

    public Optional<Cocktail> getByIdWithDetails(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            String jpql = "SELECT c FROM Cocktail c " +
                    "LEFT JOIN FETCH c.cocktailFamily " + // LEFT JOIN FETCH because can be null
                    "LEFT JOIN FETCH c.spirit " +
                    "LEFT JOIN FETCH c.liqueur " +
                    "LEFT JOIN FETCH c.mixer " +
                    "LEFT JOIN FETCH c.syrup " +
                    "LEFT JOIN FETCH c.garnish " +
                    "WHERE c.id = :id";
            return em.createQuery(jpql, Cocktail.class)
                    .setParameter("id", id)
                    .getResultStream()
                    .findFirst();
        }
    }
}
