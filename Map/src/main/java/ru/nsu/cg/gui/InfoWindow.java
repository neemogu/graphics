package ru.nsu.cg.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ru.nsu.cg.controllers.MessageWindowController;

import java.io.IOException;

public class InfoWindow extends Stage {
    protected InfoWindow(String title, String fxmlPath, String text, int minHeight, int minWidth) {
        super();
        setTitle(title);
        initStyle(StageStyle.UTILITY);
        centerOnScreen();
        initModality(Modality.APPLICATION_MODAL);
        setMinWidth(minWidth);
        setMinHeight(minHeight);
        setMaxWidth(500);
        setMaxHeight(500);
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
        MessageWindowController controller = loader.getController();
        controller.setMessage(text);
        controller.setWindow(this);
        show();
    }
}
