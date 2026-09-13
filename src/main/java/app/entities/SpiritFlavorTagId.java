package app.entities;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class SpiritFlavorTagId implements Serializable{

    private Long spiritId;
    private Long flavorTagId;

    public SpiritFlavorTagId (){
    }

    public SpiritFlavorTagId (Long spiritId, Long flavorTagId){
        this.spiritId = spiritId;
        this.flavorTagId = flavorTagId;
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof SpiritFlavorTagId that)) return false;
        return Objects.equals(spiritId, that.spiritId) && Objects.equals(flavorTagId, that.flavorTagId);
    }

    @Override
    public int hashCode(){
        return Objects.hash(spiritId, flavorTagId);
    }
}
