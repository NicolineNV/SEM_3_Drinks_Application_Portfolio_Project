package app.dao;

import app.entities.Liqueur;
import jakarta.persistence.EntityManagerFactory;

public class LiqueurDAO extends AbstractDAO<Liqueur, Long> {

    public LiqueurDAO(){
        super(Liqueur.class);
    }

    public LiqueurDAO (EntityManagerFactory emf) {
        super(Liqueur.class, emf);
    }
}
