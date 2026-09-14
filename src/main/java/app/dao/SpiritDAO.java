package app.dao;

import app.entities.Spirit;

public class SpiritDAO extends AbstractDAO<Spirit, Long> {

    public SpiritDAO(){
        super(Spirit.class);
    }
}
