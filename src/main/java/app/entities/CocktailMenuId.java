package app.entities;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class CocktailMenuId implements Serializable{

    private Long userId;
    private Long cocktailId;

    public CocktailMenuId(){
    }

    public CocktailMenuId(Long userId, Long cocktailId) {
        this.userId = userId;
        this.cocktailId = cocktailId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CocktailMenuId that)) return false;
        return Objects.equals(userId, that.userId) && Objects.equals(cocktailId, that.cocktailId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, cocktailId);
    }
}
