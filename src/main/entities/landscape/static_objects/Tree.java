package main.entities.landscape.static_objects;

import main.entities.Entity;

public class Tree extends Entity {
    @Override
    public boolean canStep() {
        return false;
    }
}
