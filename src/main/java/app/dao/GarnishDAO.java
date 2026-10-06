package app.dao;

import app.entities.Garnish;
import jakarta.persistence.EntityManagerFactory;

public class GarnishDAO extends AbstractDAO<Garnish, Long> {

    public GarnishDAO(){
        super(Garnish.class);
    }

    public GarnishDAO (EntityManagerFactory emf) {
        super(Garnish.class, emf);
    }
}
