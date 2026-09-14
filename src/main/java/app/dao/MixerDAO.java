package app.dao;

import app.entities.Mixer;

public class MixerDAO extends AbstractDAO<Mixer, Long> {

    public MixerDAO(){
        super(Mixer.class);
    }
}
