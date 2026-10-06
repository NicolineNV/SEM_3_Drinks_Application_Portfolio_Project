package app.dao;

import app.config.TestHibernateConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;

public abstract class IntegrationTestBase {

    protected static EntityManagerFactory emf;

    @BeforeAll
    static void initEmf() {
        emf = TestHibernateConfig.getEntityManagerFactory();
    }

    @AfterEach
    void cleanDatabase() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            // Are deleted in order, so foreign keys do not block (children before parents)
            em.createQuery("DELETE FROM CocktailMenu").executeUpdate();
            em.createQuery("DELETE FROM SpiritFlavorTag").executeUpdate();
            em.createQuery("DELETE FROM LiqueurFlavorTag").executeUpdate();
            em.createQuery("DELETE FROM MixerFlavorTag").executeUpdate();
            em.createQuery("DELETE FROM SyrupFlavorTag").executeUpdate();
            em.createQuery("DELETE FROM Cocktail").executeUpdate();
            em.createQuery("DELETE FROM Spirit").executeUpdate();
            em.createQuery("DELETE FROM Liqueur").executeUpdate();
            em.createQuery("DELETE FROM Mixer").executeUpdate();
            em.createQuery("DELETE FROM Syrup").executeUpdate();
            em.createQuery("DELETE FROM Garnish").executeUpdate();
            em.createQuery("DELETE FROM CocktailFamily").executeUpdate();
            em.createQuery("DELETE FROM FlavorTag").executeUpdate();
            em.createQuery("DELETE FROM User").executeUpdate();
            em.getTransaction().commit();
        }
    }
}
