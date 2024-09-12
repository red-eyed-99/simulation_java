package main;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import main.entities.Entity;
import main.entities.landscape.surface.Ground;
import main.ui.custom_controls.ZoomableScrollPane;
import java.util.Objects;

public class MainController {
    private Simulation simulation;

    @FXML
    private GridPane areaGrid;

    @FXML
    private ZoomableScrollPane zoomableScrollPane;

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

    public Node getNodeFromGridPane(GridPane gridPane, int col, int row) {
        for (Node node : gridPane.getChildren()) {
            if (node instanceof ImageView && GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                return node;
            }
        }
        return null;
    }
}