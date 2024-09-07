package simulation.entities.landscape.surface;

import simulation.entities.Entity;

public class Water extends Entity {
    @Override
    public boolean canStep() {
        return true;
    }
}
