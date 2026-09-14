package app.dao;

import app.entities.Garnish;

public class GarnishDAO extends AbstractDAO<Garnish, Long> {

    public GarnishDAO(){
        super(Garnish.class);
    }
}
