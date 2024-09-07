package simulation.entities.landscape.static_objects;

import simulation.entities.Entity;

public class Rock extends Entity {
    @Override
    public boolean canStep() {
        return false;
    }
}
