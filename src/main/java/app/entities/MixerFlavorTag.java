package app.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter

@Entity
@Table(name = "mixer_flavor_tag")
public class MixerFlavorTag {

    @EmbeddedId
    private MixerFlavorTagId id = new MixerFlavorTagId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("mixerId")
    @JoinColumn(name = "mixer_id")
    private Mixer mixer;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("flavorTagId")
    @JoinColumn(name = "flavor_tag_id")
    private FlavorTag flavorTag;

    @Column(nullable = false)
    private Integer intensity;

    public MixerFlavorTag(Mixer mixer, FlavorTag flavorTag, Integer intensity){
        this.mixer = mixer;
        this.flavorTag = flavorTag;
        this.intensity = intensity;
    }

    public void setIntensity(Integer intensity){
        this.intensity = intensity;
    }
}
