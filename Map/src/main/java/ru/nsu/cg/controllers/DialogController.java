package ru.nsu.cg.controllers;

import javafx.stage.Stage;
import ru.nsu.cg.model.MapModel;

public interface DialogController {
    void setModel(MapModel model);
    void setWindow(Stage window);
}
