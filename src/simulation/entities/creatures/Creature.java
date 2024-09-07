package simulation.entities.creatures;

import simulation.entities.Entity;

import java.util.HashSet;
import java.util.Set;

public abstract class Creature extends Entity {
    private int healthPoint;
    private int moveSpeed = 2;
    private int runSpeed;

    private Set<Entity> pathToFood = new HashSet<>();

    public abstract void makeMove();

    @Override
    public boolean canStep() {
        return false;
    }
}
