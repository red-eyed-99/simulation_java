package main.area;

import main.Coordinates;
import main.entities.Entity;
import main.entities.creatures.Creature;
import main.entities.creatures.herbivores.*;
import main.entities.creatures.predators.*;
import main.entities.landscape.LandscapeEntity;
import main.entities.landscape.food_resources.Grass;
import main.entities.landscape.static_objects.Rock;
import main.entities.landscape.static_objects.Tree;
import main.entities.landscape.surface.Ground;
import main.entities.landscape.surface.Water;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class AreaGenerator {
    private final Area area;
    private final Random random = new Random();

    private List<Coordinates> areaGroundCoordinates;
    private final Set<Coordinates> areaWaterCoordinates = new HashSet<>();

    public AreaGenerator(Area area) {
        this.area = area;

        areaGroundCoordinates = new ArrayList<>(area.getLandscapeEntities().size());
    }

    public Area generateArea() {
        // landscape
        generateGround();
        generateWater();

        generateEntity(new Tree(), getEntityCountToGenerate(Tree.class));
        generateEntity(new Rock(), getEntityCountToGenerate(Rock.class));
        generateEntity(new Grass(), getEntityCountToGenerate(Grass.class));

        // creatures

        // herbivores
        generateEntity(new Elephant(7, 1), getEntityCountToGenerate(Elephant.class));
        generateEntity(new Ostrich(3, 3), getEntityCountToGenerate(Ostrich.class));
        generateEntity(new Antelope(3, 3), getEntityCountToGenerate(Antelope.class));
        generateEntity(new Zebra(4, 2), getEntityCountToGenerate(Zebra.class));
        generateEntity(new Buffalo(4, 2), getEntityCountToGenerate(Buffalo.class));

        // predators
        generateEntity(new Lion(4, 2), getEntityCountToGenerate(Lion.class));
        generateEntity(new Panther(3, 2), getEntityCountToGenerate(Panther.class));
        generateEntity(new Tiger(3, 2), getEntityCountToGenerate(Tiger.class));

        return area;
    }

    public void generateEntity(Entity entity, int entityCount) {
        updateAreaGroundCoordinates();

        for (int i = 0; i < entityCount; i++) {
            Coordinates groundCoordinates = getRandomCoordinates(areaGroundCoordinates);

            int tryCount = 0;

            while (creatureDetected(groundCoordinates, entity)) {
                groundCoordinates = getRandomCoordinates(areaGroundCoordinates);
                tryCount++;

                if (tryCount >= 3) {
                    return;
                }
            }

            entity = placeEntity(entity, groundCoordinates);
        }
    }

    private boolean creatureDetected(Coordinates coordinates, Entity entity) {
        if (entity instanceof Herbivore) {
            return area.getCreatures().containsKey(coordinates) || isPredatorNearby(coordinates);
        }

        return area.getCreatures().containsKey(coordinates);
    }

    private Entity placeEntity(Entity entity, Coordinates coordinates) {
        try {
            if (entity instanceof Creature creature) {
                area.getCreatures().put(coordinates, creature);
                entity = creature.getClass().getConstructor(Integer.TYPE, Integer.TYPE)
                        .newInstance(creature.getHealthPoint(), creature.getMoveSpeed());
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

        area.setWaterCoordinates(areaWaterCoordinates);
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
        areaWaterCoordinates.add(waterSource);
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
            areaWaterCoordinates.add(groundCoordinates);
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

    private int getEntityCountToGenerate(Class<? extends Entity> entityClass) {
        double multiplier = EntityGenerationMultipliers.getMultiplier(entityClass);

        return (int) (area.getLandscapeEntities().size() * multiplier);
    }

    private boolean isPredatorNearby(Coordinates coordinates) {
        return area.getCreatures().keySet().stream()
                .anyMatch(creatureCoordinates -> ((Math.abs(creatureCoordinates.x - coordinates.x) == 1 && creatureCoordinates.y == coordinates.y)
                        || (Math.abs(creatureCoordinates.y - coordinates.y) == 1 && creatureCoordinates.x == coordinates.x))
                        && (area.getCreatures().get(creatureCoordinates) instanceof Predator));
    }
}


