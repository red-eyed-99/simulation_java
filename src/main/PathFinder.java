package main;

import main.entities.creatures.Creature;
import main.entities.creatures.herbivores.Herbivore;
import main.entities.landscape.food_resources.Grass;
import main.entities.landscape.surface.Water;

import java.util.*;
import java.util.stream.Collectors;

public class PathFinder {
    private final Area area;

    public PathFinder(Area area) {
        this.area = area;
    }

    class PathNode {
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

    public Stack<Coordinates> getPathToFood(Coordinates creatureCoordinates, Creature creature) {
        PathNode targetNode = new PathNode(getNearFoodCoordinates(creatureCoordinates, creature));

        Set<PathNode> checkedNodes = new HashSet<>();
        Set<PathNode> nodesForCheck = new HashSet<>();

        PathNode startNode = new PathNode(creatureCoordinates);
        startNode.approximatePathLength = getApproximatePathLength(creatureCoordinates, targetNode.coordinates, creature);

        nodesForCheck.add(startNode);

        while (nodesForCheck.size() > 0) {
            PathNode currentNode = nodesForCheck.stream()
                    .min(Comparator.comparing(PathNode::getExpectedFullPathLength))
                    .get();

            if (foodIsNearby(currentNode.coordinates, targetNode.coordinates)) {
                return pavePath(currentNode);
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

        return null;
    }

    private Stack<Coordinates> pavePath(PathNode targetNode) {
        Stack<Coordinates> coordinatesToTarget = new Stack<>();

        PathNode currentNode = targetNode;

        while (currentNode != null) {
            coordinatesToTarget.push(currentNode.coordinates);
            currentNode = currentNode.previousNode;
        }

        if (coordinatesToTarget.size() == 1) {
            coordinatesToTarget.clear();
        }

        coordinatesToTarget.removeLast();

        return coordinatesToTarget;
    }

    private Coordinates getNearFoodCoordinates(Coordinates coordinates, Creature creature) {
        List<Coordinates> foodCoordinatesList;

        if (creature instanceof Herbivore) {
            foodCoordinatesList = area.getLandscapeEntities().keySet().stream()
                    .filter(entityCoordinates -> area.getLandscapeEntities().get(entityCoordinates) instanceof Grass)
                    .collect(Collectors.toCollection(ArrayList::new));
        } else {
            foodCoordinatesList = area.getLandscapeEntities().keySet().stream()
                    .filter(entityCoordinates -> area.getCreatures().get(entityCoordinates) instanceof Herbivore)
                    .collect(Collectors.toCollection(ArrayList::new));
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

            if (area.getLandscapeOrCreature(currentCheckNode.coordinates).canStep()) {
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

            if (area.getLandscapeOrCreature(currentCheckNode.coordinates).canStep()) {
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
