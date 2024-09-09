package main.entities.food_resources;

import main.entities.Entity;

public class Grass extends Entity {
    @Override
    public boolean canStep() {
        return false;
    }
}
