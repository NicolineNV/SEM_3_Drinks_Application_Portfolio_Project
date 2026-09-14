package app.dao;

import app.entities.Liqueur;

public class LiqueurDAO extends AbstractDAO<Liqueur, Long> {

    public LiqueurDAO(){
        super(Liqueur.class);
    }
}
