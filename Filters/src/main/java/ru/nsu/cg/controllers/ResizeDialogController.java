package ru.nsu.cg.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
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

public class ResizeDialogController implements DialogController, Initializable, Subscriber {
    private FiltersModel model;
    private Stage window;

    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;
    @FXML
    private TextField widthTextField;
    @FXML
    private TextField heightTextField;

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
        widthTextField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
        heightTextField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));

        setTextFieldsListeners();

        okButton.setOnAction((e) -> {
            model.setScale();
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
        widthTextField.setText(Integer.toString(model.getSettings().getResizeWidth()));
        heightTextField.setText(Integer.toString(model.getSettings().getResizeHeight()));
    }

    private void setTextFieldsListeners() {
        EventHandler<ActionEvent> widthListener = (e) -> {
            int parsedValue;
            try {
                parsedValue = Integer.parseInt(widthTextField.getText());
                if (parsedValue >= ImageTransforms.minDimensionSize && parsedValue <= ImageTransforms.maxDimensionSize) {
                    model.getSettings().setResizeWidth(parsedValue);
                } else {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException exception) {
                Platform.runLater(() -> new ErrorWindow("Width must be between " +
                        ImageTransforms.minDimensionSize + " and " + ImageTransforms.maxDimensionSize));
                update();
            }
        };
        EventHandler<ActionEvent> heightListener = (e) -> {
            int parsedValue;
            try {
                parsedValue = Integer.parseInt(heightTextField.getText());
                if (parsedValue >= ImageTransforms.minDimensionSize && parsedValue <= ImageTransforms.maxDimensionSize) {
                    model.getSettings().setResizeHeight(parsedValue);
                } else {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException exception) {
                Platform.runLater(() -> new ErrorWindow("Height must be between " +
                        ImageTransforms.minDimensionSize + " and " + ImageTransforms.maxDimensionSize));
                update();
            }
        };
        widthTextField.setOnAction(widthListener);
        widthTextField.focusedProperty().addListener((obs, ov, nv) -> {
            if (!nv) {
                widthListener.handle(null);
            }
        });
        heightTextField.setOnAction(heightListener);
        heightTextField.focusedProperty().addListener((obs, ov, nv) -> {
            if (!nv) {
                heightListener.handle(null);
            }
        });
    }
}


