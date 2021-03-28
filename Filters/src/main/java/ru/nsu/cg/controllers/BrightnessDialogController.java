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

import static ru.nsu.cg.FiltersSettings.Filter.BRIGHTNESS;

public class BrightnessDialogController implements DialogController, Initializable, Subscriber {
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
        int minValue = (int)(Filters.minBrightness * 100);
        int maxValue = (int)(Filters.maxBrightness * 100);
        slider.minProperty().setValue(minValue);
        slider.maxProperty().setValue(maxValue);
        textField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
        slider.valueProperty().addListener((obs, ov, nv) -> {
            if (nv.intValue() != ov.intValue()) {
                model.getSettings().setBrightness(nv.intValue());
            }
        });
        EventHandler<ActionEvent> textFieldListener = (e) -> {
            int parsedValue;
            try {
                parsedValue = Integer.parseInt(textField.getText());
                if (parsedValue >= minValue && parsedValue <= maxValue) {
                    model.getSettings().setBrightness(parsedValue);
                } else {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException exception) {
                Platform.runLater(() -> new ErrorWindow("Brightness value must be between " +
                        Filters.minBrightness + " and " + Filters.maxBrightness));
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
            WritableImage imageToFilter = model.getImageToFilter();
            Filters.brightness(imageToFilter, model.getSettings().getBrightness() / 100.0);
            model.applyChanges();
            model.getSettings().setLastFilter(BRIGHTNESS);
            window.close();
        });
        cancelButton.setOnAction((e) -> {
            model.getSettings().recoverSavedValues();
            window.close();
        });
    }

    @Override
    public void update() {
        slider.valueProperty().setValue(model.getSettings().getBrightness());
        textField.setText(Integer.toString(model.getSettings().getBrightness()));
    }
}
