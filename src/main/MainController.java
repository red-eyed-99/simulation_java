package main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import main.entities.Entity;
import main.entities.creatures.Creature;
import main.entities.landscape.LandscapeEntity;
import main.entities.landscape.food_resources.Meat;
import main.entities.landscape.surface.Ground;
import main.entities.landscape.surface.Surface;
import main.entities.landscape.surface.Water;
import main.ui.custom_controls.ZoomableScrollPane;

import java.util.*;

public class MainController implements SimulationObserver {
    private Simulation simulation;

    @FXML
    private GridPane areaGrid;

    @FXML
    private ZoomableScrollPane zoomableScrollPane;

    @FXML
    private Button nextTurnButton;

    @FXML
    private Button startSimulationButton;

    @FXML
    private Button pauseSimulationButton;

    @FXML
    private void initialize() {
        zoomableScrollPane.setZoomableTarget(areaGrid);
    }

    @FXML
    private void nextTurnButtonClick(ActionEvent event) {
        simulation.nextTurn();
    }

    @FXML
    private void startSimulationButtonClick(ActionEvent event) {
        startSimulationButton.setDisable(true);
        nextTurnButton.setDisable(true);
        pauseSimulationButton.setDisable(false);

        simulation.startSimulation();
    }

    @FXML
    private void pauseSimulationButtonClick(ActionEvent event) {
        startSimulationButton.setDisable(false);
        nextTurnButton.setDisable(false);
        pauseSimulationButton.setDisable(true);

        simulation.pauseSimulation();
    }

    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
        this.simulation.addObserver(this);
    }

    public void fillAreaGridCells() {
        for (int row = 0; row < simulation.getArea().size; row++) {
            areaGrid.getRowConstraints().add(new RowConstraints());
            for (int column = 0; column < simulation.getArea().size; column++) {
                areaGrid.getColumnConstraints().add(new ColumnConstraints());

                areaGrid.getRowConstraints().get(row).setPrefHeight(100);
                areaGrid.getColumnConstraints().get(column).setPrefWidth(100);

                Entity entity = simulation.getArea().getLandscapeOrCreature(new Coordinates(column, row));

                // ground rendering implements in style.css as background
                if (entity instanceof Ground) {
                    continue;
                }

                areaGrid.add(getEntityImageView(entity), column, row);
            }
        }
    }

    private ImageView getEntityImageView(Entity entity) {
        String pathToImage;
        pathToImage = ImagePathsManager.getEntityImagePath(entity);

        Image image = new Image(Objects.requireNonNull(Main.class.getResourceAsStream(pathToImage)));

        return new ImageView(image);
    }

    private ImageView getAreaGridCellLandscapeImage(int col, int row) {
        List<ImageView> images = new ArrayList<>();

        for (Node node : areaGrid.getChildren()) {
            if (node instanceof ImageView imageView
                    && GridPane.getColumnIndex(node) == col
                    && GridPane.getRowIndex(node) == row) {
                images.add(imageView);
            }
        }

        if (!images.isEmpty()) {
            return images.getLast();
        }

        throw new NoSuchElementException("Landscape ImageView could not be found at the given coordinates");
    }

    private ImageView getAreaGridCellLandscapeImage(int col, int row, int zIndex) {
        List<ImageView> images = new ArrayList<>();

        for (Node node : areaGrid.getChildren()) {
            if (node instanceof ImageView imageView
                    && GridPane.getColumnIndex(node) == col
                    && GridPane.getRowIndex(node) == row) {
                images.add(imageView);
            }
        }

        if (!images.isEmpty()) {
            return images.get(zIndex);
        }

        throw new NoSuchElementException("Landscape ImageView could not be found at the given coordinates");
    }

    private ImageView getAreaGridCellCreatureImage(int col, int row) {
        List<ImageView> images = new ArrayList<>();

        for (Node node : areaGrid.getChildren()) {
            if (node instanceof ImageView imageView
                    && GridPane.getColumnIndex(node) == col
                    && GridPane.getRowIndex(node) == row) {
                images.add(imageView);
            }
        }

        if (!images.isEmpty()) {
            return images.getLast();
        }

        throw new NoSuchElementException("Landscape ImageView could not found by given coordinates");
    }


    @Override
    public void onAreaLandscapeUpdated(Map<Coordinates, LandscapeEntity> oldLandscape) {
        Map<Coordinates, LandscapeEntity> newLandscape = simulation.getArea().getLandscapeEntities();

        for (Map.Entry<Coordinates, LandscapeEntity> entry : newLandscape.entrySet()) {
            LandscapeEntity newLandscapeEntity = entry.getValue();
            LandscapeEntity oldLandscapeEntity = oldLandscape.get(entry.getKey());

            if (newLandscapeEntity.getClass() != oldLandscapeEntity.getClass()) {
                ImageView entityImageView;
                Coordinates entityCoordinates = entry.getKey();

                if (oldLandscapeEntity instanceof Surface) {
                    entityImageView = getEntityImageView(newLandscapeEntity);

                    areaGrid.add(entityImageView, entityCoordinates.x, entityCoordinates.y);
                } else {
                    entityImageView = getAreaGridCellLandscapeImage(entityCoordinates.x, entityCoordinates.y);

                    areaGrid.getChildren().remove(entityImageView);
                }
            }
        }
    }

    @Override
    public void onAreaCreaturesUpdated(Map<Coordinates, Creature> oldCreatures, Map<Coordinates, LandscapeEntity> oldLandscape) {
        Map<Coordinates, Creature> newCreatures = new HashMap<>(simulation.getArea().getCreatures());

        removeOutdatedCreaturesImages(newCreatures, oldCreatures, oldLandscape);

        addCurrentCreaturesImages(newCreatures);
    }

    private void removeOutdatedCreaturesImages(
            Map<Coordinates, Creature> newCreatures,
            Map<Coordinates, Creature> oldCreatures,
            Map<Coordinates, LandscapeEntity> oldLandscape) {

        for (Map.Entry<Coordinates, Creature> entry : oldCreatures.entrySet()) {
            Creature oldCreature = entry.getValue();
            Coordinates oldCoordinates = entry.getKey();

            ImageView oldCreatureImageView;

            if (!newCreatures.containsKey(oldCoordinates)) {
                if (simulation.getArea().getLandscapeEntities().get(oldCoordinates) instanceof Meat) {
                    if (oldLandscape.get(oldCoordinates) instanceof Water) {
                        oldCreatureImageView = getAreaGridCellLandscapeImage(oldCoordinates.x, oldCoordinates.y, 1);
                    } else {
                        oldCreatureImageView = getAreaGridCellLandscapeImage(oldCoordinates.x, oldCoordinates.y, 0);
                    }
                } else {
                    oldCreatureImageView = getAreaGridCellCreatureImage(oldCoordinates.x, oldCoordinates.y);
                }
                areaGrid.getChildren().remove(oldCreatureImageView);
            } else if (newCreatures.get(oldCoordinates) != oldCreature) {
                oldCreatureImageView = getAreaGridCellCreatureImage(oldCoordinates.x, oldCoordinates.y);
                areaGrid.getChildren().remove(oldCreatureImageView);
            } else {
                newCreatures.remove(oldCoordinates); // avoid duplicate in next method
            }
        }
    }

    private void addCurrentCreaturesImages(Map<Coordinates, Creature> newCreatures) {
        for (Map.Entry<Coordinates, Creature> entry : newCreatures.entrySet()) {
            Creature creature = entry.getValue();
            Coordinates newCoordinates = entry.getKey();

            ImageView creatureImageView = getEntityImageView(creature);

            areaGrid.add(creatureImageView, newCoordinates.x, newCoordinates.y);
        }
    }
}