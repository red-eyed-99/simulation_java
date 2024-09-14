package main;

import main.entities.creatures.Creature;
import main.entities.creatures.herbivores.Herbivore;
import main.entities.creatures.predators.Predator;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class CreaturesAction extends Action {
    private final Area area;
    private final PathFinder pathFinder;
    private final Map<Creature, Stack<Coordinates>> pathsToFood = new HashMap<>();

    public CreaturesAction(Area area) {
        this.area = area;
        pathFinder = new PathFinder(area);
    }

    @Override
    public void execute() {
        Map<Coordinates, Creature> creatures = area.getCreatures();

        if (pathsToFood.isEmpty()) {
            setInitialPathToFood(creatures);
        } else {
            setNewPathToFood(creatures);
        }
        makeMoveWithCreatures(creatures);
    }

    private void setInitialPathToFood(Map<Coordinates, Creature> creatures) {
        for (Map.Entry<Coordinates, Creature> entry : creatures.entrySet()) {
            pathsToFood.put(entry.getValue(), pathFinder.getPathToFood(entry.getKey(), entry.getValue()));
        }
    }

    private void setNewPathToFood(Map<Coordinates, Creature> creatures) {
        for (Map.Entry<Coordinates, Creature> entry : creatures.entrySet()) {
            if ((entry.getValue() instanceof Herbivore && pathsToFood.get(entry.getValue()).isEmpty())
                    || entry.getValue() instanceof Predator) {
                pathsToFood.put(entry.getValue(), pathFinder.getPathToFood(entry.getKey(), entry.getValue()));
            }
        }
    }

    private void makeMoveWithCreatures(Map<Coordinates, Creature> creatures) {
        Map<Coordinates, Creature> newCreaturesPositions = new HashMap<>(creatures);

        for (Map.Entry<Coordinates, Creature> entry : creatures.entrySet()) {
            if (!pathsToFood.get(entry.getValue()).isEmpty()) {
                Coordinates newCoordinates = pathsToFood.get(entry.getValue()).peek();
                if (newCreaturesPositions.containsKey(newCoordinates)) {
                    continue;
                }

                entry.getValue().makeMove();
                newCoordinates = pathsToFood.get(entry.getValue()).pop();
                Creature creature = entry.getValue();

                newCreaturesPositions.remove(entry.getKey());
                newCreaturesPositions.put(newCoordinates, creature);
            }
        }

        area.updateCreatures(newCreaturesPositions);
    }
}

