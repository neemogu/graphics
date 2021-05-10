package ru.nsu.cg.controllers;

import init.WireframeApplication;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import ru.nsu.cg.*;
import ru.nsu.cg.gui.AboutWindow;
import ru.nsu.cg.gui.DialogWindow;
import ru.nsu.cg.gui.ErrorWindow;
import ru.nsu.cg.model.ModelFile;
import ru.nsu.cg.model.WireframeModel;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Subscriber, Initializable {
    private static long lastTime = System.nanoTime();
    private static final long minMouseDeltaTime = 15000000;
    private static final long minResizeDeltaTime = 60000000;

    private static final float rotationSpeed = 270;

    private double lastMouseX = 0;
    private double lastMouseY = 0;

    private boolean invertY = false;

    private WireframeModel model = null;

    private FileChooser saveFileChooser;
    private FileChooser openFileChooser;

    @FXML
    private Pane resizePane;

    @FXML
    private ImageView mainImageView;

    @FXML
    private MenuItem openMenu;
    @FXML
    private MenuItem saveMenu;
    @FXML
    private MenuItem settingsMenu;
    @FXML
    private MenuItem aboutMenu;
    @FXML
    private MenuItem resetMenu;

    @FXML
    private Button openButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button settingsButton;
    @FXML
    private Button resetButton;
    @FXML
    private Button aboutButton;

    public MainController() {
        initFileChoosers();
    }

    private void initFileChoosers() {
        saveFileChooser = new FileChooser();
        saveFileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        saveFileChooser.setInitialFileName("model.wfm");
        saveFileChooser.setTitle("Save");
        saveFileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Wireframe model", "*.wfm")
        );

        openFileChooser = new FileChooser();
        openFileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        openFileChooser.setTitle("Open");
        openFileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Wireframe model", "*.wfm")
        );
    }

    public void setModel(WireframeModel model) {
        this.model = model;
        model.addSubscriber(this);
        update();
        Platform.runLater(() -> {
            model.resize((int)resizePane.widthProperty().doubleValue(), (int)resizePane.heightProperty().doubleValue());
        });
    }

    @Override
    public void update() {
        mainImageView.setImage(model.getScreen());
    }

    @FXML
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        resizePane.widthProperty().addListener((obs, ov, nv) -> {
            long currentTime = System.nanoTime();
            if (currentTime - lastTime < minResizeDeltaTime) {
                return;
            }
            lastTime = currentTime;
            Platform.runLater(() -> {
                model.resize((int)nv.doubleValue(), model.getScreenHeight());
            });
        });

        resizePane.heightProperty().addListener((obs, ov, nv) -> {
            long currentTime = System.nanoTime();
            if (currentTime - lastTime < minResizeDeltaTime) {
                return;
            }
            lastTime = currentTime;
            Platform.runLater(() -> {
                model.resize(model.getScreenWidth(), (int)nv.doubleValue());
            });
        });

        EventHandler<ActionEvent> openAction = (e) -> {
            File file = openFileChooser.showOpenDialog(WireframeApplication.getPrimaryStage());
            if (file == null) {
                return;
            }
            model.getSettingsModel().getCurveSettings().save();
            model.getSettingsModel().getCurve().save();
            try {
                ModelFile.openModel(file, model);
            } catch (IOException exc) {
                model.getSettingsModel().getCurveSettings().recover();
                model.getSettingsModel().getCurve().recover();
                Platform.runLater(() -> new ErrorWindow(exc.getMessage()));
            }
            model.updateScreen();
        };
        openMenu.setOnAction(openAction);
        openButton.setOnAction(openAction);

        EventHandler<ActionEvent> saveAction = (e) -> {
            File file = saveFileChooser.showSaveDialog(WireframeApplication.getPrimaryStage());
            if (file == null) {
                return;
            }
            try {
                ModelFile.saveModel(file, model);
            } catch (IOException exc) {
                Platform.runLater(() -> new ErrorWindow(exc.getMessage()));
            }
        };
        saveMenu.setOnAction(saveAction);
        saveButton.setOnAction(saveAction);

        EventHandler<ActionEvent> settingsAction = (e) -> {
            Platform.runLater(() -> new DialogWindow("/fxml/settingsDialog.fxml", model));
        };
        settingsMenu.setOnAction(settingsAction);
        settingsButton.setOnAction(settingsAction);

        EventHandler<ActionEvent> aboutAction = (e) -> {
            Platform.runLater(AboutWindow::new);
        };
        aboutMenu.setOnAction(aboutAction);
        aboutButton.setOnAction(aboutAction);

        EventHandler<ActionEvent> resetAction = (e) -> {
            model.resetRotations();
        };
        resetButton.setOnAction(resetAction);
        resetMenu.setOnAction(resetAction);

        resizePane.setOnScroll((e) -> {
            if (e.getDeltaY() > 0) {
                model.getProjection().zoomIn();
                model.updateScreen();
            } else if (e.getDeltaY() < 0) {
                model.getProjection().zoomOut();
                model.updateScreen();
            }
        });

        mainImageView.setOnMousePressed((e) -> {
            lastMouseX = e.getX();
            lastMouseY = e.getY();
            invertY = !(model.getYaw() <= 180);
        });

        mainImageView.setOnMouseDragged((e) -> {
            long currentTime = System.nanoTime();
            if (currentTime - lastTime < minMouseDeltaTime) {
                return;
            }
            double deltaX = (e.getX() - lastMouseX) / mainImageView.getImage().getWidth();
            double deltaY = (e.getY() - lastMouseY) / mainImageView.getImage().getHeight();
            lastMouseX = e.getX();
            lastMouseY = e.getY();
            if (!invertY) {
                model.addRoll((float)deltaY * rotationSpeed);
            } else {
                model.addRoll((float)(-deltaY) * rotationSpeed);
            }
            model.addYaw((float) deltaX * rotationSpeed);
            model.updateScreen();
        });
    }
}
