package app.dao;

import app.entities.spirit.Spirit;
import jakarta.persistence.EntityManagerFactory;

public class SpiritDAO extends AbstractDAO<Spirit, Long> {

    public SpiritDAO(){
        super(Spirit.class);
    }

    public SpiritDAO (EntityManagerFactory emf) {
        super(Spirit.class, emf);
    }
}
