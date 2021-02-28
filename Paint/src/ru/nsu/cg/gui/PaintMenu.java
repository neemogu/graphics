package ru.nsu.cg.gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import ru.nsu.cg.PaintModel;
import ru.nsu.cg.Subscriber;

public class PaintMenu extends MenuBar implements Subscriber {
    private final MenuItem openFileItem;
    private final MenuItem saveFileItem;
    private final ToggleGroup toolPickingGroup;
    private final RadioMenuItem lineToolItem;
    private final RadioMenuItem stampToolItem;
    private final RadioMenuItem fillToolItem;
    private final MenuItem toolSettingsItem;
    private final MenuItem clearItem;
    private final MenuItem aboutItem;
    private final PaintModel model;

    public PaintMenu(PaintModel model) {
        super();
        this.model = model;
        Menu fileMenu = new Menu("File");
        openFileItem = new MenuItem("Open Image");
        saveFileItem = new MenuItem("Save Image");
        fileMenu.getItems().addAll(openFileItem, new SeparatorMenuItem(), saveFileItem);

        Menu viewMenu = new Menu("View");
        Menu toolPickingMenu = new Menu("Tools");
        lineToolItem = new RadioMenuItem("Line");
        stampToolItem = new RadioMenuItem("Stamp");
        fillToolItem = new RadioMenuItem("Fill");
        toolPickingGroup = new ToggleGroup();
        toolPickingGroup.getToggles().addAll(lineToolItem, stampToolItem, fillToolItem);
        toolPickingMenu.getItems().addAll(lineToolItem, new SeparatorMenuItem(), stampToolItem,
                new SeparatorMenuItem(), fillToolItem);

        toolSettingsItem = new MenuItem("Settings");
        clearItem = new MenuItem("Clear");
        viewMenu.getItems().addAll(toolPickingMenu, toolSettingsItem, new SeparatorMenuItem(), clearItem);

        Menu helpMenu = new Menu("Help");
        aboutItem = new MenuItem("About");
        helpMenu.getItems().addAll(aboutItem);

        getMenus().addAll(fileMenu, viewMenu, helpMenu);
    }

    @Override
    public void update() {
        switch(model.getSettings().getMode()) {
            case LINE:
                toolPickingGroup.selectToggle(lineToolItem);
                break;
            case STAMP:
                toolPickingGroup.selectToggle(stampToolItem);
                break;
            case FILL:
                toolPickingGroup.selectToggle(fillToolItem);
                break;
        }
    }

    public void setSaveFileItemHandler(EventHandler<ActionEvent> handler) {
        saveFileItem.setOnAction(handler);
    }

    public void setOpenFileItemHandler(EventHandler<ActionEvent> handler) {
        openFileItem.setOnAction(handler);
    }

    public void setClearItemHandler(EventHandler<ActionEvent> handler) {
        clearItem.setOnAction(handler);
    }

    public void setLineToolItemHandler(EventHandler<ActionEvent> handler) {
        lineToolItem.setOnAction(handler);
    }

    public void setStampToolItemHandler(EventHandler<ActionEvent> handler) {
        stampToolItem.setOnAction(handler);
    }

    public void setFillToolItemHandler(EventHandler<ActionEvent> handler) {
        fillToolItem.setOnAction(handler);
    }

    public void setToolSettingsItemHandler(EventHandler<ActionEvent> handler) {
        toolSettingsItem.setOnAction(handler);
    }

    public void setAboutItemHandler(EventHandler<ActionEvent> handler) {
        aboutItem.setOnAction(handler);
    }
}
