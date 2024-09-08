package simulation;

import simulation.entities.Entity;
import simulation.entities.food_resources.Grass;
import simulation.entities.landscape.static_objects.Rock;
import simulation.entities.landscape.static_objects.Tree;
import simulation.entities.landscape.surface.Ground;
import simulation.entities.landscape.surface.Water;

public class EntityFactory {
    public Entity createEntity(EntityType entityType) {
        if (entityType == EntityType.GROUND) {
            return new Ground();
        } else if (entityType == EntityType.WATER) {
            return new Water();
        } else if (entityType == EntityType.TREE) {
            return new Tree();
        } else if (entityType == EntityType.ROCK) {
            return new Rock();
        } else {
            return new Grass();
        }
    }
}
