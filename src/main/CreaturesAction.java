package main;

import main.entities.creatures.Creature;
import main.entities.creatures.herbivores.Herbivore;
import main.entities.creatures.predators.Lion;
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
    private Map<Coordinates, Creature> updatedCreatures;

    public CreaturesAction(Area area) {
        this.area = area;
        pathFinder = new PathFinder(area);
    }

    @Override
    public void execute() {
        Map<Coordinates, Creature> creatures = area.getCreatures();
        updatedCreatures = new HashMap<>(Map.copyOf(creatures));

        for (Map.Entry<Coordinates, Creature> entry : creatures.entrySet()) {
            Creature creature = entry.getValue();
            Coordinates coordinates = entry.getKey();

            switch (creature.getStatus()) {
                case CreatureStatus.MOVE_TO_FOOD:
                    if ((pathsToFood.get(creature).size() == 1)) {
                        if (isFoodAvailableToEat(coordinates, creature)) {
                            makeCreatureEat(pathsToFood.get(creature).pop(), creature);
                        } else if (creature instanceof Predator) {
                            if (isCreatureAvailableToAttack(coordinates)) {
                                makeCreatureAttack(pathsToFood.get(creature).pop(), coordinates, creature, updatedCreatures);
                            } else {
                                setNewPathToFood(coordinates, creature);
                                makeCreatureMove(coordinates, creature, updatedCreatures);
                            }
                        } else {
                            setNewPathToFood(coordinates, creature);
                            makeCreatureMove(coordinates, creature, updatedCreatures);
                        }
                    } else if (creature instanceof Predator) {
                        setNewPathToFood(coordinates, creature);
                        makeCreatureMove(coordinates, creature, updatedCreatures);
                    } else {
                        makeCreatureMove(coordinates, creature, updatedCreatures);
                    }
                    break;
                case CreatureStatus.IN_SEARCH_FOOD:
                    setNewPathToFood(coordinates, creature);
                    makeCreatureMove(coordinates, creature, updatedCreatures);
                    break;
            }
        }

        area.updateCreatures(updatedCreatures);
    }

    private void setNewPathToFood(Coordinates coordinates, Creature creature) {
        Stack<Coordinates> pathToFood = pathFinder.getPathToFood(coordinates, creature);
        pathsToFood.put(creature, pathToFood);

        creature.setStatus(CreatureStatus.MOVE_TO_FOOD);
    }

    private void makeCreatureMove(Coordinates coordinates, Creature creature, Map<Coordinates, Creature> newCreaturesPositions) {
        if (pathsToFood.get(creature).isEmpty()) {
            return;
        }

        Coordinates newCoordinates = pathsToFood.get(creature).peek();

        if (newCreaturesPositions.containsKey(newCoordinates)
                || !area.getLandscapeEntities().get(newCoordinates).canStep()) {
            setNewPathToFood(coordinates, creature);

            if (pathsToFood.get(creature).isEmpty()) {
                return;
            }

            newCoordinates = pathsToFood.get(creature).peek();

            if (newCreaturesPositions.containsKey(newCoordinates)
                    || !area.getLandscapeEntities().get(newCoordinates).canStep()) {
                return;
            }
        }

        makeMove(coordinates, creature, newCreaturesPositions);
    }

    private void makeMove(Coordinates coordinates, Creature creature, Map<Coordinates, Creature> newCreaturesPositions) {
        if (!newCreaturesPositions.containsKey(coordinates)) {
            pathsToFood.remove(creature);
            return;
        }
        creature.makeMove();

        Coordinates newCoordinates = pathsToFood.get(creature).pop();

        newCreaturesPositions.remove(coordinates);
        newCreaturesPositions.put(newCoordinates, creature);
    }

    private void makeCreatureEat(Coordinates coordinates, Creature creature) {
        creature.eat();

        area.getLandscapeEntities().replace(coordinates, new Ground());

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

    private void makeCreatureAttack(Coordinates coordinates, Coordinates coordinatesPredator, Creature creature, Map<Coordinates, Creature> newCreaturesPositions) {
        Creature creatureToAttack = newCreaturesPositions.get(coordinates);


        if (creatureToAttack != null) {
            creatureToAttack.takeDamage();
        } else {
            setNewPathToFood(coordinatesPredator, creature);
            if (pathsToFood.get(creature).size() == 1) {
                creatureToAttack = area.getCreatures().get(pathsToFood.get(creature).pop());
                if (creatureToAttack != null) {
                    creatureToAttack.takeDamage();
                }
            }
            makeCreatureMove(coordinatesPredator, creature, newCreaturesPositions);
            return;
        }

        if (creatureToAttack.getHealthPoint() == 0) {
            newCreaturesPositions.remove(coordinates);
            area.getLandscapeEntities().replace(coordinates, new Meat());
        }

        creature.setStatus(CreatureStatus.IN_SEARCH_FOOD);
    }

    private boolean isCreatureAvailableToAttack(Coordinates coordinates) {
        return updatedCreatures.keySet().stream()
                .anyMatch(entityCoordinates ->
                        ((Math.abs(entityCoordinates.x - coordinates.x) == 1 && entityCoordinates.y == coordinates.y)
                                || (Math.abs(entityCoordinates.y - coordinates.y) == 1 && entityCoordinates.x == coordinates.x))
                                && (area.getCreatures().get(entityCoordinates) instanceof Herbivore));
    }
}


