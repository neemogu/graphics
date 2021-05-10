package ru.nsu.cg.controllers;

import javafx.stage.Stage;
import ru.nsu.cg.model.WireframeModel;

public interface DialogController {
    void setModel(WireframeModel model);
    void setWindow(Stage window);
}
