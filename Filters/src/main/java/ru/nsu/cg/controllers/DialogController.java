package ru.nsu.cg.controllers;

import javafx.stage.Stage;
import ru.nsu.cg.FiltersModel;

public interface DialogController {
    void setModel(FiltersModel model);
    void setWindow(Stage window);
}
