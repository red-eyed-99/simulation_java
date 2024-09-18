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

public class MoveCreaturesAction extends Action {
    private final Area area;

    private final PathFinder pathFinder;
    private final Map<Creature, Stack<Coordinates>> pathsToFood = new HashMap<>();
    private final Map<Creature, Integer> pathNotFoundCreatures = new HashMap<>();

    private Map<Coordinates, Creature> updatedCreatures;

    public MoveCreaturesAction(Area area) {
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
                    performCreatureAction(coordinates, creature);
                    break;
                case CreatureStatus.IN_SEARCH_FOOD:
                    setNewPathToFood(coordinates, creature);
                    performCreatureAction(coordinates, creature);
                    break;
                case CreatureStatus.BLOCKED:
                    updatedCreatures.remove(coordinates);
            }
        }

        area.updateCreatures(updatedCreatures);
    }

    private void performCreatureAction(Coordinates coordinates, Creature creature) {
        if (isEndPoint(creature)) {
            if (hasFoodToEat(coordinates, creature, pathsToFood.get(creature).peek())) {
                makeCreatureEat(pathsToFood.get(creature).pop(), coordinates, creature);
            } else if (creature instanceof Predator) {
                if (hasCreatureToAttack(coordinates, pathsToFood.get(creature).peek())) {
                    makeCreatureAttack(pathsToFood.get(creature).pop(), coordinates, creature);
                } else {
                    setNewPathToFood(coordinates, creature);
                    makeCreatureMove(coordinates, creature);
                }
            } else {
                setNewPathToFood(coordinates, creature);
                makeCreatureMove(coordinates, creature);
            }
        } else if (creature instanceof Predator) {
            setNewPathToFood(coordinates, creature);
            makeCreatureMove(coordinates, creature);
        } else {
            makeCreatureMove(coordinates, creature);
        }

    }

    private boolean isEndPoint(Creature creature) {
        return pathsToFood.get(creature).size() == 1;
    }

    private boolean pathNotFound(Creature creature) {
        return pathsToFood.get(creature).isEmpty();
    }

    private void setNewPathToFood(Coordinates coordinates, Creature creature) {
        pathFinder.updateCreatures(updatedCreatures);

        Stack<Coordinates> pathToFood = pathFinder.getPathToFood(coordinates, creature);
        pathsToFood.put(creature, pathToFood);

        if (!pathNotFound(creature)) {
            pathNotFoundCreatures.remove(creature);
            creature.setStatus(CreatureStatus.MOVE_TO_FOOD);
        } else {
            pathNotFoundCreatures.put(creature, pathNotFoundCreatures.getOrDefault(creature, 0) + 1);

            if (creatureBlocked(creature)) {
                creature.setStatus(CreatureStatus.BLOCKED);
                return;
            }

            creature.setStatus(CreatureStatus.IN_SEARCH_FOOD);
        }
    }

    private boolean creatureBlocked(Creature creature) {
        return pathNotFoundCreatures.get(creature) >= 6;
    }

    private void makeCreatureMove(Coordinates coordinates, Creature creature) {
        if (creatureAlreadyDead(coordinates)) {
            pathNotFoundCreatures.remove(creature);
            pathsToFood.remove(creature);
            return;
        }

        if (pathNotFound(creature)) {
            return;
        }

        Coordinates newCoordinates = pathsToFood.get(creature).peek();

        if (nextMoveBlocked(newCoordinates)) {
            setNewPathToFood(coordinates, creature);

            if (pathNotFound(creature)) {
                return;
            }

            newCoordinates = pathsToFood.get(creature).peek();

            if (isEndPoint(creature)) {
                if (hasFoodToEat(coordinates, creature, newCoordinates)) {
                    makeCreatureEat(pathsToFood.get(creature).pop(), coordinates, creature);
                    return;
                } else if (creature instanceof Predator) {
                    if (hasCreatureToAttack(coordinates, newCoordinates)) {
                        makeCreatureAttack(pathsToFood.get(creature).pop(), coordinates, creature);
                        return;
                    }
                }
            }
        }

        makeMove(coordinates, creature);
    }

    private boolean creatureAlreadyDead(Coordinates coordinates) {
        return !updatedCreatures.containsKey(coordinates);
    }

    private boolean nextMoveBlocked(Coordinates coordinates) {
        return updatedCreatures.containsKey(coordinates) || !area.getLandscapeEntities().get(coordinates).canStep();
    }

    private void makeMove(Coordinates coordinates, Creature creature) {
        creature.makeMove();

        Coordinates newCoordinates = pathsToFood.get(creature).pop();

        updatedCreatures.remove(coordinates);
        updatedCreatures.put(newCoordinates, creature);

        creature.changeViewDirection(coordinates.x, newCoordinates.x);
    }

    private void makeCreatureEat(Coordinates foodCoordinates, Coordinates creatureCoordinates, Creature creature) {
        creature.eat();

        if (area.getWaterCoordinates().contains(foodCoordinates)) {
            area.getLandscapeEntities().replace(foodCoordinates, new Water());
        } else {
            area.getLandscapeEntities().replace(foodCoordinates, new Ground());
        }

        creature.changeViewDirection(creatureCoordinates.x, foodCoordinates.x);

        creature.setStatus(CreatureStatus.IN_SEARCH_FOOD);
    }

    private boolean hasFoodToEat(Coordinates coordinates, Creature creature, Coordinates targetCoordinates) {
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
                                && (area.getLandscapeEntities().get(entityCoordinates).getClass() == foodType)
                                && (entityCoordinates.x == targetCoordinates.x && entityCoordinates.y == targetCoordinates.y));
    }

    private void makeCreatureAttack(Coordinates coordinatesToAttack, Coordinates creatureCoordinates, Creature creature) {
        Creature creatureToAttack = updatedCreatures.get(coordinatesToAttack);
        creatureToAttack.takeDamage();

        if (creatureToAttack.getHealthPoint() == 0) {
            updatedCreatures.remove(coordinatesToAttack);
            area.getLandscapeEntities().replace(coordinatesToAttack, new Meat());
        }

        creature.changeViewDirection(creatureCoordinates.x, coordinatesToAttack.x);

        creature.setStatus(CreatureStatus.IN_SEARCH_FOOD);
    }

    private boolean hasCreatureToAttack(Coordinates attackerCoordinates, Coordinates targetCoordinates) {
        return updatedCreatures.keySet().stream()
                .anyMatch(creatureCoordinates ->
                        ((Math.abs(creatureCoordinates.x - attackerCoordinates.x) == 1 && creatureCoordinates.y == attackerCoordinates.y)
                                || (Math.abs(creatureCoordinates.y - attackerCoordinates.y) == 1 && creatureCoordinates.x == attackerCoordinates.x))
                                && (updatedCreatures.get(creatureCoordinates) instanceof Herbivore)
                                && (creatureCoordinates.x == targetCoordinates.x && creatureCoordinates.y == targetCoordinates.y));
    }
}


