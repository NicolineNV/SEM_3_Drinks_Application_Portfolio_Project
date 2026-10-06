package app.dao;

import app.entities.Mixer;
import jakarta.persistence.EntityManagerFactory;

public class MixerDAO extends AbstractDAO<Mixer, Long> {

    public MixerDAO(){
        super(Mixer.class);
    }

    public MixerDAO (EntityManagerFactory emf) {
        super(Mixer.class, emf);
    }
}
