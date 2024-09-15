package main;

import main.entities.creatures.Creature;
import main.entities.landscape.LandscapeEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Simulation {
    private int movesCount = 0;

    private Area area;
    private AreaGenerator areaGenerator;

    private List<Action> turnActions = new ArrayList<>();

    private SimulationObserver observer;

    public Simulation(Area area, AreaGenerator areaGenerator) {
        this.area = area;
        this.areaGenerator = areaGenerator;

        setActions();
    }

    public void addObserver(SimulationObserver observer) {
        this.observer = observer;
    }

    private void notifyLandscapeUpdated(Map<Coordinates, LandscapeEntity> oldLandscape) {
        observer.onAreaLandscapeUpdated(oldLandscape);
    }

    private void notifyCreaturesUpdated(Map<Coordinates, Creature> oldCreatures) {
        observer.onAreaCreaturesUpdated(oldCreatures);
    }

    private void setActions() {
        turnActions.add(new CreaturesAction(area));
        turnActions.add(new AddEntityAction(area, areaGenerator));
    }

    public int getMovesCount() {
        return movesCount;
    }

    public Area getArea() {
        return area;
    }

    public void nextTurn() {
        Map<Coordinates, LandscapeEntity> oldLandscape = Map.copyOf(area.getLandscapeEntities());

        Map<Coordinates, Creature> oldCreatures = Map.copyOf(area.getCreatures());

        turnActions.get(1).execute();
        turnActions.get(0).execute();
        notifyLandscapeUpdated(oldLandscape);
        notifyCreaturesUpdated(oldCreatures);
    }


    public void startSimulation() {

    }

    public void pauseSimulation() {

    }
}
