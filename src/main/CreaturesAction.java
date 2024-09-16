package main;

import main.entities.creatures.Creature;
import main.entities.creatures.herbivores.Herbivore;
import main.entities.creatures.predators.Predator;
import main.entities.landscape.LandscapeEntity;
import main.entities.landscape.food_resources.Grass;
import main.entities.landscape.food_resources.Meat;
import main.entities.landscape.surface.Ground;

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

        // debug
        System.out.println("__________________TURN________________");
        //debug

        for (Map.Entry<Coordinates, Creature> entry : creatures.entrySet()) {
            Creature creature = entry.getValue();
            Coordinates coordinates = entry.getKey();

            // debug
            System.out.println("---------------------------------");
            System.out.println(creature.getClass().getSimpleName() + " x=" + coordinates.x + " y=" + coordinates.y);
            System.out.println(creature.getStatus());
            // debug

            switch (creature.getStatus()) {
                case CreatureStatus.MOVE_TO_FOOD:
                    performCreatureAction(coordinates, creature);
                    break;
                case CreatureStatus.IN_SEARCH_FOOD:
                    setNewPathToFood(coordinates, creature);
                    performCreatureAction(coordinates, creature);
                    break;
            }
        }

        area.updateCreatures(updatedCreatures);
    }

    private void performCreatureAction(Coordinates coordinates, Creature creature) {
        if (isEndPoint(creature)) {
            if (hasFoodToEat(coordinates, creature, pathsToFood.get(creature).peek())) {
                makeCreatureEat(pathsToFood.get(creature).pop(), creature);
            } else if (creature instanceof Predator) {
                if (hasCreatureToAttack(coordinates, pathsToFood.get(creature).peek())) {
                    makeCreatureAttack(pathsToFood.get(creature).pop(), creature);
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
            creature.setStatus(CreatureStatus.MOVE_TO_FOOD);
        } else {
            creature.setStatus(CreatureStatus.IN_SEARCH_FOOD);
        }

        // debug
        if (!pathNotFound(creature)) {
            System.out.println("найден путь");
        } else {
            System.out.println("путь не найден");
        }
        // debug
    }

    private void makeCreatureMove(Coordinates coordinates, Creature creature) {
        if (creatureAlreadyDead(coordinates)) {
            // debug
            System.out.println("существо метров, движение невозможно");
            //debug

            pathsToFood.remove(creature);
            return;
        }

        if (pathNotFound(creature)) {
            // debug
            System.out.println("движение невозможжно, так как путь не найден");
            //debug

            return;
        }

        Coordinates newCoordinates = pathsToFood.get(creature).peek();

        if (nextMoveBlocked(newCoordinates)) {
            // debug
            System.out.println("следующий шаг заблокирован, ищем другой путь");
            //debug

            setNewPathToFood(coordinates, creature);

            if (pathNotFound(creature)) {
                // debug
                System.out.println("движение невозможжно, так как путь не найден");
                //debug

                return;
            }

            newCoordinates = pathsToFood.get(creature).peek();

            if (nextMoveBlocked(newCoordinates)) {
                // debug
                System.out.println("движение опять заблокировано, пропускаю ход");
                //debug

                return;
            }

            if (isEndPoint(creature)) {
                if (hasFoodToEat(coordinates, creature, newCoordinates)) {
                    makeCreatureEat(pathsToFood.get(creature).pop(), creature);
                    return;
                } else if (creature instanceof Predator) {
                    if (hasCreatureToAttack(coordinates, newCoordinates)) {
                        makeCreatureAttack(pathsToFood.get(creature).pop(), creature);
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

        // debug
        System.out.println(" передвигваюсь с x=" + coordinates.x + " y=" + coordinates.y + " на x=" + newCoordinates.x + " y=" + newCoordinates.y);
        //debug
    }

    private void makeCreatureEat(Coordinates coordinates, Creature creature) {
        creature.eat();

        area.getLandscapeEntities().replace(coordinates, new Ground());

        creature.setStatus(CreatureStatus.IN_SEARCH_FOOD);

        // debug
        System.out.println("съел существо с x=" + coordinates.x + " y=" + coordinates.y);
        //debug
    }

    private boolean hasFoodToEat(Coordinates coordinates, Creature creature, Coordinates targetCoordinates) {
        Class<? extends LandscapeEntity> foodType;

        if (creature instanceof Herbivore) {
            foodType = Grass.class;
        } else {
            foodType = Meat.class;
        }

        // debug
        System.out.println("ищу доступную еду на x=" + targetCoordinates.x + " y=" + targetCoordinates.y + " " + area.getLandscapeEntities().keySet().stream()
                .anyMatch(entityCoordinates ->
                        ((Math.abs(entityCoordinates.x - coordinates.x) == 1 && entityCoordinates.y == coordinates.y)
                                || (Math.abs(entityCoordinates.y - coordinates.y) == 1 && entityCoordinates.x == coordinates.x))
                                && (area.getLandscapeEntities().get(entityCoordinates).getClass() == foodType)
                                && (entityCoordinates.x == targetCoordinates.x && entityCoordinates.y == targetCoordinates.y)));
        // debug

        return area.getLandscapeEntities().keySet().stream()
                .anyMatch(entityCoordinates ->
                        ((Math.abs(entityCoordinates.x - coordinates.x) == 1 && entityCoordinates.y == coordinates.y)
                                || (Math.abs(entityCoordinates.y - coordinates.y) == 1 && entityCoordinates.x == coordinates.x))
                                && (area.getLandscapeEntities().get(entityCoordinates).getClass() == foodType)
                                && (entityCoordinates.x == targetCoordinates.x && entityCoordinates.y == targetCoordinates.y));
    }

    private void makeCreatureAttack(Coordinates coordinatesToAttack, Creature creature) {
        Creature creatureToAttack = updatedCreatures.get(coordinatesToAttack);
        creatureToAttack.takeDamage();

        if (creatureToAttack.getHealthPoint() == 0) {
            updatedCreatures.remove(coordinatesToAttack);
            area.getLandscapeEntities().replace(coordinatesToAttack, new Meat());

            // debug
            System.out.println("убил существо на x=" + coordinatesToAttack.x + " y=" + coordinatesToAttack.y);
            //debug
        }

        creature.setStatus(CreatureStatus.IN_SEARCH_FOOD);
    }

    private boolean hasCreatureToAttack(Coordinates attackerCoordinates, Coordinates targetCoordinates) {

        // debug
        System.out.println("ищу существо для атаки на x=" + targetCoordinates.x + " y=" + targetCoordinates.y + " " + updatedCreatures.keySet().stream()
                .anyMatch(creatureCoordinates ->
                        ((Math.abs(creatureCoordinates.x - attackerCoordinates.x) == 1 && creatureCoordinates.y == attackerCoordinates.y)
                                || (Math.abs(creatureCoordinates.y - attackerCoordinates.y) == 1 && creatureCoordinates.x == attackerCoordinates.x))
                                && (updatedCreatures.get(creatureCoordinates) instanceof Herbivore)
                                && (creatureCoordinates.x == targetCoordinates.x && creatureCoordinates.y == targetCoordinates.y)));
        // debug

        return updatedCreatures.keySet().stream()
                .anyMatch(creatureCoordinates ->
                        ((Math.abs(creatureCoordinates.x - attackerCoordinates.x) == 1 && creatureCoordinates.y == attackerCoordinates.y)
                                || (Math.abs(creatureCoordinates.y - attackerCoordinates.y) == 1 && creatureCoordinates.x == attackerCoordinates.x))
                                && (updatedCreatures.get(creatureCoordinates) instanceof Herbivore)
                                && (creatureCoordinates.x == targetCoordinates.x && creatureCoordinates.y == targetCoordinates.y));
    }
}


