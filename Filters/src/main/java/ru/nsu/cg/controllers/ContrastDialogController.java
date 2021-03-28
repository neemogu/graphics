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
import javafx.util.converter.DoubleStringConverter;
import ru.nsu.cg.Filters;
import ru.nsu.cg.FiltersModel;
import ru.nsu.cg.Subscriber;
import ru.nsu.cg.gui.ErrorWindow;

import java.net.URL;
import java.util.ResourceBundle;

import static ru.nsu.cg.FiltersSettings.Filter.CONTRAST;

public class ContrastDialogController implements DialogController, Initializable, Subscriber {
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
        slider.minProperty().setValue(Filters.minContrast);
        slider.maxProperty().setValue(Filters.maxContrast);
        textField.setTextFormatter(new TextFormatter<>(new DoubleStringConverter()));
        slider.valueProperty().addListener((obs, ov, nv) -> {
            if (ov.doubleValue() != nv.doubleValue()) {
                model.getSettings().setContrast(nv.doubleValue());
            }
        });
        EventHandler<ActionEvent> textFieldListener = (e) -> {
            double parsedValue;
            try {
                parsedValue = Double.parseDouble(textField.getText());
                if (parsedValue >= Filters.minContrast && parsedValue <= Filters.maxContrast) {
                    model.getSettings().setContrast(parsedValue);
                } else {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException exception) {
                Platform.runLater(() -> new ErrorWindow("Contrast value must be between " +
                        Filters.minContrast + " and " + Filters.maxContrast));
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
            Filters.contrast(imageToFilter, model.getSettings().getContrast());
            model.applyChanges();
            model.getSettings().setLastFilter(CONTRAST);
            window.close();
        });
        cancelButton.setOnAction((e) -> {
            model.getSettings().recoverSavedValues();
            window.close();
        });
    }

    @Override
    public void update() {
        slider.valueProperty().setValue(model.getSettings().getContrast());
        textField.setText(Double.toString(model.getSettings().getContrast()));
    }
}
