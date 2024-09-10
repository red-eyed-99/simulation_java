package main;

import main.entities.landscape.surface.Ground;
import main.entities.landscape.surface.Water;

import java.util.*;
import java.util.stream.Collectors;

public class AreaGenerator {
    private final Area area;
    private final Random random = new Random();
    private final ArrayList<Coordinates> areaGroundCoordinates;

    public AreaGenerator(Area area) {
        this.area = area;

        areaGroundCoordinates = new ArrayList<>(area.size * area.size);
    }

    public Area generateArea() {
        // landscape
        generateEntity(EntityType.GROUND);
        generateEntity(EntityType.WATER);
        generateEntity(EntityType.TREE);
        generateEntity(EntityType.ROCK);
        generateEntity(EntityType.GRASS);

        // creatures

        // herbivores
        //generateEntity(EntityType.ELEPHANT);
        //generateEntity(EntityType.GIRAFEE);
        generateEntity(EntityType.OSTRICH);
        //generateEntity(EntityType.RHINO);

        // predators
        //generateEntity(EntityType.CROCODILE);
        generateEntity(EntityType.LION);
        //generateEntity(EntityType.PANTHER);
        //generateEntity(EntityType.TIGER);

        return area;
    }

    private void generateEntity(EntityType type) {
        EntityFactory factory = new EntityFactory();

        if (type == EntityType.GROUND) {
            generateGround();
        } else if (type == EntityType.WATER) {
            generateWater();
        } else {
            int entityCount = (int) (area.landscape.size() * EntityGenerationMultipliers.getMultiplier(type));
            for (int i = 0; i < entityCount; i++) {
                Coordinates groundCoordinates = getRandomCoordinates(areaGroundCoordinates);
                area.landscape.replace(groundCoordinates, factory.createEntity(type));
                areaGroundCoordinates.remove(groundCoordinates);
            }
        }
    }

    private void generateGround() {
        for (int x = 0; x < area.size; x++) {
            for (int y = 0; y < area.size; y++) {
                Coordinates groundCoordinates = new Coordinates(x, y);
                area.landscape.put(groundCoordinates, new Ground());
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

            if (area.landscape.get(coordinates) instanceof Ground) {
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

            area.landscape.replace(groundCoordinates, new Water());

            waterNearGroundCoordinates.add(groundCoordinates);

            areaGroundCoordinates.remove(groundCoordinates);
        }
    }

    private Coordinates getRandomCoordinates(List<Coordinates> coordinates) {
        return coordinates.get(random.nextInt(coordinates.size()));
    }

    private ArrayList<Coordinates> getNearbyGroundCoordinates(Coordinates coordinates) {
        return area.landscape.keySet().stream()
                .filter(entityCoordinates -> (Math.abs(entityCoordinates.x - coordinates.x) == 1 && entityCoordinates.y == coordinates.y)
                        || (Math.abs(entityCoordinates.y - coordinates.y) == 1 && entityCoordinates.x == coordinates.x)
                        && area.landscape.get(entityCoordinates) instanceof Ground)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
