package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.area.Area;
import main.area.AreaGenerator;

import java.io.IOException;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        AreaGenerator areaGenerator = new AreaGenerator(new Area(20));
        Area area = areaGenerator.generateArea();

        Simulation simulation = new Simulation(area, areaGenerator);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/main/ui/layouts/Main.fxml"));

        Scene scene = new Scene(fxmlLoader.load());

        MainController controller = fxmlLoader.getController();
        controller.setSimulation(simulation);
        controller.fillAreaGridCells();
        controller.setUpZoomableScrollPane();

        stage.setScene(scene);
        stage.setTitle("Simulation");
        stage.setWidth(1200);
        stage.setHeight(800);

        stage.show();
    }
}
