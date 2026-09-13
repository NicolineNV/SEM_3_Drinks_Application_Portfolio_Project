package app.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter

@Entity
@Table(name = "user_favorite_cocktail")
public class CocktailMenu {

    @EmbeddedId
    private CocktailMenuId id = new CocktailMenuId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("cocktailId")
    @JoinColumn(name = "cocktail_id")
    private Cocktail cocktail;

    @Column(name = "saved_at", nullable = false, updatable = false)
    private LocalDateTime savedAt;

    public CocktailMenu(User user, Cocktail cocktail) {
        this.user = user;
        this.cocktail = cocktail;
    }

    @PrePersist
    protected void onCreate() {
        this.savedAt = LocalDateTime.now();
    }
}
