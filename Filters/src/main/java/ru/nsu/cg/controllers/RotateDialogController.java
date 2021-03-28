package ru.nsu.cg.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import ru.nsu.cg.FiltersModel;
import ru.nsu.cg.ImageTransforms;
import ru.nsu.cg.Subscriber;
import ru.nsu.cg.gui.ErrorWindow;

import java.net.URL;
import java.util.ResourceBundle;

public class RotateDialogController implements DialogController, Initializable, Subscriber {
    private FiltersModel model;
    private Stage window;

    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Slider slider;
    @FXML
    private TextField textField;

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
        slider.minProperty().setValue(ImageTransforms.minAngle);
        slider.maxProperty().setValue(ImageTransforms.maxAngle);
        textField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
        slider.valueProperty().addListener((obs, ov, nv) -> {
            if (nv.intValue() != ov.intValue()) {
                model.getSettings().setRotationAngle(nv.intValue());
            }
        });
        EventHandler<ActionEvent> textFieldListener = (e) -> {
            int parsedValue;
            try {
                parsedValue = Integer.parseInt(textField.getText());
                if (parsedValue >= ImageTransforms.minAngle && parsedValue <= ImageTransforms.maxAngle) {
                    model.getSettings().setRotationAngle(parsedValue);
                } else {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException exception) {
                Platform.runLater(() -> new ErrorWindow("Angle must be between " +
                        ImageTransforms.minAngle + " and " + ImageTransforms.maxAngle));
                update();
            }
        };
        textField.setOnAction(textFieldListener);
        textField.focusedProperty().addListener((obs, ov, nv) -> {
            if (!nv) {
                textFieldListener.handle(null);
            }
        });
        okButton.setOnAction((e) -> {
            model.addRotation();
            model.applyChanges();
            window.close();
        });
        cancelButton.setOnAction((e) -> {
            model.getSettings().recoverSavedValues();
            window.close();
        });
    }

    @Override
    public void update() {
        slider.valueProperty().setValue(model.getSettings().getRotationAngle());
        textField.setText(Integer.toString(model.getSettings().getRotationAngle()));
    }
}

