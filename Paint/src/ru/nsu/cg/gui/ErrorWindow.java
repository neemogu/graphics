package ru.nsu.cg.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ru.nsu.cg.buttons.SimpleButton;

public class ErrorWindow extends Stage {
    public ErrorWindow(String message) {
        super();
        setTitle("Error");
        centerOnScreen();
        initModality(Modality.APPLICATION_MODAL);
        initStyle(StageStyle.UTILITY);
        Label msgLabel = new Label(message);
        msgLabel.setWrapText(true);
        msgLabel.setMaxWidth(200);
        msgLabel.setFont(new Font(15));
        Button okButton = new SimpleButton("OK");
        okButton.setOnAction((event) -> {
            close();
        });
        VBox root = new VBox(msgLabel, okButton);
        root.setAlignment(Pos.CENTER);
        VBox.setMargin(msgLabel, new Insets(10, 15, 10,15));
        VBox.setMargin(okButton, new Insets(10, 10, 10 ,10));
        Scene scene = new Scene(root);
        setScene(scene);
    }
}
