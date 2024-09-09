package main;

import main.entities.landscape.surface.Ground;
import main.entities.landscape.surface.Water;

import java.util.*;
import java.util.stream.Collectors;

public class AreaGenerator {
    private final Area area;
    private final Random random = new Random();
    private final ArrayList<Coordinates> areaGroundCoordinates;
    private final Map<EntityType, Double> entityMultipliers = new HashMap<>();

    private static final double TREE_MULTIPLIER = 0.2;
    private static final double ROCK_MULTIPLIER = 0.1;
    private static final double GRASS_MULTIPLIER = 0.2;

    public AreaGenerator(Area area) {
        this.area = area;

        areaGroundCoordinates = new ArrayList<>(area.size * area.size);

        setMultipliers();
    }

    private void setMultipliers() {
        entityMultipliers.put(EntityType.TREE, TREE_MULTIPLIER);
        entityMultipliers.put(EntityType.ROCK, ROCK_MULTIPLIER);
        entityMultipliers.put(EntityType.GRASS, GRASS_MULTIPLIER);
    }

    public Area generateArea() {
        generateEntity(EntityType.GROUND);
        generateEntity(EntityType.WATER);
        generateEntity(EntityType.TREE);
        generateEntity(EntityType.ROCK);
        generateEntity(EntityType.GRASS);

        return area;
    }

    private void generateEntity(EntityType type) {
        EntityFactory factory = new EntityFactory();

        if (type == EntityType.GROUND) {
            generateGround();
        } else if (type == EntityType.WATER) {
            generateWater();
        } else {
            int entityCount = (int) (area.entities.size() * entityMultipliers.get(type));
            for (int i = 0; i < entityCount; i++) {
                Coordinates groundCoordinates = getRandomCoordinates();
                area.entities.replace(groundCoordinates, factory.createEntity(type));
                areaGroundCoordinates.remove(groundCoordinates);
            }
        }
    }

    private void generateGround() {
        for (int x = 0; x < area.size; x++) {
            for (int y = 0; y < area.size; y++) {
                Coordinates groundCoordinates = new Coordinates(x, y);
                area.entities.put(groundCoordinates, new Ground());
                areaGroundCoordinates.add(groundCoordinates);
            }
        }
    }

    private void generateWater() {
        int randomWaterCount = random.nextInt(area.size / 2) + 1;

        for (int i = 0; i < randomWaterCount; i++) {
            int randomWaterSize = random.nextInt(area.size / 2) + 1;

            Coordinates waterSourceCoordinates = getRandomWaterSourceCoordinates();

            placeWaterSurface(waterSourceCoordinates, randomWaterSize);
        }
    }

    private Coordinates getRandomWaterSourceCoordinates() {
        while (true) {
            Coordinates coordinates = new Coordinates(
                    random.nextInt(area.size),
                    random.nextInt(area.size));

            if (area.entities.get(coordinates) instanceof Ground) {
                return coordinates;
            }
        }
    }

    private void placeWaterSurface(Coordinates waterSource, int waterSize) {
        ArrayList<Coordinates> waterNearGroundCoordinates = new ArrayList<>();
        waterNearGroundCoordinates.add(waterSource);

        for (int i = 0; i < waterSize; i++) {
            waterNearGroundCoordinates = waterNearGroundCoordinates.stream()
                    .filter(entityCoordinates -> !getNearbyGroundCoordinates(entityCoordinates).isEmpty())
                    .collect(Collectors.toCollection(ArrayList::new));

            Coordinates waterCoordinates = getRandomCoordinates(waterNearGroundCoordinates);

            ArrayList<Coordinates> groundNearWaterCoordinates = getNearbyGroundCoordinates(waterCoordinates);

            Coordinates groundCoordinates = getRandomCoordinates(groundNearWaterCoordinates);

            area.entities.replace(groundCoordinates, new Water());

            waterNearGroundCoordinates.add(groundCoordinates);

            areaGroundCoordinates.remove(groundCoordinates);
        }
    }

    private Coordinates getRandomCoordinates() {
        return areaGroundCoordinates.get(random.nextInt(areaGroundCoordinates.size()));
    }

    private Coordinates getRandomCoordinates(List<Coordinates> coordinates) {
        return coordinates.get(random.nextInt(coordinates.size()));
    }

    private ArrayList<Coordinates> getNearbyGroundCoordinates(Coordinates coordinates) {
        return area.entities.keySet().stream()
                .filter(entityCoordinates -> (Math.abs(entityCoordinates.x - coordinates.x) == 1 && entityCoordinates.y == coordinates.y)
                        || (Math.abs(entityCoordinates.y - coordinates.y) == 1 && entityCoordinates.x == coordinates.x)
                        && area.entities.get(entityCoordinates) instanceof Ground)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
