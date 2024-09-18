package main.entities.creatures;

import main.CreatureStatus;
import main.CreatureViewDirection;
import main.entities.Entity;

import java.util.HashSet;
import java.util.Set;

public abstract class Creature extends Entity {
    private int healthPoint;
    private final int moveSpeed;
    private final int maxHealthPoint;

    public Creature(int healthPoint, int moveSpeed) {
        this.healthPoint = healthPoint;
        this.maxHealthPoint = healthPoint;
        this.moveSpeed = moveSpeed;
    }

    private CreatureStatus status = CreatureStatus.IN_SEARCH_FOOD;

    public CreatureViewDirection viewDirection = CreatureViewDirection.RIGHT;

    public void makeMove() {

    }

    public void eat() {

    }

    public void takeDamage() {
        if (healthPoint > 0) {
            healthPoint--;
        }
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

    public int getHealthPoint() {
        return healthPoint;
    }
}
