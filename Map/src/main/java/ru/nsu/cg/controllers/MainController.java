package ru.nsu.cg.controllers;

import init.MapApplication;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ru.nsu.cg.*;
import ru.nsu.cg.gui.AboutWindow;
import ru.nsu.cg.gui.DialogWindow;
import ru.nsu.cg.gui.ErrorWindow;
import ru.nsu.cg.model.MapModel;
import ru.nsu.cg.model.MapSettings;
import ru.nsu.cg.model.PropertyReader;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.Consumer;

public class MainController implements Subscriber, Initializable {
    private static long lastTime = System.nanoTime();
    private static final long minDeltaTime = 15000000;

    private MapModel model = null;

    private FileChooser saveFileChooser;
    private FileChooser openFileChooser;

    @FXML
    private Pane resizePane;

    @FXML
    private ImageView displayImageView;

    @FXML
    private MenuItem openMenu;
    @FXML
    private MenuItem saveMenu;
    @FXML
    private MenuItem settingsMenu;
    @FXML
    private RadioMenuItem smoothDisplayModeMenu;
    @FXML
    private RadioMenuItem colorMapDisplayModeMenu;
    @FXML
    private ToggleGroup displayModeToggleGroup;
    @FXML
    private CheckMenuItem isolineDisplayMenu;
    @FXML
    private CheckMenuItem gridDisplayMenu;
    @FXML
    private MenuItem aboutMenu;

    @FXML
    private Button openButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button settingsButton;
    @FXML
    private Button displayModeButton;
    private ImageView colorMapDisplayModeIcon;
    private ImageView smoothDisplayModeIcon;
    @FXML
    private ToggleButton isolineDisplayButton;
    @FXML
    private ToggleButton gridDisplayButton;
    @FXML
    private Button aboutButton;

    @FXML
    private HBox legendLabelsHBox;
    @FXML
    private ImageView legendImageView;
    @FXML
    private Label dynamicXYFLabel;

    public MainController() {
        initFileChoosers();
    }

    private void initFileChoosers() {
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
                new FileChooser.ExtensionFilter("Text", "*.txt"),
            new FileChooser.ExtensionFilter("All Files", "*.*")
        );
    }

    public void setModel(MapModel model) {
        this.model = model;
        model.addSubscriber(this);
        update();
    }

    @Override
    public void update() {
        displayImageView.setImage(model.getImage());
        legendImageView.setImage(model.getLegendImage());
        switch (model.getSettings().getDisplayMode()) {
            case SMOOTH:
                displayModeToggleGroup.selectToggle(smoothDisplayModeMenu);
                displayModeButton.setGraphic(smoothDisplayModeIcon);
                break;
            case COLOR_MAP:
                displayModeToggleGroup.selectToggle(colorMapDisplayModeMenu);
                displayModeButton.setGraphic(colorMapDisplayModeIcon);
                break;
        }
        isolineDisplayButton.setSelected(model.getSettings().isIsolinesVisible());
        isolineDisplayMenu.setSelected(model.getSettings().isIsolinesVisible());
        gridDisplayButton.setSelected(model.getSettings().isGridVisible());
        gridDisplayMenu.setSelected(model.getSettings().isGridVisible());
        legendLabelsHBox.getChildren().clear();
        double[] labels = model.getLegendLabels();
        legendLabelsHBox.setSpacing(model.getLegendImage().getWidth() / (labels.length - 1) - 15.0);
        for (double l : labels) {
            addLegendLabel(l);
        }
    }

    @FXML
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        resizePane.widthProperty().addListener((obs, ov, nv) -> {
            long currentTime = System.nanoTime();
            if (currentTime - lastTime < minDeltaTime) {
                return;
            }
            lastTime = currentTime;
            model.resize((int)nv.doubleValue(), (int)model.getImage().getHeight());
        });
        resizePane.heightProperty().addListener((obs, ov, nv) -> {
            long currentTime = System.nanoTime();
            if (currentTime - lastTime < minDeltaTime) {
                return;
            }
            lastTime = currentTime;
            model.resize((int)model.getImage().getWidth(), (int)nv.doubleValue());
        });

        displayImageView.setOnMouseMoved((e) -> {
            dynamicXYFLabel.setText("X = " + (float)Math.round(model.getFunctionX(e.getX()) * 1000) / 1000 +
                    "; Y = " + (float)Math.round(model.getFunctionY(e.getY()) * 1000) / 1000 +
                    "; F(x,y) = " + (float)Math.round(model.getFunctionValue(e.getX(), e.getY()) * 1000) / 1000);
        });

        displayImageView.setOnMousePressed((e) -> {
            model.getSettings().setDynamicIsolineVisible(true);
            model.setDynamicIsoline(model.getFunctionValue(e.getX(), e.getY()));
            update();
        });

        displayImageView.setOnMouseDragged((e) -> {
            model.setDynamicIsoline(model.getFunctionValue(e.getX(), e.getY()));
            update();
        });

        displayImageView.setOnMouseReleased((e) -> {
            model.setDynamicIsoline(model.getFunctionValue(e.getX(), e.getY()));
            model.getSettings().setDynamicIsolineVisible(false);
            update();
        });



        Image colorMapDisplayModeImg = new Image(getClass().getResourceAsStream("/icons/colorMapDisplayMode.png"),
                25, 25, true, true);
        colorMapDisplayModeIcon = new ImageView(colorMapDisplayModeImg);
        Image smoothDisplayModeImg = new Image(getClass().getResourceAsStream("/icons/smoothDisplayMode.png"),
                25, 25, true, true);
        smoothDisplayModeIcon = new ImageView(smoothDisplayModeImg);

        EventHandler<ActionEvent> openAction = (e) -> {
            File file = openFileChooser.showOpenDialog(MapApplication.getPrimaryStage());
            if (file == null) {
                return;
            }
            try {
                new PropertyReader(model).readPropertiesFromFile(file);
            } catch (PropertyReader.PropertyException exc) {
                Platform.runLater(() -> new ErrorWindow(exc.getMessage()));
            }
        };
        openMenu.setOnAction(openAction);
        openButton.setOnAction(openAction);

        EventHandler<ActionEvent> saveAction = (e) -> {
            File file = saveFileChooser.showSaveDialog(MapApplication.getPrimaryStage());
            if (file == null) {
                return;
            }
            if (!model.saveImage(file)) {
                Platform.runLater(() -> new ErrorWindow("Error: unable to save an image..."));
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

        displayModeButton.setOnAction((e) -> {
            switch (model.getSettings().getDisplayMode()) {
                case SMOOTH:
                    model.getSettings().setDisplayMode(MapSettings.DisplayMode.COLOR_MAP);
                    break;
                case COLOR_MAP:
                    model.getSettings().setDisplayMode(MapSettings.DisplayMode.SMOOTH);
                    break;
            }
            model.updateImage();
        });

        colorMapDisplayModeMenu.setOnAction((e) -> {
            model.getSettings().setDisplayMode(MapSettings.DisplayMode.COLOR_MAP);
            model.updateImage();
        });
        smoothDisplayModeMenu.setOnAction((e) -> {
            model.getSettings().setDisplayMode(MapSettings.DisplayMode.SMOOTH);
            model.updateImage();
        });

        ChangeListener<Boolean> isolineDisplayAction = (obs, ov, nv) -> {
            model.getSettings().setIsolinesVisible(nv);
            model.updateIsolines();
        };
        isolineDisplayButton.selectedProperty().addListener(isolineDisplayAction);
        isolineDisplayMenu.selectedProperty().addListener(isolineDisplayAction);

        ChangeListener<Boolean> gridDisplayAction = (obs, ov, nv) -> {
            model.getSettings().setGridVisible(nv);
            model.updateGrid();
        };

        gridDisplayButton.selectedProperty().addListener(gridDisplayAction);
        gridDisplayMenu.selectedProperty().addListener(gridDisplayAction);
    }

    private void addLegendLabel(double labelValue) {
        Label label = new Label("" + (float)Math.round(labelValue * 100) / 100);
        label.getStyleClass().add("legendLabel");
        legendLabelsHBox.getChildren().add(label);
    }
}
