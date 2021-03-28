package ru.nsu.cg.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class MessageWindowController {
    @FXML
    private Label message;
    @FXML
    private Button okButton;

    public void setMessage(String errorMessage) {
        this.message.setText(errorMessage);
    }

    public void setWindow(Stage stage) {
        okButton.setOnAction((e) -> stage.close());
    }
}
