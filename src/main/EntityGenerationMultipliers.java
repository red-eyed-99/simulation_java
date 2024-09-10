package main;

import java.util.HashMap;
import java.util.Map;

public class EntityGenerationMultipliers {
    private static final Map<EntityType, Double> entityMultipliers = new HashMap<>();

    static {
        // landscape
        entityMultipliers.put(EntityType.TREE, 0.1);
        entityMultipliers.put(EntityType.ROCK, 0.05);
        entityMultipliers.put(EntityType.GRASS, 0.2);

        // creatures

        // herbivores
        entityMultipliers.put(EntityType.OSTRICH, 0.1);
        entityMultipliers.put(EntityType.ELEPHANT, 0.1);
        entityMultipliers.put(EntityType.GIRAFEE, 0.1);
        entityMultipliers.put(EntityType.RHINO, 0.1);

        //predators
        entityMultipliers.put(EntityType.LION, 0.1);
        entityMultipliers.put(EntityType.CROCODILE, 0.1);
        entityMultipliers.put(EntityType.PANTHER, 0.1);
        entityMultipliers.put(EntityType.TIGER, 0.1);
    }

    public static Double getMultiplier(EntityType type) {
        return entityMultipliers.get(type);
    }
}
