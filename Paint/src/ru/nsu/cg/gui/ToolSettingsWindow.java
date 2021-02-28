package ru.nsu.cg.gui;

import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.converter.IntegerStringConverter;
import ru.nsu.cg.PaintModel;
import ru.nsu.cg.PaintSettings;
import ru.nsu.cg.Subscriber;
import ru.nsu.cg.buttons.SimpleButton;

public class ToolSettingsWindow extends Stage implements Subscriber {
    private final PaintModel model;
    private final TextField areaWidthTextField;
    private final TextField areaHeightTextField;
    private final TextField lineThicknessTextField;
    private final Slider lineThicknessSlider;
    private final TextField stampVertexNumTextField;
    private final Slider stampVertexNumSlider;
    private final TextField stampSizeTextField;
    private final Slider stampSizeSlider;
    private final TextField stampRotationTextField;
    private final Slider stampRotationSlider;
    private final CheckBox isStarCheckbox;
    private final Button okButton;
    private final Button cancelButton;


    public ToolSettingsWindow(PaintModel model) {
        super();
        this.model = model;
        setTitle("Settings");
        centerOnScreen();
        initModality(Modality.APPLICATION_MODAL);
        initStyle(StageStyle.UTILITY);

        areaWidthTextField = new TextField();
        areaWidthTextField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
        areaHeightTextField = new TextField();
        areaHeightTextField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
        lineThicknessTextField = new TextField();
        lineThicknessTextField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
        lineThicknessSlider = new Slider(1, PaintSettings.maxThickness, model.getSettings().getLineThickness());
        lineThicknessSlider.setShowTickMarks(true);
        lineThicknessSlider.setMajorTickUnit(5);
        lineThicknessSlider.setMinorTickCount(4);
        lineThicknessSlider.setPrefWidth(200);
        stampVertexNumTextField = new TextField();
        stampVertexNumTextField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
        stampVertexNumSlider = new Slider(PaintSettings.minStampVertexNum, PaintSettings.maxStampVertexNum,
                model.getSettings().getStampVertexNum());
        stampVertexNumSlider.setShowTickMarks(true);
        stampVertexNumSlider.setMajorTickUnit(1);
        stampVertexNumSlider.setMinorTickCount(0);
        stampVertexNumSlider.setPrefWidth(200);
        stampSizeTextField = new TextField();
        stampSizeTextField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
        stampSizeSlider = new Slider(PaintSettings.minStampSize, PaintSettings.maxStampSize, model.getSettings().getStampSize());
        stampSizeSlider.setShowTickMarks(true);
        stampSizeSlider.setMajorTickUnit(50);
        stampSizeSlider.setMinorTickCount(10);
        stampSizeSlider.setPrefWidth(200);
        stampRotationTextField = new TextField();
        stampRotationTextField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
        stampRotationSlider = new Slider(0, 359, model.getSettings().getStampRotation());
        stampRotationSlider.setShowTickMarks(true);
        stampRotationSlider.setMajorTickUnit(45);
        stampRotationSlider.setMinorTickCount(1);
        stampRotationSlider.setPrefWidth(200);
        isStarCheckbox = new CheckBox();
        okButton = new SimpleButton("OK");
        cancelButton = new SimpleButton("Cancel");

        SettingLabel isStarLabel = new SettingLabel("Star stamp", 12);
        SettingLabel widthLabel = new SettingLabel("Width", 12);
        SettingLabel heightLabel = new SettingLabel("Height", 12);

        TwoNodeHBox.setMargin(widthLabel, new Insets(0, 0, 10, 0));
        TwoNodeHBox.setMargin(heightLabel, new Insets(0, 0, 10, 0));
        TwoNodeHBox.setMargin(areaWidthTextField, new Insets(0, 0, 10, 0));
        TwoNodeHBox.setMargin(areaHeightTextField, new Insets(0, 0, 10, 0));
        TwoNodeHBox.setMargin(isStarCheckbox, new Insets(10, 0, 10, 0));
        TwoNodeHBox.setMargin(isStarLabel, new Insets(10, 0, 10, 0));
        TwoNodeHBox.setMargin(okButton, new Insets(10, 10, 10, 0));
        TwoNodeHBox.setMargin(cancelButton, new Insets(10, 0, 10, 10));



        VBox root = new VBox(
                new CenteredLabelBox(new SettingLabel("Screen Size", 14)),
                new TwoNodeHBox(widthLabel, areaWidthTextField),
                new TwoNodeHBox(heightLabel, areaHeightTextField),
                new CenteredLabelBox(new SettingLabel("Line thickness (from 1 to " + PaintSettings.maxThickness + ")", 14)),
                new TwoNodeHBox(lineThicknessSlider, lineThicknessTextField),
                new CenteredLabelBox(new SettingLabel("Stamp vertex number (from " + PaintSettings.minStampVertexNum +
                        " to " + PaintSettings.maxStampVertexNum + ")", 14)),
                new TwoNodeHBox(stampVertexNumSlider, stampVertexNumTextField),
                new CenteredLabelBox(new SettingLabel("Stamp radius (from " + PaintSettings.minStampSize + " to " +
                        PaintSettings.maxStampSize + ")", 14)),
                new TwoNodeHBox(stampSizeSlider, stampSizeTextField),
                new CenteredLabelBox(new SettingLabel("Stamp rotation angle", 14)),
                new TwoNodeHBox(stampRotationSlider, stampRotationTextField),
                new TwoNodeHBox(isStarCheckbox, isStarLabel),
                new TwoNodeHBox(okButton, cancelButton)
        );
        Scene scene = new Scene(root, 420, 465);
        setScene(scene);
    }

    @Override
    public void update() {
        lineThicknessTextField.setText(Integer.toString(model.getSettings().getLineThickness()));
        lineThicknessSlider.valueProperty().setValue(model.getSettings().getLineThickness());
        stampVertexNumTextField.setText(Integer.toString(model.getSettings().getStampVertexNum()));
        stampVertexNumSlider.valueProperty().setValue(model.getSettings().getStampVertexNum());
        stampSizeTextField.setText(Integer.toString(model.getSettings().getStampSize()));
        stampSizeSlider.valueProperty().setValue(model.getSettings().getStampSize());
        stampRotationTextField.setText(Integer.toString(model.getSettings().getStampRotation()));
        stampRotationSlider.valueProperty().setValue(model.getSettings().getStampRotation());
        isStarCheckbox.setSelected(model.getSettings().isStarStamp());
        areaWidthTextField.setText(Integer.toString(model.getNewWidth()));
        areaHeightTextField.setText(Integer.toString(model.getNewHeight()));
    }

    public void setCancelButtonHandler(EventHandler<ActionEvent> handler) {
        cancelButton.setOnAction(handler);
    }

    public void setOkButtonHandler(EventHandler<ActionEvent> handler) {
        okButton.setOnAction(handler);
    }

    public void setLineThicknessHandlers(ChangeListener<Number> sliderHandler,
                                         EventHandler<ActionEvent> textFieldActionHandler,
                                         ChangeListener<Boolean> textFieldFocusedHandler) {
        lineThicknessSlider.valueProperty().addListener(sliderHandler);
        lineThicknessTextField.setOnAction(textFieldActionHandler);
        lineThicknessTextField.focusedProperty().addListener(textFieldFocusedHandler);
    }

    public void setStampVertexNumHandlers(ChangeListener<Number> sliderHandler,
                                          EventHandler<ActionEvent> textFieldActionHandler,
                                          ChangeListener<Boolean> textFieldFocusedHandler) {
        stampVertexNumSlider.valueProperty().addListener(sliderHandler);
        stampVertexNumTextField.setOnAction(textFieldActionHandler);
        stampVertexNumTextField.focusedProperty().addListener(textFieldFocusedHandler);
    }

    public void setStampSizeHandlers(ChangeListener<Number> sliderHandler,
                                     EventHandler<ActionEvent> textFieldActionHandler,
                                     ChangeListener<Boolean> textFieldFocusedHandler) {
        stampSizeSlider.valueProperty().addListener(sliderHandler);
        stampSizeTextField.setOnAction(textFieldActionHandler);
        stampSizeTextField.focusedProperty().addListener(textFieldFocusedHandler);
    }

    public void setStampRotationHandlers(ChangeListener<Number> sliderHandler,
                                         EventHandler<ActionEvent> textFieldActionHandler,
                                         ChangeListener<Boolean> textFieldFocusedHandler) {
        stampRotationSlider.valueProperty().addListener(sliderHandler);
        stampRotationTextField.setOnAction(textFieldActionHandler);
        stampRotationTextField.focusedProperty().addListener(textFieldFocusedHandler);
    }

    public void setIsStarStampButtonHandler(EventHandler<ActionEvent> handler) {
        isStarCheckbox.setOnAction(handler);
    }

    public void setAreaWidthHandlers(EventHandler<ActionEvent> textFieldActionHandler,
                                    ChangeListener<Boolean> textFieldFocusedHandler) {
        areaWidthTextField.setOnAction(textFieldActionHandler);
        areaWidthTextField.focusedProperty().addListener(textFieldFocusedHandler);
    }

    public void setAreaHeightHandlers(EventHandler<ActionEvent> textFieldActionHandler,
                                      ChangeListener<Boolean> textFieldFocusedHandler) {
        areaHeightTextField.setOnAction(textFieldActionHandler);
        areaHeightTextField.focusedProperty().addListener(textFieldFocusedHandler);
    }
}
