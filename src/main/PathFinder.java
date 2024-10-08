package main;

import main.area.Area;
import main.entities.creatures.Creature;
import main.entities.creatures.herbivores.Herbivore;
import main.entities.landscape.food_resources.Grass;
import main.entities.landscape.food_resources.Meat;
import main.entities.landscape.surface.Water;

import java.util.*;
import java.util.stream.Collectors;

public class PathFinder {
    private final Area area;
    private Map<Coordinates, Creature> creatures;

    public PathFinder(Area area) {
        this.area = area;
    }

    static class PathNode {
        Coordinates coordinates;
        int pathLengthFromStart;
        PathNode previousNode;
        int approximatePathLength;

        PathNode(Coordinates coordinates) {
            this.coordinates = coordinates;
        }

        int getExpectedFullPathLength() {
            return pathLengthFromStart + approximatePathLength;
        }
    }

    public void updateCreatures(Map<Coordinates, Creature> updatedCreatures) {
        this.creatures = updatedCreatures;
    }

    public Stack<Coordinates> getPathToFood(Coordinates creatureCoordinates, Creature creature) {
        Stack<Coordinates> pathToFood = new Stack<>();

        PathNode targetNode;

        try {
            targetNode = new PathNode(getNearFoodCoordinates(creatureCoordinates, creature));
        } catch (NoSuchElementException e) {
            return pathToFood;
        }

        Set<PathNode> checkedNodes = new HashSet<>();
        Set<PathNode> nodesForCheck = new HashSet<>();

        PathNode startNode = new PathNode(creatureCoordinates);
        startNode.approximatePathLength = getApproximatePathLength(creatureCoordinates, targetNode.coordinates, creature);

        nodesForCheck.add(startNode);

        while (!nodesForCheck.isEmpty()) {
            PathNode currentNode = nodesForCheck.stream()
                    .min(Comparator.comparing(PathNode::getExpectedFullPathLength))
                    .get();

            if (foodIsNearby(currentNode.coordinates, targetNode.coordinates)) {
                targetNode.previousNode = currentNode;
                pathToFood = pavePath(targetNode);
                return pathToFood;
            }

            nodesForCheck.remove(currentNode);
            checkedNodes.add(currentNode);

            Set<PathNode> availableNodesToMove = getAvailableNodesToMove(currentNode, creature);

            for (PathNode pathNode : availableNodesToMove) {
                if (checkedNodes.stream()
                        .anyMatch(node ->
                                node.coordinates.x == pathNode.coordinates.x
                                        && node.coordinates.y == pathNode.coordinates.y)) {
                    continue;
                }

                PathNode nodeToCheck = nodesForCheck.stream()
                        .filter(node -> node.coordinates.x == pathNode.coordinates.x
                                && node.coordinates.y == pathNode.coordinates.y)
                        .findFirst()
                        .orElse(null);

                if (nodeToCheck == null) {
                    pathNode.previousNode = currentNode;
                    pathNode.pathLengthFromStart = currentNode.pathLengthFromStart + 1;
                    pathNode.approximatePathLength = getApproximatePathLength(currentNode.coordinates, targetNode.coordinates, creature);

                    nodesForCheck.add(pathNode);
                } else if (currentNode.pathLengthFromStart + 1 < pathNode.pathLengthFromStart) {
                    pathNode.previousNode = currentNode;
                    pathNode.pathLengthFromStart = currentNode.pathLengthFromStart + 1;
                }
            }
        }

        return pathToFood;
    }

    private Stack<Coordinates> pavePath(PathNode targetNode) {
        Stack<Coordinates> coordinatesToTarget = new Stack<>();

        PathNode currentNode = targetNode;

        while (currentNode != null) {
            coordinatesToTarget.push(currentNode.coordinates);
            currentNode = currentNode.previousNode;
        }

        coordinatesToTarget.removeLast();

        return coordinatesToTarget;
    }

    private Coordinates getNearFoodCoordinates(Coordinates coordinates, Creature creature) {
        List<Coordinates> foodCoordinatesList;

        Set<Coordinates> landscapeEntitiesCoordinates = area.getLandscapeEntities().keySet();

        if (creature instanceof Herbivore) {
            foodCoordinatesList = landscapeEntitiesCoordinates.stream()
                    .filter(entityCoordinates -> area.getLandscapeEntities().get(entityCoordinates) instanceof Grass)
                    .collect(Collectors.toCollection(ArrayList::new));
        } else {
            foodCoordinatesList = creatures.keySet().stream()
                    .filter(creatureCoordinates -> creatures.get(creatureCoordinates) instanceof Herbivore)
                    .collect(Collectors.toCollection(ArrayList::new));

            landscapeEntitiesCoordinates.stream()
                    .filter(entityCoordinates -> area.getLandscapeEntities().get(entityCoordinates) instanceof Meat)
                    .forEach(foodCoordinatesList::add);
        }

        Coordinates nearFoodCoordinates = foodCoordinatesList.getFirst();

        for (Coordinates foodCoordinates : foodCoordinatesList) {
            if ((Math.abs(foodCoordinates.x - coordinates.x) + Math.abs(foodCoordinates.y - coordinates.y))
                    < (Math.abs(nearFoodCoordinates.x - coordinates.x) + Math.abs(nearFoodCoordinates.y - coordinates.y))) {
                nearFoodCoordinates = foodCoordinates;
            }
        }

        return nearFoodCoordinates;
    }

    private int getApproximatePathLength(Coordinates currentNode, Coordinates targetNode, Creature creature) {
        int moveSpeed = creature.getMoveSpeed();

        int dX = Math.abs(currentNode.x - targetNode.x);
        int dY = Math.abs(currentNode.y - targetNode.y);

        int manhattanDistance = dX + dY;

        int pathLength = (int) Math.ceil((double) (manhattanDistance) / moveSpeed);

        if (nodesAreCollinear(dX, dY)) {
            if (moveSpeed == 1
                    || (manhattanDistance % 2 != 0 && moveSpeed == 2)
                    || manhattanDistance == 1
                    || (manhattanDistance % 2 == 0 && moveSpeed == manhattanDistance - 1)) {
                return pathLength - 1;
            }
        }

        if (moveSpeed == 1) {
            return pathLength - 1;
        }

        return pathLength;
    }

    private boolean nodesAreCollinear(int dX, int dY) {
        return dX == 0 || dY == 0;
    }

    private boolean foodIsNearby(Coordinates current, Coordinates target) {
        return area.getLandscapeEntities().keySet().stream()
                .filter(coordinates -> ((Math.abs(current.x - target.x) == 1 && current.y == target.y)
                        || (Math.abs(current.y - target.y) == 1 && current.x == target.x)))
                .anyMatch(coordinates -> coordinates.x == target.x && coordinates.y == target.y);
    }

    private Set<PathNode> getAvailableNodesToMove(PathNode currentNode, Creature creature) {
        int creatureSpeed;

        if (area.getLandscapeEntities().get(currentNode.coordinates) instanceof Water) {
            creatureSpeed = 1;
        } else {
            creatureSpeed = creature.getMoveSpeed();
        }

        Set<PathNode> availableNodesX = getAvailableNodesByX(currentNode.coordinates, creatureSpeed);
        Set<PathNode> availableNodesY = getAvailableNodesByY(currentNode.coordinates, creatureSpeed);

        Set<PathNode> availableNodes = new HashSet<>(availableNodesX);
        availableNodes.addAll(availableNodesY);

        return availableNodes;
    }

    private Set<PathNode> getAvailableNodesByX(Coordinates nodeCoordinates, int speed) {
        Set<PathNode> availableNodes = new HashSet<>(speed * 2);

        for (int i = -speed; i <= speed; i++) {
            if (i == 0) {
                continue;
            }

            PathNode currentCheckNode = new PathNode(new Coordinates(nodeCoordinates.x + i, nodeCoordinates.y));

            if (currentCheckNode.coordinates.x < 0 || currentCheckNode.coordinates.x >= area.size) {
                continue;
            }

            if (area.getLandscapeEntities().get(currentCheckNode.coordinates).canStep()
                    && !creatures.containsKey(currentCheckNode.coordinates)) {
                availableNodes.add(currentCheckNode);
            } else if (i < 0) {
                availableNodes.clear();
            } else {
                break;
            }
        }

        return availableNodes;
    }

    private Set<PathNode> getAvailableNodesByY(Coordinates nodeCoordinates, int speed) {
        Set<PathNode> availableNodes = new HashSet<>(speed * 2);

        for (int i = -speed; i <= speed; i++) {
            if (i == 0) {
                continue;
            }

            PathNode currentCheckNode = new PathNode(new Coordinates(nodeCoordinates.x, nodeCoordinates.y + i));

            if (currentCheckNode.coordinates.y < 0 || currentCheckNode.coordinates.y >= area.size) {
                continue;
            }

            if (area.getLandscapeEntities().get(currentCheckNode.coordinates).canStep()
                    && !creatures.containsKey(currentCheckNode.coordinates)) {
                availableNodes.add(currentCheckNode);
            } else if (i < 0) {
                availableNodes.clear();
            } else {
                break;
            }
        }

        return availableNodes;
    }
}
