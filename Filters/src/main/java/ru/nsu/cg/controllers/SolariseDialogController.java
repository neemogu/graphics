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
import ru.nsu.cg.Filters;
import ru.nsu.cg.FiltersModel;
import ru.nsu.cg.Subscriber;
import ru.nsu.cg.gui.ErrorWindow;

import java.net.URL;
import java.util.ResourceBundle;

import static ru.nsu.cg.FiltersSettings.Filter.SOLARISE;

public class SolariseDialogController implements DialogController, Initializable, Subscriber {
    private FiltersModel model;
    private Stage window;

    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Slider redThresholdSlider;
    @FXML
    private TextField redThresholdTextField;
    @FXML
    private Slider greenThresholdSlider;
    @FXML
    private TextField greenThresholdTextField;
    @FXML
    private Slider blueThresholdSlider;
    @FXML
    private TextField blueThresholdTextField;

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
        redThresholdSlider.minProperty().setValue(Filters.minColorThreshold);
        redThresholdSlider.maxProperty().setValue(Filters.maxColorThreshold);
        greenThresholdSlider.minProperty().setValue(Filters.minColorThreshold);
        greenThresholdSlider.maxProperty().setValue(Filters.maxColorThreshold);
        blueThresholdSlider.minProperty().setValue(Filters.minColorThreshold);
        blueThresholdSlider.maxProperty().setValue(Filters.maxColorThreshold);
        redThresholdTextField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
        greenThresholdTextField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
        blueThresholdTextField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
        redThresholdSlider.valueProperty().addListener((obs, ov, nv) -> {
            if (ov.intValue() != nv.intValue()) {
                model.getSettings().setSolariseRedThreshold(nv.intValue());
            }
        });
        greenThresholdSlider.valueProperty().addListener((obs, ov, nv) -> {
            if (ov.intValue() != nv.intValue()) {
                model.getSettings().setSolariseGreenThreshold(nv.intValue());
            }
        });
        blueThresholdSlider.valueProperty().addListener((obs, ov, nv) -> {
            if (ov.intValue() != nv.intValue()) {
                model.getSettings().setSolariseBlueThreshold(nv.intValue());
            }
        });

        EventHandler<ActionEvent> textFieldListener = (e) -> {
            try {
                int redThresholdParsedValue = Integer.parseInt(redThresholdTextField.getText());
                int greenThresholdParsedValue = Integer.parseInt(greenThresholdTextField.getText());
                int blueThresholdParsedValue = Integer.parseInt(blueThresholdTextField.getText());
                if (redThresholdParsedValue >= Filters.minColorThreshold && redThresholdParsedValue <= Filters.maxColorThreshold &&
                        greenThresholdParsedValue >= Filters.minColorThreshold && greenThresholdParsedValue <= Filters.maxColorThreshold &&
                        blueThresholdParsedValue >= Filters.minColorThreshold && blueThresholdParsedValue <= Filters.maxColorThreshold) {
                    model.getSettings().setSolariseRedThreshold(redThresholdParsedValue);
                    model.getSettings().setSolariseGreenThreshold(greenThresholdParsedValue);
                    model.getSettings().setSolariseBlueThreshold(blueThresholdParsedValue);
                } else {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException exception) {
                Platform.runLater(() -> new ErrorWindow("Color component threshold must be between " +
                        Filters.minColorThreshold + " and " + Filters.maxColorThreshold));
                update();
            }
        };

        redThresholdTextField.setOnAction(textFieldListener);
        redThresholdTextField.focusedProperty().addListener((obs, ov, nv) -> {
            if (!nv) {
                textFieldListener.handle(null);
            }
        });
        greenThresholdTextField.setOnAction(textFieldListener);
        greenThresholdTextField.focusedProperty().addListener((obs, ov, nv) -> {
            if (!nv) {
                textFieldListener.handle(null);
            }
        });
        blueThresholdTextField.setOnAction(textFieldListener);
        blueThresholdTextField.focusedProperty().addListener((obs, ov, nv) -> {
            if (!nv) {
                textFieldListener.handle(null);
            }
        });

        okButton.setOnAction((e) -> {
            WritableImage imageToFilter = model.getImageToFilter();
            Filters.solarise(imageToFilter, model.getSettings().getSolariseRedThreshold(),
                    model.getSettings().getSolariseGreenThreshold(), model.getSettings().getSolariseBlueThreshold());
            model.applyChanges();
            model.getSettings().setLastFilter(SOLARISE);
            window.close();
        });
        cancelButton.setOnAction((e) -> {
            model.getSettings().recoverSavedValues();
            window.close();
        });
    }

    @Override
    public void update() {
        redThresholdSlider.valueProperty().setValue(model.getSettings().getSolariseRedThreshold());
        redThresholdTextField.setText(Integer.toString(model.getSettings().getSolariseRedThreshold()));
        greenThresholdSlider.valueProperty().setValue(model.getSettings().getSolariseGreenThreshold());
        greenThresholdTextField.setText(Integer.toString(model.getSettings().getSolariseGreenThreshold()));
        blueThresholdSlider.valueProperty().setValue(model.getSettings().getSolariseBlueThreshold());
        blueThresholdTextField.setText(Integer.toString(model.getSettings().getSolariseBlueThreshold()));
    }
}

