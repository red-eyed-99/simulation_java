package main.entities.landscape.surface;

import main.entities.Entity;

public class Water extends Entity {
    @Override
    public boolean canStep() {
        return true;
    }
}
