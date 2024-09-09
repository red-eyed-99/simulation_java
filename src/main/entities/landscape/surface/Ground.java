package main.entities.landscape.surface;

import main.entities.Entity;

public class Ground extends Entity {
    @Override
    public boolean canStep() {
        return true;
    }
}
