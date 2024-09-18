package main;

import javafx.application.Platform;
import javafx.concurrent.Task;
import main.entities.creatures.Creature;
import main.entities.landscape.LandscapeEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Simulation {
    private int movesCount = 0;

    private Area area;
    private AreaGenerator areaGenerator;

    private List<Action> turnActions = new ArrayList<>();

    private Map<Coordinates, LandscapeEntity> landscapeBeforeRedraw;
    private Map<Coordinates, Creature> creaturesBeforeRedraw;

    private SimulationObserver observer;

    private SimulationThread simulationThread;

    public Simulation(Area area, AreaGenerator areaGenerator) {
        this.area = area;
        this.areaGenerator = areaGenerator;

        setActions();

        updateEntitiesBeforeRedraw();

        simulationThread = new SimulationThread();
        simulationThread.setDaemon(true);
    }

    private void setActions() {
        turnActions.add(new CreaturesAction(area));
        turnActions.add(new AddEntityAction(area, areaGenerator));
    }

    public void addObserver(SimulationObserver observer) {
        this.observer = observer;
    }

    public Area getArea() {
        return area;
    }

    public void nextTurn() {
        turnActions.get(1).execute();
        notifyObserverForRedraw();
        updateEntitiesBeforeRedraw();

        turnActions.get(0).execute();
        notifyObserverForRedraw();
        updateEntitiesBeforeRedraw();

        movesCount++;
    }

    public void startSimulation() {
        if (simulationThread.paused) {
            simulationThread.resumeSimulation();
            return;
        }

        simulationThread.start();
    }

    public void pauseSimulation() {
        simulationThread.pause();
    }

    private void notifyObserverForRedraw() {
        notifyLandscapeUpdated(landscapeBeforeRedraw);
        notifyCreaturesUpdated(creaturesBeforeRedraw, landscapeBeforeRedraw);
    }

    private void notifyLandscapeUpdated(Map<Coordinates, LandscapeEntity> oldLandscape) {
        observer.onAreaLandscapeUpdated(oldLandscape);
    }

    private void notifyCreaturesUpdated(Map<Coordinates, Creature> oldCreatures, Map<Coordinates, LandscapeEntity> oldLandscape) {
        observer.onAreaCreaturesUpdated(oldCreatures, oldLandscape);
    }

    private void updateEntitiesBeforeRedraw() {
        landscapeBeforeRedraw = Map.copyOf(area.getLandscapeEntities());
        creaturesBeforeRedraw = Map.copyOf(area.getCreatures());
    }

    private class SimulationThread extends Thread {
        volatile boolean paused = false;

        Task<Void> simulationTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                while (true) {

                    while (paused) {
                        Thread.sleep(1000);
                    }

                    turnActions.get(1).execute();

                    Platform.runLater(Simulation.this::notifyObserverForRedraw);

                    Thread.sleep(50);

                    updateEntitiesBeforeRedraw();

                    turnActions.get(0).execute();

                    Platform.runLater(Simulation.this::notifyObserverForRedraw);

                    Thread.sleep(500);

                    updateEntitiesBeforeRedraw();

                    movesCount++;
                }
            }
        };

        @Override
        public void run() {
            simulationTask.run();
        }

        void pause() {
            paused = true;
        }

        void resumeSimulation() {
            paused = false;
        }
    }
}
