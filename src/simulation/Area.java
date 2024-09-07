package simulation;

import simulation.entities.Entity;
import simulation.entities.creatures.Creature;

import java.awt.*;
import java.util.HashMap;

public class Area {
    private HashMap<Point, Entity> cells = new HashMap<>();
    private HashMap<Point, Creature> creatures = new HashMap<>();
}
