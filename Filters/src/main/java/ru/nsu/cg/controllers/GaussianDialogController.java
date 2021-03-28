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

import static ru.nsu.cg.FiltersSettings.Filter.GAUSSIAN;

public class GaussianDialogController implements DialogController, Initializable, Subscriber {
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
        slider.minProperty().setValue(Filters.minGaussianSize);
        slider.maxProperty().setValue(Filters.maxGaussianSize);
        slider.blockIncrementProperty().setValue(2);
        textField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
        slider.valueProperty().addListener((obs, ov, nv) -> {
            if (ov.intValue() != nv.intValue()) {
                model.getSettings().setGaussianSize(nv.intValue());
            }
        });
        EventHandler<ActionEvent> textFieldListener = (e) -> {
            int parsedValue;
            try {
                parsedValue = Integer.parseInt(textField.getText());
                if (parsedValue >= Filters.minGaussianSize && parsedValue <= Filters.maxGaussianSize && parsedValue % 2 != 0) {
                    model.getSettings().setGaussianSize(parsedValue);
                } else {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException exception) {
                Platform.runLater(() -> new ErrorWindow("Gaussian filter size value must be odd and between " +
                        Filters.minGaussianSize + " and " + Filters.maxGaussianSize));
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
            Filters.gaussian(imageToFilter, model.getSettings().getGaussianSize());
            model.applyChanges();
            model.getSettings().setLastFilter(GAUSSIAN);
            window.close();
        });
        cancelButton.setOnAction((e) -> {
            model.getSettings().recoverSavedValues();
            window.close();
        });
    }

    @Override
    public void update() {
        slider.valueProperty().setValue(model.getSettings().getGaussianSize());
        textField.setText(Integer.toString(model.getSettings().getGaussianSize()));
    }
}

