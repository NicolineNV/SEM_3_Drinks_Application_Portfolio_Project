package app.dao;

import app.entities.FlavorTag;

public class FlavorTagDAO extends AbstractDAO<FlavorTag, Long> {

    public FlavorTagDAO(){
        super(FlavorTag.class);
    }
}
