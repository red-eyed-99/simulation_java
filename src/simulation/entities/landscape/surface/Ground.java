package simulation.entities.landscape.surface;

import simulation.entities.Entity;

public class Ground extends Entity {
    @Override
    public boolean canStep() {
        return true;
    }
}
