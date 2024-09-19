package main;

import javafx.application.Platform;
import javafx.concurrent.Task;
import main.actions.Action;
import main.actions.AddEntityAction;
import main.actions.MoveCreaturesAction;
import main.area.Area;
import main.area.AreaGenerator;
import main.entities.creatures.Creature;
import main.entities.landscape.LandscapeEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Simulation {
    private int movesCount = 0;

    private final Area area;
    private final AreaGenerator areaGenerator;

    private final List<Action> turnActions = new ArrayList<>();

    private Map<Coordinates, LandscapeEntity> landscapeBeforeRedraw;
    private Map<Coordinates, Creature> creaturesBeforeRedraw;

    private SimulationObserver observer;

    private final SimulationThread simulationThread;

    public Simulation(Area area, AreaGenerator areaGenerator) {
        this.area = area;
        this.areaGenerator = areaGenerator;

        setActions();

        updateEntitiesBeforeRedraw();

        simulationThread = new SimulationThread();
        simulationThread.setDaemon(true);
    }

    private void setActions() {
        turnActions.add(new MoveCreaturesAction(area));
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
        movesCount++;
        notifyObserverForRedraw();
        updateEntitiesBeforeRedraw();
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
        notifyLandscapeUpdated();
        notifyCreaturesUpdated();
        notifyMovesCountIncreased();
    }

    private void notifyMovesCountIncreased() {
        observer.onMovesCountIncrease(movesCount);
    }

    private void notifyLandscapeUpdated() {
        observer.onAreaLandscapeUpdated(landscapeBeforeRedraw);
    }

    private void notifyCreaturesUpdated() {
        observer.onAreaCreaturesUpdated(creaturesBeforeRedraw, landscapeBeforeRedraw);
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

                    Thread.sleep(250);

                    updateEntitiesBeforeRedraw();

                    turnActions.get(0).execute();

                    movesCount++;

                    Platform.runLater(Simulation.this::notifyObserverForRedraw);

                    Thread.sleep(250);

                    updateEntitiesBeforeRedraw();
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
