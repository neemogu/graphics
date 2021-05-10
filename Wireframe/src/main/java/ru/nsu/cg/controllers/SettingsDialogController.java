package ru.nsu.cg.controllers;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import ru.nsu.cg.Subscriber;
import ru.nsu.cg.gui.ErrorWindow;
import ru.nsu.cg.model.SettingsModel;
import ru.nsu.cg.model.WireframeModel;

import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class SettingsDialogController implements DialogController, Initializable, Subscriber {
    private static long lastTime = System.nanoTime();
    private static final long minMouseDeltaTime = 15000000;
    private static final long minResizeDeltaTime = 1000000;

    @FXML
    private Pane resizePane;
    @FXML
    private ImageView curveWindow;
    @FXML
    private TextField mTextField;
    @FXML
    private TextField kTextField;
    @FXML
    private TextField nTextField;
    @FXML
    private TextField tnTextField;
    @FXML
    private TextField redTextField;
    @FXML
    private TextField greenTextField;
    @FXML
    private TextField blueTextField;
    @FXML
    private Button addButton;
    @FXML
    private Button removeButton;
    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;

    private WireframeModel model;
    private SettingsModel settingsModel;
    private Stage window;

    @Override
    public void setModel(WireframeModel model) {
        this.model = model;
        settingsModel = model.getSettingsModel();
        settingsModel.addSubscriber(this);
        settingsModel.getCurveSettings().addSubscriber(this);
        settingsModel.getCurve().addSubscriber(this);
        settingsModel.getCurveSettings().save();
        settingsModel.getCurve().save();
        setTextFieldsListeners();
        settingsModel.updateCurveImage();
    }

    @Override
    public void setWindow(Stage window) {
        this.window = window;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cancelButton.setOnAction((e) -> {
            settingsModel.getCurveSettings().recover();
            settingsModel.getCurve().recover();
            window.close();
        });
        okButton.setOnAction((e) -> {
            model.updateScreen();
            window.close();
        });
        resizePane.widthProperty().addListener((obs, ov, nv) -> {
            Platform.runLater(() -> {
                settingsModel.resizeCurveImage((int)nv.doubleValue(), settingsModel.getCurveImageHeight());
            });
        });
        resizePane.heightProperty().addListener((obs, ov, nv) -> {
            long currentTime = System.nanoTime();
            if (currentTime - lastTime < minResizeDeltaTime) {
                return;
            }
            lastTime = currentTime;
            Platform.runLater(() -> {
                settingsModel.resizeCurveImage(settingsModel.getCurveImageWidth(), (int)nv.doubleValue());
            });
        });
        curveWindow.setOnMousePressed((e) -> {
            float width = (float)curveWindow.getImage().getWidth();
            float height = (float)curveWindow.getImage().getHeight();
            settingsModel.getCurve().setSelectedPoint(
                    (float)e.getX() / (width - 1),
                    (float)e.getY() / (height - 1)
            );
            settingsModel.updateCurveImage();
        });

        curveWindow.setOnMouseDragged((e) -> {
            long currentTime = System.nanoTime();
            if (currentTime - lastTime < minMouseDeltaTime) {
                return;
            }
            float width = (float)curveWindow.getImage().getWidth();
            float height = (float)curveWindow.getImage().getHeight();
            settingsModel.getCurve().movePoint(
                    (float)e.getX() / (width - 1),
                    (float)e.getY() / (height - 1)
                    );
            settingsModel.updateCurveImage();
        });

        addButton.setOnAction((e) -> {
            settingsModel.getCurve().addPoint();
            settingsModel.updateCurveImage();
        });
        removeButton.setOnAction((e) -> {
            settingsModel.getCurve().removePoint();
            settingsModel.updateCurveImage();
        });
    }

    private void setTextFieldsListeners() {
        nTextField.setOnAction
                (getTextFieldActionListener(nTextField, "n", settingsModel.getCurveSettings()::setN));
        nTextField.focusedProperty().addListener
                (getTextFieldFocusedListener(nTextField, "n", settingsModel.getCurveSettings()::setN));

        mTextField.setOnAction
                (getTextFieldActionListener(mTextField, "m", settingsModel.getCurveSettings()::setM));
        mTextField.focusedProperty().addListener
                (getTextFieldFocusedListener(mTextField, "m", settingsModel.getCurveSettings()::setM));

        kTextField.setOnAction
                (getTextFieldActionListener(kTextField, "k", settingsModel.getCurveSettings()::setK));
        kTextField.focusedProperty().addListener
                (getTextFieldFocusedListener(kTextField, "k", settingsModel.getCurveSettings()::setK));

        tnTextField.setOnAction
                (getTextFieldActionListener(tnTextField, "tN", settingsModel.getCurveSettings()::settN));
        tnTextField.focusedProperty().addListener
                (getTextFieldFocusedListener(tnTextField, "tN", settingsModel.getCurveSettings()::settN));

        redTextField.setOnAction
                (getTextFieldActionListener(redTextField, "R", settingsModel.getCurveSettings()::setR));
        redTextField.focusedProperty().addListener
                (getTextFieldFocusedListener(redTextField, "R", settingsModel.getCurveSettings()::setR));

        greenTextField.setOnAction
                (getTextFieldActionListener(greenTextField, "G", settingsModel.getCurveSettings()::setG));
        greenTextField.focusedProperty().addListener
                (getTextFieldFocusedListener(greenTextField, "G", settingsModel.getCurveSettings()::setG));

        blueTextField.setOnAction
                (getTextFieldActionListener(blueTextField, "B", settingsModel.getCurveSettings()::setB));
        blueTextField.focusedProperty().addListener
                (getTextFieldFocusedListener(blueTextField, "B", settingsModel.getCurveSettings()::setB));
    }

    private EventHandler<ActionEvent> getTextFieldActionListener(TextField textField, String textFieldName,
                                                                 Consumer<Integer> setter) {
        return (e) -> {
            setValueFromTextField(textField, textFieldName, setter);
        };
    }

    private ChangeListener<Boolean> getTextFieldFocusedListener(TextField textField, String textFieldName,
                                                                Consumer<Integer> setter) {
        return (obs, ov, nv) -> {
            if (!nv) {
                setValueFromTextField(textField, textFieldName, setter);
            }
        };
    }

    private void setValueFromTextField(TextField textField, String textFieldName, Consumer<Integer> setter) {
        try {
            int value = Integer.parseInt(textField.getText());
            setter.accept(value);
            settingsModel.updateCurveImage();
        } catch (NumberFormatException exc) {
            Platform.runLater(() -> new ErrorWindow("'" + textFieldName + "' invalid value"));
            update();
        } catch (IllegalArgumentException exc) {
            Platform.runLater(() -> new ErrorWindow(exc.getMessage()));
            update();
        }
    }

    @Override
    public void update() {
        curveWindow.setImage(settingsModel.getCurveImage());
        if (!mTextField.getText().equals("" + settingsModel.getCurveSettings().getM())) {
            mTextField.setText("" + settingsModel.getCurveSettings().getM());
        }
        if (!nTextField.getText().equals("" + settingsModel.getCurveSettings().getN())) {
            nTextField.setText("" + settingsModel.getCurveSettings().getN());
        }
        if (!kTextField.getText().equals("" + settingsModel.getCurveSettings().getK())) {
            kTextField.setText("" + settingsModel.getCurveSettings().getK());
        }
        if (!tnTextField.getText().equals("" + settingsModel.getCurveSettings().getTN())) {
            tnTextField.setText("" + settingsModel.getCurveSettings().getTN());
        }
        if (!redTextField.getText().equals("" + settingsModel.getCurveSettings().getR())) {
            redTextField.setText("" + settingsModel.getCurveSettings().getR());
        }
        if (!greenTextField.getText().equals("" + settingsModel.getCurveSettings().getG())) {
            greenTextField.setText("" + settingsModel.getCurveSettings().getG());
        }
        if (!blueTextField.getText().equals("" + settingsModel.getCurveSettings().getB())) {
            blueTextField.setText("" + settingsModel.getCurveSettings().getB());
        }
    }
}
