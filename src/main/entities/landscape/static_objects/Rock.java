package main.entities.landscape.static_objects;

import main.entities.Entity;

public class Rock extends Entity {
    @Override
    public boolean canStep() {
        return false;
    }
}
