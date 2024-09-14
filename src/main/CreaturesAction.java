package main;

import main.entities.creatures.Creature;
import main.entities.creatures.herbivores.Herbivore;
import main.entities.creatures.predators.Predator;
import main.entities.landscape.LandscapeEntity;
import main.entities.landscape.food_resources.Grass;
import main.entities.landscape.food_resources.Meat;
import main.entities.landscape.surface.Ground;
import main.entities.landscape.surface.Water;

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
        Map<Coordinates, Creature> newCreaturesPositions = new HashMap<>(creatures);

        if (pathsToFood.isEmpty()) {
            setInitialPathToFood(creatures);
        }

        for (Map.Entry<Coordinates, Creature> entry : creatures.entrySet()) {
            Creature creature = entry.getValue();
            Coordinates coordinates = entry.getKey();

            switch (creature.getStatus()) {
                case CreatureStatus.MOVE_TO_FOOD:
                    if ((pathsToFood.get(creature).size() == 1)) {
                        if (isFoodAvailableToEat(coordinates, creature)) {
                            makeCreatureEat(pathsToFood.get(creature).pop(), creature);
                            break;
                        } else if (creature instanceof Predator) {
                            // проверить возможность нанести урон
                            // нанести урон / искать новый путь
                            break;
                        } else {
                            setNewPathToFood(coordinates, creature);
                            makeCreatureMove(coordinates, creature, newCreaturesPositions);
                            break;
                        }
                    } else if (creature instanceof Predator) {
                        setNewPathToFood(coordinates, creature);
                        makeCreatureMove(coordinates, creature, newCreaturesPositions);
                        break;
                    } else {
                        makeCreatureMove(coordinates, creature, newCreaturesPositions);
                        break;
                    }
                case CreatureStatus.IN_SEARCH_FOOD:
                    setNewPathToFood(coordinates, creature);
                    makeCreatureMove(coordinates, creature, newCreaturesPositions);
                    break;
            }
        }

        area.updateCreatures(newCreaturesPositions);
    }

    private void setInitialPathToFood(Map<Coordinates, Creature> creatures) {
        for (Map.Entry<Coordinates, Creature> entry : creatures.entrySet()) {
            pathsToFood.put(entry.getValue(), pathFinder.getPathToFood(entry.getKey(), entry.getValue()));
            entry.getValue().setStatus(CreatureStatus.MOVE_TO_FOOD);
        }
    }

    private void setNewPathToFood(Coordinates coordinates, Creature creature) {
        pathsToFood.put(creature, pathFinder.getPathToFood(coordinates, creature));
        creature.setStatus(CreatureStatus.MOVE_TO_FOOD);
    }

    private void makeCreatureMove(Coordinates coordinates, Creature creature, Map<Coordinates, Creature> newCreaturesPositions) {
        Coordinates newCoordinates = null;
        try {
            newCoordinates = pathsToFood.get(creature).peek();
        } catch (Exception e) {
            System.out.println("Creature coordinates: " + coordinates.x + ";" + coordinates.y);
            System.out.println("Paths to food: " + pathsToFood.entrySet());
        }
        if (newCreaturesPositions.containsKey(newCoordinates)) {
            return; // возможно стоит найти новый путь а не пропускать ход
        }

        creature.makeMove();

        newCoordinates = pathsToFood.get(creature).pop();

        newCreaturesPositions.remove(coordinates);
        newCreaturesPositions.put(newCoordinates, creature);
    }

    private void makeCreatureEat(Coordinates coordinates, Creature creature) {
        creature.eat();

        LandscapeEntity surfaceType;

        if (area.getLandscapeEntities().get(coordinates) instanceof Ground) {
            surfaceType = new Ground();
        } else {
            surfaceType = new Water();
        }

        area.getLandscapeEntities().replace(coordinates, surfaceType);

        creature.setStatus(CreatureStatus.IN_SEARCH_FOOD);
    }

    private boolean isFoodAvailableToEat(Coordinates coordinates, Creature creature) {
        Class<? extends LandscapeEntity> foodType;

        if (creature instanceof Herbivore) {
            foodType = Grass.class;
        } else {
            foodType = Meat.class;
        }

        return area.getLandscapeEntities().keySet().stream()
                .anyMatch(entityCoordinates ->
                        ((Math.abs(entityCoordinates.x - coordinates.x) == 1 && entityCoordinates.y == coordinates.y)
                                || (Math.abs(entityCoordinates.y - coordinates.y) == 1 && entityCoordinates.x == coordinates.x))
                                && (area.getLandscapeEntities().get(entityCoordinates).getClass() == foodType));
    }
}


