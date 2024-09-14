package main;

import main.entities.creatures.Creature;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Simulation {
    private int movesCount = 0;

    private Area area;

    //private List<Action> initActions;
    private List<Action> turnActions = new ArrayList<>();

    public Simulation(Area area) {
        this.area = area;
        setActions();
    }

    private void setActions() {
        turnActions.add(new CreaturesAction(area));
    }

    public int getMovesCount() {
        return movesCount;
    }

    public Area getArea() {
        return area;
    }

    public void nextTurn() {
        turnActions.get(0).execute();
    }

    public void startSimulation() {

    }

    public void pauseSimulation() {

    }
}
