package main.entities.creatures;

import main.CreatureViewDirection;
import main.entities.Entity;

import java.util.HashSet;
import java.util.Set;

public abstract class Creature extends Entity {
    private int healthPoint;
    private int moveSpeed = 2;
    private int runSpeed;

    public CreatureViewDirection viewDirection = CreatureViewDirection.RIGHT;

    public abstract void makeMove();

    public int getMoveSpeed() {
        return moveSpeed;
    }
}
