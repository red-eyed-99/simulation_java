package simulation;

import simulation.entities.Entity;
import simulation.entities.creatures.Creature;

import java.util.HashMap;

public class Area {
    public final int size;
    public final HashMap<Coordinates, Entity> entities;
    public final HashMap<Coordinates, Creature> creatures = new HashMap<>();

    public Area(int size) {
        this.size = size;
        entities = new HashMap<>(size * 2);
    }
}
