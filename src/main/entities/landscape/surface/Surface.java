package main.entities.landscape.surface;

import main.entities.landscape.LandscapeEntity;

public abstract class Surface extends LandscapeEntity {
    @Override
    public boolean canStep() {
        return true;
    }
}
