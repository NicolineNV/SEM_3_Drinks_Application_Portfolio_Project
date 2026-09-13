package app.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter

@Entity
@Table(name = "liqueur_flavor_tag")
public class LiqueurFlavorTag {

    @EmbeddedId
    private LiqueurFlavorTagId id = new LiqueurFlavorTagId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("liqueurId")
    @JoinColumn(name = "liqueur_id")
    private Liqueur liqueur;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("flavorTagId")
    @JoinColumn(name = "flavor_tag_id")
    private FlavorTag flavorTag;

    @Column(nullable = false)
    private Integer intensity;

    public LiqueurFlavorTag(Liqueur liqueur, FlavorTag flavorTag, Integer intensity){
        this.liqueur = liqueur;
        this.flavorTag = flavorTag;
        this.intensity = intensity;
    }

    public void setIntensity(Integer intensity){
        this.intensity = intensity;
    }
}
