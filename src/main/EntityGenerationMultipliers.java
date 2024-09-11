package main;

import main.entities.Entity;
import main.entities.creatures.herbivores.Elephant;
import main.entities.creatures.herbivores.Girafee;
import main.entities.creatures.herbivores.Ostrich;
import main.entities.creatures.herbivores.Rhino;
import main.entities.creatures.predators.Crocodile;
import main.entities.creatures.predators.Lion;
import main.entities.creatures.predators.Panther;
import main.entities.creatures.predators.Tiger;
import main.entities.landscape.food_resources.Grass;
import main.entities.landscape.static_objects.Rock;
import main.entities.landscape.static_objects.Tree;

import java.util.HashMap;
import java.util.Map;

public class EntityGenerationMultipliers {
    private static final Map<Class<? extends Entity>, Double> ENTITY_MULTIPLIERS = new HashMap<>();

    static {
        // landscape
        ENTITY_MULTIPLIERS.put(Tree.class, 0.1);
        ENTITY_MULTIPLIERS.put(Rock.class, 0.05);
        ENTITY_MULTIPLIERS.put(Grass.class, 0.02);

        // creatures

        // herbivores
        ENTITY_MULTIPLIERS.put(Ostrich.class, 0.02);
        ENTITY_MULTIPLIERS.put(Elephant.class, 0.1);
        ENTITY_MULTIPLIERS.put(Girafee.class, 0.1);
        ENTITY_MULTIPLIERS.put(Rhino.class, 0.1);

        //predators
        ENTITY_MULTIPLIERS.put(Lion.class, 0.02);
        ENTITY_MULTIPLIERS.put(Crocodile.class, 0.1);
        ENTITY_MULTIPLIERS.put(Panther.class, 0.1);
        ENTITY_MULTIPLIERS.put(Tiger.class, 0.1);
    }

    public static Double getMultiplier(Class<? extends Entity> entityType) {
        return ENTITY_MULTIPLIERS.get(entityType);
    }
}
