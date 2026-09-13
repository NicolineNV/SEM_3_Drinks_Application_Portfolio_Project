package app.entities;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class LiqueurFlavorTagId implements Serializable{

    private Long liqueurId;
    private Long flavorTagId;

    public LiqueurFlavorTagId (){
    }

    public LiqueurFlavorTagId (Long liqueurId, Long flavorTagId){
        this.liqueurId = liqueurId;
        this.flavorTagId = flavorTagId;
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof LiqueurFlavorTagId that)) return false;
        return Objects.equals(liqueurId, that.liqueurId) && Objects.equals(flavorTagId, that.flavorTagId);
    }

    @Override
    public int hashCode(){
        return Objects.hash(liqueurId, flavorTagId);
    }

}
