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
import main.entities.landscape.surface.Ground;
import main.entities.landscape.surface.Water;

import java.util.NoSuchElementException;

public class EntityFactory {
    public Entity createEntity(EntityType entityType) {
        // landscape
        if (entityType == EntityType.GROUND) {
            return new Ground();
        } else if (entityType == EntityType.WATER) {
            return new Water();
        } else if (entityType == EntityType.TREE) {
            return new Tree();
        } else if (entityType == EntityType.ROCK) {
            return new Rock();
        } else if (entityType == EntityType.GRASS){
            return new Grass();
        }

        // creatures

        // herbivores
        else if (entityType == EntityType.ELEPHANT){
            return new Elephant();
        }
        else if (entityType == EntityType.OSTRICH){
            return new Ostrich();
        }
        else if (entityType == EntityType.GIRAFEE){
            return new Girafee();
        }
        else if (entityType == EntityType.RHINO){
            return new Rhino();
        }
        // predators
        else if (entityType == EntityType.LION){
            return new Lion();
        }
        else if (entityType == EntityType.PANTHER){
            return new Panther();
        }
        else if (entityType == EntityType.TIGER){
            return new Tiger();
        }
        else if (entityType == EntityType.CROCODILE){
            return new Crocodile();
        }

        throw new NoSuchElementException();
    }
}
