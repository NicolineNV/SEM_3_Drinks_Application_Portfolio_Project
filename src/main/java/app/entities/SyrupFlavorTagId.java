package app.entities;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class SyrupFlavorTagId implements Serializable{

    private Long syrupId;
    private Long flavorTagId;

    public SyrupFlavorTagId (){
    }

    public SyrupFlavorTagId (Long syrupId, Long flavorTagId){
        this.syrupId = syrupId;
        this.flavorTagId = flavorTagId;
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof SyrupFlavorTagId that)) return false;
        return Objects.equals(syrupId, that.syrupId) && Objects.equals(flavorTagId, that.flavorTagId);
    }

    @Override
    public int hashCode(){
        return Objects.hash(syrupId, flavorTagId);
    }
}
