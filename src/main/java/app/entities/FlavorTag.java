package app.entities;

import jakarta.persistence.*;
import lombok.Getter;

@Getter

@Entity
@Table(name="flavor_tag")
public class FlavorTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    public FlavorTag (){
    }

    public FlavorTag (String name){
        this.name = name;
    }

    public void setName(String name){
        this.name = name;
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof FlavorTag that)) return false;
        return name != null && name.equals(that.name);
    }

    @Override
    public int hashCode(){
        return name != null ? name.hashCode() : 0;
    }
}


