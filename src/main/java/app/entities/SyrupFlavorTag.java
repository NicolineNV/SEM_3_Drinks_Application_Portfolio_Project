package app.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter

@Entity
@Table(name = "syrup_flavor_tag")
public class SyrupFlavorTag {

    @EmbeddedId
    private SyrupFlavorTagId id = new SyrupFlavorTagId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("syrupId")
    @JoinColumn(name = "syrup_id")
    private Syrup syrup;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("flavorTagId")
    @JoinColumn(name = "flavor_tag_id")
    private FlavorTag flavorTag;

    @Column(nullable = false)
    private Integer intensity;

    public SyrupFlavorTag(Syrup syrup, FlavorTag flavorTag, Integer intensity){
        this.syrup = syrup;
        this.flavorTag = flavorTag;
        this.intensity = intensity;
    }

    public void setIntensity(Integer intensity){
        this.intensity = intensity;
    }
}
