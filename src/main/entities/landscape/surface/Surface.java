package main.entities.landscape.surface;

import main.entities.Entity;

public abstract class Surface extends Entity {
    @Override
    public boolean canStep() {
        return true;
    }
}
