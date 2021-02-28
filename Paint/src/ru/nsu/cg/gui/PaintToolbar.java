package ru.nsu.cg.gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Separator;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import ru.nsu.cg.PaintModel;
import ru.nsu.cg.Subscriber;
import ru.nsu.cg.buttons.*;

public class PaintToolbar extends ToolBar implements Subscriber {
    private final ToggleGroup modeGroup;
    private final LineButton lineButton;
    private final FillButton fillButton;
    private final StampButton stampButton;
    private final SaveButton saveButton;
    private final OpenButton openButton;
    private final SettingsButton toolSettingsButton;
    private final PaintColorPicker paintColorPicker;
    private final ClearButton clearButton;
    private final AboutButton aboutButton;
    private final PaintModel model;

    public PaintToolbar(PaintModel model) {
        super();
        this.model = model;
        modeGroup = new ToggleGroup();
        lineButton = new LineButton();
        fillButton = new FillButton();
        stampButton = new StampButton();
        modeGroup.getToggles().addAll(lineButton, fillButton, stampButton);

        saveButton = new SaveButton();
        openButton = new OpenButton();
        toolSettingsButton = new SettingsButton();
        paintColorPicker = new PaintColorPicker();
        clearButton = new ClearButton();
        aboutButton = new AboutButton();

        getItems().addAll(saveButton, openButton, new Separator(), lineButton, stampButton, fillButton,
                new Separator(), toolSettingsButton, paintColorPicker, new Separator(),
                clearButton, new Separator(), aboutButton);
    }

    @Override
    public void update() {
        switch(model.getSettings().getMode()) {
            case LINE:
                modeGroup.selectToggle(lineButton);
                break;
            case STAMP:
                modeGroup.selectToggle(stampButton);
                break;
            case FILL:
                modeGroup.selectToggle(fillButton);
                break;
        }
        paintColorPicker.setValue(model.getSettings().getColor());
    }

    public void setSaveButtonHandler(EventHandler<ActionEvent> handler) {
        saveButton.setOnAction(handler);
    }

    public void setOpenButtonHandler(EventHandler<ActionEvent> handler) {
        openButton.setOnAction(handler);
    }

    public void setClearButtonHandler(EventHandler<ActionEvent> handler) {
        clearButton.setOnAction(handler);
    }

    public void setLineButtonHandler(EventHandler<ActionEvent> handler) {
        lineButton.setOnAction(handler);
    }

    public void setStampButtonHandler(EventHandler<ActionEvent> handler) {
        stampButton.setOnAction(handler);
    }

    public void setFillButtonHandler(EventHandler<ActionEvent> handler) {
        fillButton.setOnAction(handler);
    }

    public void setToolSettingsButtonHandler(EventHandler<ActionEvent> handler) {
        toolSettingsButton.setOnAction(handler);
    }

    public void setAboutButtonHandler(EventHandler<ActionEvent> handler) {
        aboutButton.setOnAction(handler);
    }

    public void setColorPickerHandler(EventHandler<ActionEvent> handler) {
        paintColorPicker.setOnAction(handler);
    }
}
