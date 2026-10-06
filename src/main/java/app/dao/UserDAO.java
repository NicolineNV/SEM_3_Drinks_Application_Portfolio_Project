package app.dao;

import app.entities.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Optional;

public class UserDAO extends AbstractDAO<User, Long> {

    public UserDAO() {
        super(User.class);
    }

    public UserDAO(EntityManagerFactory emf) {
        super(User.class, emf);
    }

    public Optional<User> findByUsername(String username) {
        try (EntityManager em = emf.createEntityManager()) {
            String jpql = "SELECT u FROM User u WHERE u.username = :username";
            List<User> results = em.createQuery(jpql, User.class)
                    .setParameter("username", username)
                    .getResultList();
            return results.stream().findFirst();
        }
    }
}
