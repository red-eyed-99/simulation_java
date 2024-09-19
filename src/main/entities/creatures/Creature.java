package main.entities.creatures;

import main.entities.Entity;

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

    private CreatureViewDirection viewDirection = CreatureViewDirection.RIGHT;

    public int getMoveSpeed() {
        return moveSpeed;
    }

    public int getHealthPoint() {
        return healthPoint;
    }

    public CreatureStatus getStatus() {
        return status;
    }

    public void setStatus(CreatureStatus status) {
        this.status = status;
    }

    public CreatureViewDirection getViewDirection() {
        return viewDirection;
    }

    public void makeMove() {
        // todo
    }

    public void eat() {
        if (healthPoint < maxHealthPoint) {
            healthPoint++;
        }
    }

    public void takeDamage() {
        if (healthPoint > 0) {
            healthPoint--;
        }
    }

    public void changeViewDirection(int xBefore, int xAfter) {
        if (xBefore < xAfter) {
            viewDirection = CreatureViewDirection.RIGHT;
        } else {
            viewDirection = CreatureViewDirection.LEFT;
        }
    }

}
