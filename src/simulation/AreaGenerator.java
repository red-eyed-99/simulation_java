package simulation;

import simulation.entities.landscape.surface.Ground;
import simulation.entities.landscape.surface.Water;

import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;

public class AreaGenerator {
    private Area area;
    private Random random = new Random();

    public AreaGenerator(Area area) {
        this.area = area;
    }

    public void generateArea() {
        generateGround();
        generateWater();
    }

    private void generateGround() {
        for (int x = 0; x < area.size; x++) {
            for (int y = 0; y < area.size; y++) {
                area.entities.put(new Coordinates(x, y), new Ground());
            }
        }
    }

    private void generateWater() {
        int randomWaterCount = random.nextInt(area.size) + 10;

        ArrayList<Coordinates> waterEntityCoordinates = new ArrayList<>(randomWaterCount);

        for (int i = 0; i < randomWaterCount; i++) {
            int randomWaterSize = random.nextInt(area.size ) + 5;

            while (true) {
                Coordinates randomCoordinates = new Coordinates(
                        random.nextInt(area.size),
                        random.nextInt(area.size));

                if (!waterEntityCoordinates.contains(randomCoordinates)) {
                    waterEntityCoordinates.add(randomCoordinates);
                    break;
                }
            }

            for (int j = 1; j < randomWaterSize; j++) {
                ArrayList<Coordinates> waterEntitiesNearGroundCoordinates = waterEntityCoordinates.stream()
                        .filter(entityCoordinates -> !getNearbyGroundCoordinatesTo(entityCoordinates).isEmpty())
                        .collect(Collectors.toCollection(ArrayList::new));

                Coordinates randomWaterEntityCoordinates = waterEntitiesNearGroundCoordinates
                        .get(random.nextInt(waterEntitiesNearGroundCoordinates.size()));

                ArrayList<Coordinates> groundEntitiesNearWater = getNearbyGroundCoordinatesTo(randomWaterEntityCoordinates);

                Coordinates randomGroundNearWaterCoordinates = groundEntitiesNearWater
                        .get(random.nextInt(groundEntitiesNearWater.size()));

                area.entities.replace(randomGroundNearWaterCoordinates, new Water());
            }
        }
    }

    private ArrayList<Coordinates> getNearbyGroundCoordinatesTo(Coordinates coordinates) {
        return area.entities.keySet().stream()
                .filter(entityCoordinates -> (Math.abs(entityCoordinates.x - coordinates.x) == 1 && entityCoordinates.y == coordinates.y)
                        || (Math.abs(entityCoordinates.y - coordinates.y) == 1 && entityCoordinates.x == coordinates.x)
                        && area.entities.get(entityCoordinates) instanceof Ground)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
