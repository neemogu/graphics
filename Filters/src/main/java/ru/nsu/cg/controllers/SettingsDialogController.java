package ru.nsu.cg.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import ru.nsu.cg.FiltersModel;
import ru.nsu.cg.FiltersSettings;
import ru.nsu.cg.Subscriber;

import java.net.URL;
import java.util.ResourceBundle;

public class SettingsDialogController implements DialogController, Initializable, Subscriber {
    private FiltersModel model;
    private Stage window;

    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;
    @FXML
    private RadioButton bilinearButton;
    @FXML
    private RadioButton nearestButton;
    @FXML
    private ToggleGroup lerpTypeGroup;

    @Override
    public void setModel(FiltersModel model) {
        this.model = model;
        model.getSettings().addSubscriber(this);
        model.getSettings().saveValues();
        update();
    }

    @Override
    public void setWindow(Stage window) {
        this.window = window;
        window.setOnCloseRequest((e) -> model.getSettings().unsubscribe(this));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        bilinearButton.setOnAction((e) -> model.getSettings().setInterpType(FiltersSettings.InterpType.BILINEAR));
        nearestButton.setOnAction((e) -> model.getSettings().setInterpType(FiltersSettings.InterpType.NEAREST));

        okButton.setOnAction((e) -> {
            window.close();
        });
        cancelButton.setOnAction((e) -> {
            model.getSettings().recoverSavedValues();
            window.close();
        });
    }

    @Override
    public void update() {
        switch (model.getSettings().getInterpType()) {
            case BILINEAR:
                lerpTypeGroup.selectToggle(bilinearButton);
                break;
            case NEAREST:
                lerpTypeGroup.selectToggle(nearestButton);
                break;
        }
    }
}

