package app.dao;

import app.entities.Syrup;
import jakarta.persistence.EntityManagerFactory;

public class SyrupDAO extends AbstractDAO<Syrup, Long> {

    public SyrupDAO(){
        super(Syrup.class);
    }

    public SyrupDAO (EntityManagerFactory emf) {
        super(Syrup.class, emf);
    }
}
