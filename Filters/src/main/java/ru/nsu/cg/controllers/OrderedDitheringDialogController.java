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

import static ru.nsu.cg.FiltersSettings.Filter.OD;

public class OrderedDitheringDialogController implements DialogController, Initializable, Subscriber {
    private FiltersModel model;
    private Stage window;

    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Slider redQuantitySlider;
    @FXML
    private TextField redQuantityTextField;
    @FXML
    private Slider greenQuantitySlider;
    @FXML
    private TextField greenQuantityTextField;
    @FXML
    private Slider blueQuantitySlider;
    @FXML
    private TextField blueQuantityTextField;

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
        redQuantitySlider.minProperty().setValue(Filters.minColorQuantity);
        redQuantitySlider.maxProperty().setValue(Filters.maxColorQuantity);
        greenQuantitySlider.minProperty().setValue(Filters.minColorQuantity);
        greenQuantitySlider.maxProperty().setValue(Filters.maxColorQuantity);
        blueQuantitySlider.minProperty().setValue(Filters.minColorQuantity);
        blueQuantitySlider.maxProperty().setValue(Filters.maxColorQuantity);
        redQuantityTextField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
        greenQuantityTextField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
        blueQuantityTextField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
        redQuantitySlider.valueProperty().addListener((obs, ov, nv) -> {
            if (ov.intValue() != nv.intValue()) {
                model.getSettings().setOrderedRedQuantity(nv.intValue());
            }
        });
        greenQuantitySlider.valueProperty().addListener((obs, ov, nv) -> {
            if (ov.intValue() != nv.intValue()) {
                model.getSettings().setOrderedGreenQuantity(nv.intValue());
            }
        });
        blueQuantitySlider.valueProperty().addListener((obs, ov, nv) -> {
            if (ov.intValue() != nv.intValue()) {
                model.getSettings().setOrderedBlueQuantity(nv.intValue());
            }
        });

        EventHandler<ActionEvent> textFieldListener = (e) -> {
            try {
                int redQuantityParsedValue = Integer.parseInt(redQuantityTextField.getText());
                int greenQuantityParsedValue = Integer.parseInt(greenQuantityTextField.getText());
                int blueQuantityParsedValue = Integer.parseInt(blueQuantityTextField.getText());
                if (redQuantityParsedValue >= Filters.minColorQuantity && redQuantityParsedValue <= Filters.maxColorQuantity &&
                        greenQuantityParsedValue >= Filters.minColorQuantity && greenQuantityParsedValue <= Filters.maxColorQuantity &&
                        blueQuantityParsedValue >= Filters.minColorQuantity && blueQuantityParsedValue <= Filters.maxColorQuantity) {
                    model.getSettings().setOrderedRedQuantity(redQuantityParsedValue);
                    model.getSettings().setOrderedGreenQuantity(greenQuantityParsedValue);
                    model.getSettings().setOrderedBlueQuantity(blueQuantityParsedValue);
                } else {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException exception) {
                Platform.runLater(() -> new ErrorWindow("Color component quantity must be between " +
                        Filters.minColorQuantity + " and " + Filters.maxColorQuantity));
                update();
            }
        };

        redQuantityTextField.setOnAction(textFieldListener);
        redQuantityTextField.focusedProperty().addListener((obs, ov, nv) -> {
            if (!nv) {
                textFieldListener.handle(null);
            }
        });
        greenQuantityTextField.setOnAction(textFieldListener);
        greenQuantityTextField.focusedProperty().addListener((obs, ov, nv) -> {
            if (!nv) {
                textFieldListener.handle(null);
            }
        });
        blueQuantityTextField.setOnAction(textFieldListener);
        blueQuantityTextField.focusedProperty().addListener((obs, ov, nv) -> {
            if (!nv) {
                textFieldListener.handle(null);
            }
        });

        okButton.setOnAction((e) -> {
            WritableImage imageToFilter = model.getImageToFilter();
            Filters.orderedDithering(imageToFilter, model.getSettings().getOrderedRedQuantity(),
                    model.getSettings().getOrderedGreenQuantity(), model.getSettings().getOrderedBlueQuantity());
            model.applyChanges();
            model.getSettings().setLastFilter(OD);
            window.close();
        });
        cancelButton.setOnAction((e) -> {
            model.getSettings().recoverSavedValues();
            window.close();
        });
    }

    @Override
    public void update() {
        redQuantitySlider.valueProperty().setValue(model.getSettings().getOrderedRedQuantity());
        redQuantityTextField.setText(Integer.toString(model.getSettings().getOrderedRedQuantity()));
        greenQuantitySlider.valueProperty().setValue(model.getSettings().getOrderedGreenQuantity());
        greenQuantityTextField.setText(Integer.toString(model.getSettings().getOrderedGreenQuantity()));
        blueQuantitySlider.valueProperty().setValue(model.getSettings().getOrderedBlueQuantity());
        blueQuantityTextField.setText(Integer.toString(model.getSettings().getOrderedBlueQuantity()));
    }
}

