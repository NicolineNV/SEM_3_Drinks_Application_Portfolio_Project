package app.dao;

import app.entities.FlavorTag;
import jakarta.persistence.EntityManagerFactory;

public class FlavorTagDAO extends AbstractDAO<FlavorTag, Long> {

    public FlavorTagDAO(){
        super(FlavorTag.class);
    }

    public FlavorTagDAO (EntityManagerFactory emf) {
        super(FlavorTag.class, emf);
    }
}
