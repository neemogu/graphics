package init;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import ru.nsu.cg.Filters;
import ru.nsu.cg.FiltersModel;
import ru.nsu.cg.controllers.MainController;

import java.io.IOException;

public class FiltersApplication extends Application {
    private static Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    @Override
    public void start(Stage primaryStage) {
        FiltersApplication.primaryStage = primaryStage;
        primaryStage.centerOnScreen();
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setTitle("ICGFilter");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/icons/icon.png")));
        primaryStage.centerOnScreen();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            Parent root = loader.load();
            MainController controller = loader.getController();
            FiltersModel model = new FiltersModel();
            controller.setModel(model);
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (IOException e) {
            System.out.println("Can't start an application...");
            System.exit(1);
        }
    }
}
