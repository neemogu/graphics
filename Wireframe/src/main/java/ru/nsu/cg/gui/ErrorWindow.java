package ru.nsu.cg.gui;

public class ErrorWindow extends InfoWindow {
    public ErrorWindow(String message) {
        super("Error", "/fxml/errorWindow.fxml", message, 200, 225);
    }
}
