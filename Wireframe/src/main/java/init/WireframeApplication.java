package init;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import ru.nsu.cg.model.WireframeModel;
import ru.nsu.cg.controllers.MainController;

import java.io.IOException;
import java.util.Objects;

public class WireframeApplication extends Application {
    private static Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    @Override
    public void start(Stage primaryStage) {
        WireframeApplication.primaryStage = primaryStage;
        primaryStage.centerOnScreen();
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setTitle("ICGWireframe");
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/icon.png"))));
        primaryStage.centerOnScreen();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            Parent root = loader.load();
            MainController controller = loader.getController();
            WireframeModel model = new WireframeModel();
            controller.setModel(model);
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (IOException e) {
            System.out.println("Can't start an application...");
            System.exit(1);
        }
    }
}
