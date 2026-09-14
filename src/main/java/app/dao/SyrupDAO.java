package app.dao;

import app.entities.Syrup;

public class SyrupDAO extends AbstractDAO<Syrup, Long> {

    public SyrupDAO(){
        super(Syrup.class);
    }
}
