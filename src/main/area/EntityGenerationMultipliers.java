package main.area;

import main.entities.Entity;
import main.entities.creatures.herbivores.*;
import main.entities.creatures.predators.Lion;
import main.entities.creatures.predators.Panther;
import main.entities.creatures.predators.Tiger;
import main.entities.landscape.food_resources.Grass;
import main.entities.landscape.static_objects.Rock;
import main.entities.landscape.static_objects.Tree;

import java.util.HashMap;
import java.util.Map;

public class EntityGenerationMultipliers {
    private static final Map<Class<? extends Entity>, Double> ENTITY_SPAWN_MULTIPLIERS = new HashMap<>();

    static {
        // landscape
        ENTITY_SPAWN_MULTIPLIERS.put(Tree.class, 0.05);
        ENTITY_SPAWN_MULTIPLIERS.put(Rock.class, 0.02);
        ENTITY_SPAWN_MULTIPLIERS.put(Grass.class, 0.03);

        // creatures

        // herbivores
        ENTITY_SPAWN_MULTIPLIERS.put(Ostrich.class, 0.015);
        ENTITY_SPAWN_MULTIPLIERS.put(Zebra.class, 0.015);
        ENTITY_SPAWN_MULTIPLIERS.put(Buffalo.class, 0.015);
        ENTITY_SPAWN_MULTIPLIERS.put(Antelope.class, 0.015);
        ENTITY_SPAWN_MULTIPLIERS.put(Elephant.class, 0.015);

        //predators
        ENTITY_SPAWN_MULTIPLIERS.put(Lion.class, 0.01);
        ENTITY_SPAWN_MULTIPLIERS.put(Panther.class, 0.009);
        ENTITY_SPAWN_MULTIPLIERS.put(Tiger.class, 0.01);
    }

    public static Double getMultiplier(Class<? extends Entity> entityType) {
        return ENTITY_SPAWN_MULTIPLIERS.get(entityType);
    }
}
