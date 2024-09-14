package main.entities.creatures;

import main.CreatureStatus;
import main.CreatureViewDirection;
import main.entities.Entity;

import java.util.HashSet;
import java.util.Set;

public abstract class Creature extends Entity {
    private int healthPoint;
    private int moveSpeed = 2;
    private int runSpeed;

    private CreatureStatus status;

    public CreatureViewDirection viewDirection = CreatureViewDirection.RIGHT;

    public void makeMove() {

    }

    public void eat() {

    }

    public int getMoveSpeed() {
        return moveSpeed;
    }

    public CreatureStatus getStatus() {
        return status;
    }

    public void setStatus(CreatureStatus status) {
        this.status = status;
    }
}
