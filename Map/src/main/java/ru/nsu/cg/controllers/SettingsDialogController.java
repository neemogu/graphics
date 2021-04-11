package ru.nsu.cg.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import ru.nsu.cg.Subscriber;
import ru.nsu.cg.gui.ErrorWindow;
import ru.nsu.cg.model.MapModel;
import ru.nsu.cg.model.PropertyReader;

import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;
import java.util.ResourceBundle;

public class SettingsDialogController implements DialogController, Initializable, Subscriber {
    private MapModel model;
    private Stage primaryWindow;

    private PropertyReader propertyReader;

    @FXML
    private TextField startXTextField;
    @FXML
    private TextField endXTextField;
    @FXML
    private TextField startYTextField;
    @FXML
    private TextField endYTextField;
    @FXML
    private TextField gridWidthTextField;
    @FXML
    private TextField gridHeightTextField;
    @FXML
    private TextField isolinesCountTextField;
    @FXML
    private ColorPicker isolineColorPicker;
    @FXML
    private VBox displayColorsVBox;
    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;

    private final ArrayList<ColorPicker> colorPickers = new ArrayList<>();

    @Override
    public void setModel(MapModel model) {
        this.model = model;
        this.propertyReader = new PropertyReader(model);
        model.getSettings().saveSettings();
        model.getFunction().saveParameters();
        model.getSettings().addSubscriber(this);
        update();
    }

    @Override
    public void setWindow(Stage window) {
        this.primaryWindow = window;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cancelButton.setOnAction((e) -> {
            model.getFunction().recoverParameters();
            model.getSettings().recoverSettings();
            primaryWindow.close();
        });
        okButton.setOnAction((e) -> {
            Properties properties = new Properties();
            properties.setProperty("startX", startXTextField.getText());
            properties.setProperty("endX", endXTextField.getText());
            properties.setProperty("startY", startYTextField.getText());
            properties.setProperty("endY", endYTextField.getText());
            properties.setProperty("gridWidth", gridWidthTextField.getText());
            properties.setProperty("gridHeight", gridHeightTextField.getText());
            properties.setProperty("isolinesCount", isolinesCountTextField.getText());
            properties.setProperty("Rz", "" + (int)(isolineColorPicker.getValue().getRed() * 255));
            properties.setProperty("Gz", "" + (int)(isolineColorPicker.getValue().getGreen() * 255));
            properties.setProperty("Bz", "" + (int)(isolineColorPicker.getValue().getBlue() * 255));
            for (int i = 0; i < colorPickers.size(); ++i) {
                properties.setProperty("R" + i, "" + (int)(colorPickers.get(i).getValue().getRed() * 255));
                properties.setProperty("G" + i, "" + (int)(colorPickers.get(i).getValue().getGreen() * 255));
                properties.setProperty("B" + i, "" + (int)(colorPickers.get(i).getValue().getBlue() * 255));
            }
            try {
                propertyReader.setProperties(properties);
                primaryWindow.close();
            } catch (PropertyReader.PropertyException exc) {
                model.getFunction().recoverParameters();
                model.getSettings().recoverSettings();
                Platform.runLater(() -> new ErrorWindow(exc.getMessage()));
            }
        });
        EventHandler<ActionEvent> isolinesCountAction = (e) -> {
            String isolinesCountStr = isolinesCountTextField.getText();
            try {
                int isolinesCount = Integer.parseInt(isolinesCountStr);
                model.getSettings().setIsolinesCount(isolinesCount);
            } catch (NumberFormatException exc) {
                Platform.runLater(() -> new ErrorWindow("Isolines number is in invalid format"));
            } catch (IllegalArgumentException exc) {
                Platform.runLater(() -> new ErrorWindow(exc.getMessage()));
            }
        };
        isolinesCountTextField.setOnAction(isolinesCountAction);
        isolinesCountTextField.focusedProperty().addListener((obs, ov, nv) -> {
            if (!nv) {
                isolinesCountAction.handle(new ActionEvent());
            }
        });
    }

    @Override
    public void update() {
        startXTextField.setText("" + model.getFunction().getStartX());
        endXTextField.setText("" + model.getFunction().getEndX());
        startYTextField.setText("" + model.getFunction().getStartY());
        endYTextField.setText("" + model.getFunction().getEndY());
        gridWidthTextField.setText("" + model.getFunction().getGridWidth());
        gridHeightTextField.setText("" + model.getFunction().getGridHeight());
        isolineColorPicker.setValue(model.getSettings().getIsolineColor());
        isolinesCountTextField.setText("" + model.getSettings().getIsolinesCount());
        Color[] colors = model.getSettings().getColors();
        if (colors.length != colorPickers.size()) {
            resetColorPickers();
            for (int i = 0; i < colors.length; ++i) {
                addColorPickerBox();
            }
        }
        for (int i = 0; i < colors.length; ++i) {
            colorPickers.get(i).setValue(colors[i]);
        }
    }

    private void resetColorPickers() {
        displayColorsVBox.getChildren().clear();
        colorPickers.clear();
    }

    private void addColorPickerBox() {
        HBox result = new HBox();
        result.getStyleClass().add("settingHBox");
        VBox.setMargin(result, new Insets(15, 15, 15, 15));
        Label label = new Label("R" + colorPickers.size() + ":");
        label.getStyleClass().add("propertyLabel");
        ColorPicker colorPicker = new ColorPicker();
        colorPicker.getStyleClass().add("isolineColorPicker");
        result.getChildren().addAll(label, colorPicker);
        displayColorsVBox.getChildren().add(result);
        colorPickers.add(colorPicker);
    }
}
