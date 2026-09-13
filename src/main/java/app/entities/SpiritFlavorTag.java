package app.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter

@Entity
@Table(name = "spirit_flavor_tag")
public class SpiritFlavorTag {

    @EmbeddedId
    private SpiritFlavorTagId id = new SpiritFlavorTagId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("spiritId")
    @JoinColumn(name = "spirit_id")
    private Spirit spirit;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("flavorTagId")
    @JoinColumn(name = "flavor_tag_id")
    private FlavorTag flavorTag;

    @Column(nullable = false)
    private Integer intensity;

    public SpiritFlavorTag(Spirit spirit, FlavorTag flavorTag, Integer intensity){
        this.spirit = spirit;
        this.flavorTag = flavorTag;
        this.intensity = intensity;
    }

    public void setIntensity(Integer intensity){
        this.intensity = intensity;
    }
}
