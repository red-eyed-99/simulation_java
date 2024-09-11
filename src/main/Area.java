package main;

import main.entities.Entity;
import main.entities.creatures.Creature;
import main.entities.landscape.LandscapeEntity;

import java.util.HashMap;
import java.util.Map;

public class Area {
    public final int size;

    private final Map<Coordinates, LandscapeEntity> landscapeEntities;
    private final Map<Coordinates, Creature> creatures = new HashMap<>();

    public Area(int size) {
        this.size = size;
        landscapeEntities = new HashMap<>(size * 2);
    }

    public Entity getLandscapeOrCreature(Coordinates coordinates) {
        Creature creature = creatures.get(coordinates);
        if (creature != null) {
            return creature;
        }

        return landscapeEntities.get(coordinates);
    }

    public Map<Coordinates, Creature> getCreatures() {
        return creatures;
    }

    public Map<Coordinates, LandscapeEntity> getLandscapeEntities() {
        return landscapeEntities;
    }
}
