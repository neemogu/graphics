package init;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import resources.ResourceLoader;
import ru.nsu.cg.PaintController;
import ru.nsu.cg.PaintModel;
import ru.nsu.cg.gui.PaintView;

public class PaintApplication extends Application {
    private PaintModel model = null;
    private PaintView view = null;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.centerOnScreen();
        primaryStage.setMinWidth(640);
        primaryStage.setMinHeight(480);
        primaryStage.setTitle("ICGPaint");
        primaryStage.getIcons().add(new Image(ResourceLoader.class.getResourceAsStream("icon.png")));

        model = new PaintModel();
        view = new PaintView(primaryStage, model);
        PaintController.linkModelAndView(model, view);

        primaryStage.show();
    }
}
