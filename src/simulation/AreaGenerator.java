package simulation;

import simulation.entities.landscape.surface.Ground;
import simulation.entities.landscape.surface.Water;

import java.util.ArrayList;
import java.util.List;
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
                    .filter(entityCoordinates -> !getNearbyGroundCoordinatesTo(entityCoordinates).isEmpty())
                    .collect(Collectors.toCollection(ArrayList::new));

            Coordinates waterCoordinates = getRandomCoordinates(waterNearGroundCoordinates);

            ArrayList<Coordinates> groundNearWaterCoordinates = getNearbyGroundCoordinatesTo(waterCoordinates);

            Coordinates groundCoordinates = getRandomCoordinates(groundNearWaterCoordinates);

            area.entities.replace(groundCoordinates, new Water());

            waterNearGroundCoordinates.add(groundCoordinates);
        }
    }

    private Coordinates getRandomCoordinates(List<Coordinates> coordinates) {
        return coordinates.get(random.nextInt(coordinates.size()));
    }

    private ArrayList<Coordinates> getNearbyGroundCoordinatesTo(Coordinates coordinates) {
        return area.entities.keySet().stream()
                .filter(entityCoordinates -> (Math.abs(entityCoordinates.x - coordinates.x) == 1 && entityCoordinates.y == coordinates.y)
                        || (Math.abs(entityCoordinates.y - coordinates.y) == 1 && entityCoordinates.x == coordinates.x)
                        && area.entities.get(entityCoordinates) instanceof Ground)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
