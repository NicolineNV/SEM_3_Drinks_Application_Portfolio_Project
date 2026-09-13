package app.config;

import app.entities.*;
import org.hibernate.cfg.Configuration;

final class EntityRegistry {

    private EntityRegistry() {}

    static void registerEntities(Configuration configuration) {

        // Independent entities
        configuration.addAnnotatedClass(FlavorTag.class);
        configuration.addAnnotatedClass(Garnish.class);
        configuration.addAnnotatedClass(CocktailFamily.class);
        configuration.addAnnotatedClass(User.class);

        // Ingredient
        configuration.addAnnotatedClass(Spirit.class);
        configuration.addAnnotatedClass(Liqueur.class);
        configuration.addAnnotatedClass(Mixer.class);
        configuration.addAnnotatedClass(Syrup.class);

        // Join-entities (ingredient <-> flavor_tag)
        configuration.addAnnotatedClass(SpiritFlavorTag.class);
        configuration.addAnnotatedClass(LiqueurFlavorTag.class);
        configuration.addAnnotatedClass(MixerFlavorTag.class);
        configuration.addAnnotatedClass(SyrupFlavorTag.class);

        // Cocktail and user-relations
        configuration.addAnnotatedClass(Cocktail.class);
        configuration.addAnnotatedClass(CocktailMenu.class);


    }
}
