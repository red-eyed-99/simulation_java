package simulation.entities.food_resources;

import simulation.entities.Entity;

public class Grass extends Entity {
    @Override
    public boolean canStep() {
        return false;
    }
}
