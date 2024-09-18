package main;

import main.entities.Entity;
import main.entities.creatures.Creature;
import main.entities.creatures.herbivores.Herbivore;
import main.entities.creatures.predators.Predator;
import main.entities.landscape.LandscapeEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Area {
    public final int size;

    private final Map<Coordinates, LandscapeEntity> landscapeEntities;
    private Set<Coordinates> waterCoordinates;
    private Map<Coordinates, Creature> creatures = new HashMap<>();

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

    public void updateCreatures(Map<Coordinates, Creature> newCreaturesPositions) {
        creatures = newCreaturesPositions;
    }

    public Map<Coordinates, LandscapeEntity> getLandscapeEntities() {
        return landscapeEntities;
    }

    public Set<Coordinates> getWaterCoordinates() {
        return waterCoordinates;
    }

    public void setWaterCoordinates(Set<Coordinates> waterCoordinates) {
        this.waterCoordinates = waterCoordinates;
    }

    public int getPredatorsCount() {
        return (int) creatures.keySet().stream()
                .filter(coordinates -> creatures.get(coordinates) instanceof Predator)
                .count();
    }

    public int getHerbivoresCount() {
        return (int) creatures.keySet().stream()
                .filter(coordinates -> creatures.get(coordinates) instanceof Herbivore)
                .count();
    }
}
