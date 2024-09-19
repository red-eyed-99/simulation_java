package main.actions;

import main.area.Area;
import main.area.AreaGenerator;
import main.entities.creatures.herbivores.*;
import main.entities.landscape.food_resources.Grass;

import java.util.Random;

public class AddEntityAction extends Action {
    private final Area area;
    private final AreaGenerator areaGenerator;
    private final Random random = new Random();

    public AddEntityAction(Area area, AreaGenerator areaGenerator) {
        this.area = area;
        this.areaGenerator = areaGenerator;
    }

    @Override
    public void execute() {
        if (area.getLandscapeEntities().keySet().stream()
                .filter(coordinates -> area.getLandscapeEntities().get(coordinates) instanceof Grass)
                .count() < area.getHerbivoresCount()) {
            areaGenerator.generateEntity(new Grass(), 3);
        }

        if (area.getHerbivoresCount() < area.getPredatorsCount()) {
            try {
                for (int i = 0; i < 3; i++) {
                    areaGenerator.generateEntity(getRandomHerbivore(), 1);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Herbivore getRandomHerbivore() throws RuntimeException {
        int number = random.nextInt(5);

        return switch (number) {
            case 0 -> new Antelope(3, 3);
            case 1 -> new Buffalo(4, 2);
            case 2 -> new Elephant(7, 1);
            case 3 -> new Ostrich(3, 3);
            case 4 -> new Zebra(4, 2);
            default -> throw new RuntimeException();
        };
    }
}
