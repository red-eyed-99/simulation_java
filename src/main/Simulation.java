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

    private SimulationObserver observer;

    private Map<Coordinates, LandscapeEntity> landscapeBeforeRedraw;
    private Map<Coordinates, Creature> creaturesBeforeRedraw;

    SimulationThread simulationThread;

    public Simulation(Area area, AreaGenerator areaGenerator) {
        this.area = area;
        this.areaGenerator = areaGenerator;

        setActions();
        updateEntitiesBeforeRedraw();
        simulationThread = new SimulationThread();
        simulationThread.setDaemon(true);

    }

    public void addObserver(SimulationObserver observer) {
        this.observer = observer;
    }

    private void notifyLandscapeUpdated(Map<Coordinates, LandscapeEntity> oldLandscape) {
        observer.onAreaLandscapeUpdated(oldLandscape);
    }

    private void notifyCreaturesUpdated(Map<Coordinates, Creature> oldCreatures, Map<Coordinates, LandscapeEntity> oldLandscape) {
        observer.onAreaCreaturesUpdated(oldCreatures, oldLandscape);
    }

    private void setActions() {
        turnActions.add(new CreaturesAction(area));
        turnActions.add(new AddEntityAction(area, areaGenerator));
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
    }

    public void startSimulation() {
        simulationThread.start();
    }

    public void pauseSimulation() {
        simulationThread.pause();
    }

    private void updateEntitiesBeforeRedraw() {
        landscapeBeforeRedraw = Map.copyOf(area.getLandscapeEntities());
        creaturesBeforeRedraw = Map.copyOf(area.getCreatures());
    }

    private void notifyObserverForRedraw() {
        notifyLandscapeUpdated(landscapeBeforeRedraw);
        notifyCreaturesUpdated(creaturesBeforeRedraw, landscapeBeforeRedraw);
    }

    class SimulationThread extends Thread {
        @Override
        public void run() {
            simulationTask.run();
        }

        public void pause() {
            simulationTask.cancel();
//            try {
//                sleep(900000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
        }
    }

    Task<Void> simulationTask = new Task<>() {
        @Override
        protected Void call() throws Exception {
            while (true) {
                turnActions.get(1).execute();

                Platform.runLater(() -> {
                    notifyObserverForRedraw();
                });

                Thread.sleep(100);

                updateEntitiesBeforeRedraw();

                turnActions.get(0).execute();

                Platform.runLater(() -> {
                    notifyObserverForRedraw();
                });

                Thread.sleep(500);

                updateEntitiesBeforeRedraw();
            }
        }
    };
}
