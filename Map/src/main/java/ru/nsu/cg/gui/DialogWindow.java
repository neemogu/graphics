package ru.nsu.cg.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ru.nsu.cg.model.MapModel;
import ru.nsu.cg.controllers.DialogController;

import java.io.IOException;

public class DialogWindow extends Stage {
    public DialogWindow(String fxmlPath, MapModel model) {
        super();
        setTitle("Select options");
        initStyle(StageStyle.UTILITY);
        centerOnScreen();
        initModality(Modality.APPLICATION_MODAL);
        setMinWidth(400);
        setMinHeight(300);
        setMaxWidth(500);
        setMaxHeight(600);
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        try {
            Parent root = loader.load();
            Scene scene = new Scene(root);
            setScene(scene);
        } catch (IOException e) {
            setScene(new Scene(new Label("Error")));
            show();
            return;
        }
        DialogController controller = loader.getController();
        controller.setModel(model);
        controller.setWindow(this);
        show();
    }
}
