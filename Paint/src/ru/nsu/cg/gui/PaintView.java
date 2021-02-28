package ru.nsu.cg.gui;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ru.nsu.cg.PaintModel;
import ru.nsu.cg.Subscriber;

import java.io.File;

public class PaintView implements Subscriber {
    private final PaintToolbar toolbar;
    private final PaintMenu menu;
    private final PaintDrawArea paintDrawArea;
    private final ToolSettingsWindow tsWindow;
    private final AboutWindow aboutWindow;
    private final Stage primaryStage;
    private final FileChooser saveFileChooser;
    private final FileChooser openFileChooser;


    public PaintView(Stage primaryStage, PaintModel model) {
        this.primaryStage = primaryStage;
        menu = new PaintMenu(model);
        toolbar = new PaintToolbar(model);
        paintDrawArea = new PaintDrawArea(model);
        VBox root = new VBox(menu, toolbar, new HBox(paintDrawArea));
        root.setBackground(new Background(new BackgroundFill(Color.gray(0.85), CornerRadii.EMPTY, Insets.EMPTY)));
        primaryStage.setScene(new Scene(root));
        tsWindow = new ToolSettingsWindow(model);
        aboutWindow = new AboutWindow();

        saveFileChooser = new FileChooser();
        saveFileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        saveFileChooser.setInitialFileName("image.png");
        saveFileChooser.setTitle("Save");
        saveFileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
                );

        openFileChooser = new FileChooser();
        openFileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        openFileChooser.setTitle("Open");
        openFileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image", "*.png", "*.bmp", "*.jpg", "*.gif")
        );
    }

    @Override
    public void update() {
        toolbar.update();
        menu.update();
        paintDrawArea.update();
        tsWindow.update();
    }

    public void showError(String message) {
        Platform.runLater(() -> new ErrorWindow(message).show());
    }

    public File showSaveDialog() {
        return saveFileChooser.showSaveDialog(primaryStage);
    }

    public File showOpenDialog() {
        return openFileChooser.showOpenDialog(primaryStage);
    }

    public void showAboutWindow() {
        aboutWindow.show();
    }

    public void showToolSettingsWindow() {
        tsWindow.update();
        tsWindow.show();
    }

    public void closeToolSettingsWindow() {
        tsWindow.close();
    }

    public void setMousePressedHandler(EventHandler<? super MouseEvent> eventHandler) {
        paintDrawArea.setMousePressedHandler(eventHandler);
    }

    public void setMouseReleasedHandler(EventHandler<? super MouseEvent> eventHandler) {
        paintDrawArea.setMouseReleasedHandler(eventHandler);
    }

    public void setMouseDraggedHandler(EventHandler<? super MouseEvent> eventHandler) {
        paintDrawArea.setMouseDraggedHandler(eventHandler);
    }

    public void setSaveFileHandler(EventHandler<ActionEvent> handler) {
        toolbar.setSaveButtonHandler(handler);
        menu.setSaveFileItemHandler(handler);
    }

    public void setOpenFileHandler(EventHandler<ActionEvent> handler) {
        toolbar.setOpenButtonHandler(handler);
        menu.setOpenFileItemHandler(handler);
    }

    public void setClearHandler(EventHandler<ActionEvent> handler) {
        toolbar.setClearButtonHandler(handler);
        menu.setClearItemHandler(handler);
    }

    public void setLineToolHandler(EventHandler<ActionEvent> handler) {
        toolbar.setLineButtonHandler(handler);
        menu.setLineToolItemHandler(handler);
    }

    public void setStampToolHandler(EventHandler<ActionEvent> handler) {
        toolbar.setStampButtonHandler(handler);
        menu.setStampToolItemHandler(handler);
    }

    public void setFillToolHandler(EventHandler<ActionEvent> handler) {
        toolbar.setFillButtonHandler(handler);
        menu.setFillToolItemHandler(handler);
    }

    public void setColorPickerHandler(EventHandler<ActionEvent> handler) {
        toolbar.setColorPickerHandler(handler);
    }

    public void setOpenToolSettingsWindowHandler(EventHandler<ActionEvent> handler) {
        toolbar.setToolSettingsButtonHandler(handler);
        menu.setToolSettingsItemHandler(handler);
    }

    public void setAboutHandler(EventHandler<ActionEvent> handler) {
        toolbar.setAboutButtonHandler(handler);
        menu.setAboutItemHandler(handler);
    }

    public void setCancelButtonToolSettingsWindowHandler(EventHandler<ActionEvent> handler) {
        tsWindow.setCancelButtonHandler(handler);
    }

    public void setOkButtonToolSettingsWindowHandler(EventHandler<ActionEvent> handler) {
        tsWindow.setOkButtonHandler(handler);
    }

    public void setLineThicknessSettingHandlers(ChangeListener<Number> sliderHandler,
                                                EventHandler<ActionEvent> textFieldActionHandler,
                                                ChangeListener<Boolean> textFieldFocusedHandler) {
        tsWindow.setLineThicknessHandlers(sliderHandler, textFieldActionHandler, textFieldFocusedHandler);
    }

    public void setStampVertexNumSettingHandlers(ChangeListener<Number> sliderHandler,
                                                 EventHandler<ActionEvent> textFieldActionHandler,
                                                 ChangeListener<Boolean> textFieldFocusedHandler) {
        tsWindow.setStampVertexNumHandlers(sliderHandler, textFieldActionHandler, textFieldFocusedHandler);
    }

    public void setStampSizeSettingHandlers(ChangeListener<Number> sliderHandler,
                                            EventHandler<ActionEvent> textFieldActionHandler,
                                            ChangeListener<Boolean> textFieldFocusedHandler) {
        tsWindow.setStampSizeHandlers(sliderHandler, textFieldActionHandler, textFieldFocusedHandler);
    }

    public void setStampRotationSettingHandlers(ChangeListener<Number> sliderHandler,
                                                EventHandler<ActionEvent> textFieldActionHandler,
                                                ChangeListener<Boolean> textFieldFocusedHandler) {
        tsWindow.setStampRotationHandlers(sliderHandler, textFieldActionHandler, textFieldFocusedHandler);
    }

    public void setIsStarStampButtonHandler(EventHandler<ActionEvent> handler) {
        tsWindow.setIsStarStampButtonHandler(handler);
    }

    public void setAreaWidthSettingHandler(EventHandler<ActionEvent> textFieldActionHandler,
                                           ChangeListener<Boolean> textFieldFocusedHandler) {
        tsWindow.setAreaWidthHandlers(textFieldActionHandler, textFieldFocusedHandler);
    }

    public void setAreaHeightSettingHandler(EventHandler<ActionEvent> textFieldActionHandler,
                                            ChangeListener<Boolean> textFieldFocusedHandler) {
       tsWindow.setAreaHeightHandlers(textFieldActionHandler, textFieldFocusedHandler);
    }
}
