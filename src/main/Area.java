package main;

import main.entities.Entity;
import main.entities.creatures.Creature;
import main.entities.landscape.LandscapeEntity;

import java.util.HashMap;
import java.util.Set;

public class Area {
    public final int size;

    private final HashMap<Coordinates, LandscapeEntity> landscapeEntities;
    private final HashMap<Coordinates, Creature> creatures = new HashMap<>();

    public Area(int size) {
        this.size = size;
        landscapeEntities = new HashMap<>(size * 2);
    }

    public int getLandscapeEntitiesCount() {
        return landscapeEntities.size();
    }

    public void addEntity(Coordinates coordinates, Entity entity) {
        if (entity instanceof Creature creature) {
            creatures.put(coordinates, creature);
        } else {
            landscapeEntities.put(coordinates, (LandscapeEntity) entity);
        }
    }

    public Entity getLandscapeOrCreature(Coordinates coordinates) {
        Creature creature = creatures.get(coordinates);
        if (creature != null) {
            return creature;
        }

        return landscapeEntities.get(coordinates);
    }

    public LandscapeEntity getLandscapeEntity(Coordinates coordinates) {
        return landscapeEntities.get(coordinates);
    }

    public Set<Coordinates> getLandscapeEntitiesCoordinates() {
        return landscapeEntities.keySet();
    }

    public void replaceLandscapeEntity(Coordinates coordinates, Entity entity) {
        landscapeEntities.replace(coordinates, (LandscapeEntity) entity);
    }

    public void removeCreature(Coordinates coordinates) {
        creatures.remove(coordinates);
    }
}
