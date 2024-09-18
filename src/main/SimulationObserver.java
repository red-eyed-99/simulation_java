package main;

import main.entities.creatures.Creature;
import main.entities.landscape.LandscapeEntity;

import java.util.Map;
import java.util.Set;

public interface SimulationObserver {
    void onAreaLandscapeUpdated(Map<Coordinates, LandscapeEntity> oldLandscape);
    void onAreaCreaturesUpdated(Map<Coordinates, Creature> oldCreatures, Map<Coordinates, LandscapeEntity> oldLandscape);
}
