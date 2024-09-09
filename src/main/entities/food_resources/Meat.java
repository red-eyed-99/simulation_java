package main.entities.food_resources;

import main.entities.Entity;

public class Meat extends Entity {

    @Override
    public boolean canStep() {
        return false;
    }
}
