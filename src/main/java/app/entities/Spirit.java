package app.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter

@Entity
@Table(name ="spirit")
public class Spirit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "spirit", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SpiritFlavorTag> flavorTags = new ArrayList<>();

    public Spirit(String name, String description){
        this.name = name;
        this.description = description;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public void addFlavorTag (FlavorTag tag, int intensity){
        flavorTags.add(new SpiritFlavorTag(this, tag, intensity));
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof Spirit that)) return false;
        return name != null && name.equals(that.name);
    }

    @Override
    public int hashCode(){
        return name != null ? name.hashCode() : 0;
    }
}
