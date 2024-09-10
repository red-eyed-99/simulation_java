package main;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import main.entities.Entity;
import main.entities.creatures.Creature;
import main.entities.creatures.herbivores.Elephant;
import main.entities.creatures.herbivores.Girafee;
import main.entities.creatures.herbivores.Ostrich;
import main.entities.creatures.herbivores.Rhino;
import main.entities.creatures.predators.Crocodile;
import main.entities.creatures.predators.Lion;
import main.entities.creatures.predators.Panther;
import main.entities.creatures.predators.Tiger;
import main.entities.food_resources.Grass;
import main.entities.food_resources.Meat;
import main.entities.landscape.static_objects.Rock;
import main.entities.landscape.static_objects.Tree;
import main.entities.landscape.surface.Ground;
import main.entities.landscape.surface.Water;
import main.ui.custom_controls.ZoomableScrollPane;

import java.io.FileNotFoundException;
import java.util.HashMap;

public class MainController {
    private Simulation simulation;

    private final HashMap<Class<? extends Entity>, String> entityPathsToImage = new HashMap<>();
    private final HashMap<Class<? extends Creature>, HashMap<CreatureViewDirection, String>> creaturePathsToImage = new HashMap<>();

    @FXML
    private GridPane areaGrid;

    @FXML
    private ZoomableScrollPane zoomableScrollPane;

    @FXML
    private void initialize() {
        setEntityImagePaths();
        setCreatureImagePaths();

        simulation = new Simulation();

        try {
            fillAreaGridCells(simulation.getArea());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        zoomableScrollPane.setZoomableTarget(areaGrid);
    }

    private void setEntityImagePaths() {
        entityPathsToImage.put(Ground.class, "/main/ui/images/landscape/ground.png");
        entityPathsToImage.put(Water.class, "/main/ui/images/landscape/water.png");
        entityPathsToImage.put(Tree.class, "/main/ui/images/landscape/tree.png");
        entityPathsToImage.put(Rock.class, "/main/ui/images/landscape/rock.png");
        entityPathsToImage.put(Grass.class, "/main/ui/images/landscape/grass.png");
        entityPathsToImage.put(Meat.class, "/main/ui/images/meat.png");
    }

    private void setCreatureImagePaths() {
        // predators
        creaturePathsToImage.put(Lion.class, getCreatureImagePaths(Lion.class));
        creaturePathsToImage.put(Crocodile.class, getCreatureImagePaths(Crocodile.class));
        creaturePathsToImage.put(Panther.class, getCreatureImagePaths(Panther.class));
        creaturePathsToImage.put(Tiger.class, getCreatureImagePaths(Tiger.class));

        //herbivores
        creaturePathsToImage.put(Elephant.class, getCreatureImagePaths(Elephant.class));
        creaturePathsToImage.put(Girafee.class, getCreatureImagePaths(Girafee.class));
        creaturePathsToImage.put(Ostrich.class, getCreatureImagePaths(Ostrich.class));
        creaturePathsToImage.put(Rhino.class, getCreatureImagePaths(Rhino.class));
    }

    private HashMap<CreatureViewDirection, String> getCreatureImagePaths(Class<? extends Creature> creatureType) {
        HashMap<CreatureViewDirection, String> creatureImagePaths = new HashMap<>();

        String[] pathToImages = new String[2];

        // predators paths

        if (creatureType == Lion.class) {
            pathToImages[0] = "/main/ui/images/creatures/predators/lion/lion_left.png";
            pathToImages[1] = "/main/ui/images/creatures/predators/lion/lion_right.png";
        }
        if (creatureType == Crocodile.class) {
            pathToImages[0] = "/main/ui/images/creatures/predators/crocodile/crocodile_left.png";
            pathToImages[1] = "/main/ui/images/creatures/predators/crocodile/crocodile_right.png";
        }
        if (creatureType == Panther.class) {
            pathToImages[0] = "/main/ui/images/creatures/predators/panther/panther_left.png";
            pathToImages[1] = "/main/ui/images/creatures/predators/panther/panther_right.png";
        }
        if (creatureType == Tiger.class) {
            pathToImages[0] = "/main/ui/images/creatures/predators/tiger/tiger_left.png";
            pathToImages[1] = "/main/ui/images/creatures/predators/tiger/tiger_right.png";
        }

        // herbivores paths

        if (creatureType == Elephant.class) {
            pathToImages[0] = "/main/ui/images/creatures/herbivores/elephant/elephant_left.png";
            pathToImages[1] = "/main/ui/images/creatures/herbivores/elephant/elephant_right.png";
        }
        if (creatureType == Girafee.class) {
            pathToImages[0] = "/main/ui/images/creatures/herbivores/girafee/girafee_left.png";
            pathToImages[1] = "/main/ui/images/creatures/herbivores/girafee/girafee_right.png";
        }
        if (creatureType == Ostrich.class) {
            pathToImages[0] = "/main/ui/images/creatures/herbivores/ostrich/ostrich_left.png";
            pathToImages[1] = "/main/ui/images/creatures/herbivores/ostrich/ostrich_right.png";
        }
        if (creatureType == Rhino.class) {
            pathToImages[0] = "/main/ui/images/creatures/herbivores/rhino/rhino_left.png";
            pathToImages[1] = "/main/ui/images/creatures/herbivores/rhino/rhino_right.png";
        }

        creatureImagePaths.put(CreatureViewDirection.LEFT, pathToImages[0]);
        creatureImagePaths.put(CreatureViewDirection.RIGHT, pathToImages[1]);

        return creatureImagePaths;
    }

    @FXML
    public void fillAreaGridCells(Area area) throws FileNotFoundException {
        for (int row = 0; row < area.size; row++) {
            areaGrid.getRowConstraints().add(new RowConstraints());
            for (int column = 0; column < area.size; column++) {
                areaGrid.getColumnConstraints().add(new ColumnConstraints());

                Entity entity = area.entities.get(new Coordinates(row, column));

                String pathToImage;

                // ground rendering implements in style.css as background
                if (entity instanceof Ground) {
                    continue;
                }

                if (entity instanceof Creature creature) {
                    HashMap<CreatureViewDirection, String> imagePaths = creaturePathsToImage.get(entity.getClass());
                    pathToImage = imagePaths.get(creature.viewDirection);
                } else {
                    pathToImage = entityPathsToImage.get(entity.getClass());
                }

                ImageView entityImage = new ImageView(new Image(Main.class.getResourceAsStream(pathToImage)));

                areaGrid.add(entityImage, column, row);
            }
        }
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