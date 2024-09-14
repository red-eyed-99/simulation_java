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
import main.entities.landscape.surface.Ground;
import main.ui.custom_controls.ZoomableScrollPane;

import java.util.*;

public class MainController {
    private Simulation simulation;

    @FXML
    private GridPane areaGrid;

    @FXML
    private ZoomableScrollPane zoomableScrollPane;

    @FXML
    private Button nextTurnButton;

    @FXML
    private void initialize() {
        zoomableScrollPane.setZoomableTarget(areaGrid);
    }

    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
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

                areaGrid.add(getEntityImage(entity), column, row);
            }
        }
    }

    private ImageView getEntityImage(Entity entity) {
        String pathToImage;

        pathToImage = ImagePathsManager.getEntityImagePath(entity);

        return new ImageView(new Image(Objects.requireNonNull(Main.class.getResourceAsStream(pathToImage))));
    }

    private ImageView getAreaGridCellLandscapeImage(int col, int row) {
        List<ImageView> images = new ArrayList<>();

        for (Node node : areaGrid.getChildren()) {
            if (node instanceof ImageView imageView
                    && GridPane.getColumnIndex(node) == col
                    && GridPane.getRowIndex(node) == row){
                images.add(imageView);
            }
        }

        if (!images.isEmpty()) {
            return images.getFirst();
        }

        throw new NoSuchElementException();
    }

    private ImageView getAreaGridCellCreatureImage(int col, int row) {
        List<ImageView> images = new ArrayList<>();

        for (Node node : areaGrid.getChildren()) {
            if (node instanceof ImageView imageView
                    && GridPane.getColumnIndex(node) == col
                    && GridPane.getRowIndex(node) == row){
                images.add(imageView);
            }
        }

        if (!images.isEmpty()) {
            return images.getLast();
        }

        throw new NoSuchElementException();
    }

    @FXML
    private void nextTurnButtonClick(ActionEvent event) {
        Set<Coordinates> oldCreaturesCoordinates = Set.copyOf(simulation.getArea().getCreatures().keySet());
        Map<Coordinates, LandscapeEntity> oldLandscape = Map.copyOf(simulation.getArea().getLandscapeEntities());

        simulation.nextTurn();

        updateAreaGridLandscape(oldLandscape);
        updateAreaGridCreaturesPositions(oldCreaturesCoordinates);
    }

    private void updateAreaGridLandscape(Map<Coordinates, LandscapeEntity> oldLandscape) {
        Map<Coordinates, LandscapeEntity> newLandscape = simulation.getArea().getLandscapeEntities();

        for (Map.Entry<Coordinates, LandscapeEntity> entry : oldLandscape.entrySet()) {
            if (!newLandscape.containsValue(entry.getValue())) {
                if (!(entry.getValue() instanceof Ground)) {
                    areaGrid.getChildren().remove(getAreaGridCellLandscapeImage(entry.getKey().x, entry.getKey().y));
                } else {
                    areaGrid.getChildren().remove(getAreaGridCellLandscapeImage(entry.getKey().x, entry.getKey().y));
                    areaGrid.add(getEntityImage(newLandscape.get(entry.getKey())), entry.getKey().x, entry.getKey().y);
                }
            }
        }
    }

    private void updateAreaGridCreaturesPositions(Set<Coordinates> oldCreaturesCoordinates) {
        for (Coordinates coordinates : oldCreaturesCoordinates) {
            areaGrid.getChildren().remove(getAreaGridCellCreatureImage(coordinates.x, coordinates.y));
        }

        Map<Coordinates, Creature> newCreaturesLocation = new HashMap<>(simulation.getArea().getCreatures());

        for (Map.Entry<Coordinates, Creature> entry : newCreaturesLocation.entrySet()) {
            areaGrid.add(getEntityImage(entry.getValue()), entry.getKey().x, entry.getKey().y);
        }
    }
}