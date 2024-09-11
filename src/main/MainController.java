package main;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import main.entities.Entity;
import main.entities.landscape.surface.Ground;
import main.ui.custom_controls.ZoomableScrollPane;

import java.io.FileNotFoundException;

public class MainController {
    private Simulation simulation;

    @FXML
    private GridPane areaGrid;

    @FXML
    private ZoomableScrollPane zoomableScrollPane;

    @FXML
    private void initialize() {
        simulation = new Simulation();

        try {
            fillAreaGridCells(simulation.getArea());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        zoomableScrollPane.setZoomableTarget(areaGrid);
    }

    private void fillAreaGridCells(Area area) throws FileNotFoundException {
        for (int row = 0; row < area.size; row++) {
            areaGrid.getRowConstraints().add(new RowConstraints());
            for (int column = 0; column < area.size; column++) {
                areaGrid.getColumnConstraints().add(new ColumnConstraints());

                Entity entity = area.getLandscapeOrCreature(new Coordinates(row, column));

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

        return new ImageView(new Image(Main.class.getResourceAsStream(pathToImage)));
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