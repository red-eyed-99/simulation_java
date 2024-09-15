package main;

import main.entities.Entity;
import main.entities.creatures.Creature;
import main.entities.creatures.herbivores.Ostrich;
import main.entities.creatures.predators.Lion;
import main.entities.landscape.LandscapeEntity;
import main.entities.landscape.food_resources.Grass;
import main.entities.landscape.static_objects.Rock;
import main.entities.landscape.surface.Ground;
import main.entities.landscape.surface.Water;
import main.entities.landscape.static_objects.Tree;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class AreaGenerator {
    private final Area area;
    private final Random random = new Random();
    private List<Coordinates> areaGroundCoordinates;

    public AreaGenerator(Area area) {
        this.area = area;

        areaGroundCoordinates = new ArrayList<>(area.getLandscapeEntities().size());
    }

    public Area generateArea() {
        // landscape
        generateGround();
        generateWater();

        generateEntity(new Tree());
        generateEntity(new Rock());
        generateEntity(new Grass());

        // creatures

        // herbivores
        //generateEntity(new Elephant());
        //generateEntity(new Girafee());
        generateEntity(new Ostrich());
        //generateEntity(new Rhino());

        // predators
        //generateEntity(new Crocodile());
        generateEntity(new Lion());
        //generateEntity(new Panther());
        //generateEntity(new Tiger());

        return area;
    }

    public void generateEntity(Entity entity) {
        updateAreaGroundCoordinates();

        int entityCount = getEntityCountToGenerate(entity);

        for (int i = 0; i < entityCount; i++) {
            Coordinates groundCoordinates = getRandomCoordinates(areaGroundCoordinates);

            int tryCount = 0;

            while (creatureDetected(groundCoordinates)) {
                groundCoordinates = getRandomCoordinates(areaGroundCoordinates);
                tryCount++;

                if (tryCount > 3) {
                    return;
                }
            }

            entity = placeEntity(entity, groundCoordinates);
        }
    }

    private boolean creatureDetected(Coordinates coordinates) {
        return area.getCreatures().containsKey(coordinates);
    }

    private Entity placeEntity(Entity entity, Coordinates coordinates) {
        try {
            if (entity instanceof Creature creature) {
                area.getCreatures().put(coordinates, creature);
                entity = creature.getClass().getDeclaredConstructor().newInstance();
            } else {
                area.getLandscapeEntities().put(coordinates, (LandscapeEntity) entity);
                entity = entity.getClass().getDeclaredConstructor().newInstance();
            }
        } catch (IllegalAccessException
                 | NoSuchMethodException
                 | InvocationTargetException
                 | InstantiationException e) {
            throw new RuntimeException(e);
        }

        areaGroundCoordinates.remove(coordinates);

        return entity;
    }

    private void generateGround() {
        for (int x = 0; x < area.size; x++) {
            for (int y = 0; y < area.size; y++) {
                Coordinates groundCoordinates = new Coordinates(x, y);
                area.getLandscapeEntities().put(groundCoordinates, new Ground());
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

            if (area.getLandscapeEntities().get(coordinates) instanceof Ground) {
                return coordinates;
            }
        }
    }

    private void placeWaterSurface(Coordinates waterSourceCoordinates, int waterSize) {
        List<Coordinates> waterNearGroundCoordinates = new ArrayList<>();
        waterNearGroundCoordinates.add(waterSourceCoordinates);

        placeWaterSource(waterSourceCoordinates);

        if (waterSize > 1) {
            fillSurfaceWithWater(waterNearGroundCoordinates, waterSize);
        }
    }

    private void placeWaterSource(Coordinates waterSource) {
        area.getLandscapeEntities().replace(waterSource, new Water());
        areaGroundCoordinates.remove(waterSource);
    }

    private void fillSurfaceWithWater(List<Coordinates> waterNearGroundCoordinates, int waterSize) {
        for (int i = 1; i < waterSize; i++) {
            waterNearGroundCoordinates = updateWaterNearGroundCoordinates(waterNearGroundCoordinates);

            Coordinates waterCoordinates = getRandomCoordinates(waterNearGroundCoordinates);

            List<Coordinates> groundNearWaterCoordinates = getNearbyGroundCoordinates(waterCoordinates);

            Coordinates groundCoordinates = getRandomCoordinates(groundNearWaterCoordinates);

            area.getLandscapeEntities().replace(groundCoordinates, new Water());

            waterNearGroundCoordinates.add(groundCoordinates);

            areaGroundCoordinates.remove(groundCoordinates);
        }
    }

    private List<Coordinates> updateWaterNearGroundCoordinates(List<Coordinates> waterNearGroundCoordinates) {
        return waterNearGroundCoordinates.stream()
                .filter(coordinates -> !getNearbyGroundCoordinates(coordinates).isEmpty())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private Coordinates getRandomCoordinates(List<Coordinates> coordinates) {
        return coordinates.get(random.nextInt(coordinates.size()));
    }

    private List<Coordinates> getNearbyGroundCoordinates(Coordinates coordinates) {
        return area.getLandscapeEntities().keySet().stream()
                .filter(entityCoordinates -> ((Math.abs(entityCoordinates.x - coordinates.x) == 1 && entityCoordinates.y == coordinates.y)
                        || (Math.abs(entityCoordinates.y - coordinates.y) == 1 && entityCoordinates.x == coordinates.x))
                        && (area.getLandscapeEntities().get(entityCoordinates) instanceof Ground))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private void updateAreaGroundCoordinates() {
        areaGroundCoordinates = area.getLandscapeEntities().keySet().stream()
                .filter(coordinates -> area.getLandscapeEntities().get(coordinates) instanceof Ground)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private int getEntityCountToGenerate(Entity entity) {
        double multiplier = EntityGenerationMultipliers.getMultiplier(entity.getClass());

        return (int) (area.getLandscapeEntities().size() * multiplier);
    }
}


