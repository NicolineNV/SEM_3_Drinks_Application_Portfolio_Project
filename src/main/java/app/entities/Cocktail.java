package app.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter

@Entity
@Table(name = "cocktail")
public class Cocktail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String instructions;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "cocktail_family_id", nullable = false)
    private CocktailFamily cocktailFamily;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "spirit_id", nullable = true)
    private Spirit spirit;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "liqueur_id", nullable = true)
    private Liqueur liqueur;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "mixer_id", nullable = true)
    private Mixer mixer;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "syrup_id", nullable = true)
    private Syrup syrup;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "garnish_id", nullable = true)
    private Garnish garnish;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Cocktail(String name, String description, String instructions, CocktailFamily cocktailFamily){
        this.name = name;
        this.description = description;
        this.instructions = instructions;
        this.cocktailFamily = cocktailFamily;
    }

    @PrePersist
    protected void onCreate(){
        this.createdAt = LocalDateTime.now();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public void setCocktailFamily(CocktailFamily cocktailFamily) {
        this.cocktailFamily = cocktailFamily;
    }

    public void setSpirit(Spirit spirit) {
        this.spirit = spirit;
    }

    public void setLiqueur(Liqueur liqueur) {
        this.liqueur = liqueur;
    }

    public void setMixer(Mixer mixer) {
        this.mixer = mixer;
    }

    public void setSyrup(Syrup syrup) {
        this.syrup = syrup;
    }

    public void setGarnish(Garnish garnish) {
        this.garnish = garnish;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cocktail that)) return false;
        return name != null && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
