package app.entities;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class MixerFlavorTagId implements Serializable{

    private Long mixerId;
    private Long flavorTagId;

    public MixerFlavorTagId (){
    }

    public MixerFlavorTagId (Long mixerId, Long flavorTagId){
        this.mixerId = mixerId;
        this.flavorTagId = flavorTagId;
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof MixerFlavorTagId that)) return false;
        return Objects.equals(mixerId, that.mixerId) && Objects.equals(flavorTagId, that.flavorTagId);
    }

    @Override
    public int hashCode(){
        return Objects.hash(mixerId, flavorTagId);
    }
}
