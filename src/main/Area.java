package main;

import main.entities.Entity;
import main.entities.creatures.Creature;

import java.util.HashMap;

public class Area {
    public final int size;

    public final HashMap<Coordinates, Entity> landscape;
    public final HashMap<Coordinates, Creature> creatures = new HashMap<>();

    public Area(int size) {
        this.size = size;
        landscape = new HashMap<>(size * 2);
    }

    public Entity getEntity(Coordinates coordinates) {
        Creature creature = creatures.get(coordinates);
        if (creature != null) {
            return creature;
        }

        return landscape.get(coordinates);
    }
}
