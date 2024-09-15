package main;

import main.entities.creatures.herbivores.Herbivore;
import main.entities.creatures.herbivores.Ostrich;
import main.entities.landscape.food_resources.Grass;

public class AddEntityAction extends Action{
    private final Area area;
    private final AreaGenerator areaGenerator;

    public AddEntityAction(Area area, AreaGenerator areaGenerator) {
        this.area = area;
        this.areaGenerator = areaGenerator;
    }

    @Override
    public void execute() {
        if (area.getLandscapeEntities().keySet().stream().filter(coordinates -> area.getLandscapeEntities().get(coordinates) instanceof Grass).count() < 2) {
            areaGenerator.generateEntity(new Grass());
        }
//        if (area.getCreatures().keySet().stream().filter(coordinates -> area.getCreatures().get(coordinates) instanceof Herbivore).count() < 2) {
//            areaGenerator.generateEntity(new Ostrich());
//        }
    }
}
