package ru.nsu.cg.controllers;

import com.sun.prism.image.ViewPort;
import init.FiltersApplication;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.stage.FileChooser;
import ru.nsu.cg.*;
import ru.nsu.cg.gui.AboutWindow;
import ru.nsu.cg.gui.DialogWindow;
import ru.nsu.cg.gui.ErrorWindow;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public class MainController implements Subscriber, Initializable {
    private FiltersModel model = null;

    private FileChooser saveFileChooser;
    private FileChooser openFileChooser;

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private ImageView workspaceImageView;

    @FXML
    private MenuItem openMenu;
    @FXML
    private MenuItem saveMenu;
    @FXML
    private MenuItem closeMenu;
    @FXML
    private MenuItem resetMenu;
    @FXML
    private MenuItem zoomMenu;
    @FXML
    private MenuItem reduceToViewportMenu;
    @FXML
    private MenuItem originSizeMenu;
    @FXML
    private MenuItem rotateMenu;
    @FXML
    private MenuItem settingsMenu;
    @FXML
    private MenuItem negativeMenu;
    @FXML
    private MenuItem grayscaleMenu;
    @FXML
    private MenuItem gaussianMenu;
    @FXML
    private MenuItem sharpeningMenu;
    @FXML
    private MenuItem watercolorMenu;
    @FXML
    private MenuItem robertsMenu;
    @FXML
    private MenuItem sobelMenu;
    @FXML
    private MenuItem embossingMenu;
    @FXML
    private MenuItem brightnessMenu;
    @FXML
    private MenuItem contrastMenu;
    @FXML
    private MenuItem gammaMenu;
    @FXML
    private MenuItem fsdMenu;
    @FXML
    private MenuItem odMenu;
    @FXML
    private MenuItem aboutMenu;
    @FXML
    private MenuItem lastFilterMenu;
    @FXML
    private MenuItem solariseMenu;

    @FXML
    private Button openButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button resetButton;
    @FXML
    private Button zoomButton;
    @FXML
    private Button reduceToViewportButton;
    @FXML
    private Button originSizeButton;
    @FXML
    private Button rotateButton;
    @FXML
    private Button settingsButton;
    @FXML
    private Button negativeButton;
    @FXML
    private Button grayscaleButton;
    @FXML
    private Button brightnessButton;
    @FXML
    private Button contrastButton;
    @FXML
    private Button gammaButton;
    @FXML
    private Button gaussianButton;
    @FXML
    private Button sharpeningButton;
    @FXML
    private Button watercolorButton;
    @FXML
    private Button robertsButton;
    @FXML
    private Button sobelButton;
    @FXML
    private Button embossingButton;
    @FXML
    private Button fsdButton;
    @FXML
    private Button odButton;
    @FXML
    private Button aboutButton;
    @FXML
    private Button solariseButton;

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
                new FileChooser.ExtensionFilter("Image", "*.png", "*.bmp", "*.jpg", "*.gif")
        );
    }

    public void setModel(FiltersModel model) {
        this.model = model;
        model.addSubscriber(this);
    }

    @Override
    public void update() {
        if (model.isImageOpened()) {
            scrollPane.setVisible(true);
            workspaceImageView.setImage(model.getImage());
        }
        resetButton.setDisable(!model.canReset());
        lastFilterMenu.setText("Last Filter (" + model.getSettings().getLastFilter().name() + ")");
    }

    @FXML
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        workspaceImageView.setOnMouseClicked((e) -> {
            if (e.getButton().equals(MouseButton.PRIMARY) && e.getClickCount() == 2) {
                model.changeImage();
            }
        });

        EventHandler<ActionEvent> openAction = (e) -> {
            File file = openFileChooser.showOpenDialog(FiltersApplication.getPrimaryStage());
            if (!model.openImage(file)) {
                Platform.runLater(() -> new ErrorWindow("Error: unable to open an image..."));
            }
        };
        openMenu.setOnAction(openAction);
        openButton.setOnAction(openAction);

        EventHandler<ActionEvent> saveAction = (e) -> {
            File file = saveFileChooser.showSaveDialog(FiltersApplication.getPrimaryStage());
            if (!model.saveImage(file)) {
                Platform.runLater(() -> new ErrorWindow("Error: unable to save an image..."));
            }
        };
        saveMenu.setOnAction(saveAction);
        saveButton.setOnAction(saveAction);

        closeMenu.setOnAction((e) -> FiltersApplication.getPrimaryStage().close());

        EventHandler<ActionEvent> aboutAction = (e) -> Platform.runLater(() -> new AboutWindow().show());
        aboutMenu.setOnAction(aboutAction);
        aboutButton.setOnAction(aboutAction);

        EventHandler<ActionEvent> settingsAction = (e) -> Platform.runLater(() -> {
            Platform.runLater(() -> new DialogWindow("/fxml/dialogs/settingsDialog.fxml", model));
        });
        settingsMenu.setOnAction(settingsAction);
        settingsButton.setOnAction(settingsAction);

        setFilterListener(Filters::negative, FiltersSettings.Filter.NEGATIVE, negativeButton, negativeMenu);
        setFilterListener(Filters::greyscale, FiltersSettings.Filter.GREYSCALE, grayscaleButton, grayscaleMenu);
        setFilterListener(Filters::sharpening, FiltersSettings.Filter.SHARPENING, sharpeningButton, sharpeningMenu);
        setFilterListener(Filters::embossing, FiltersSettings.Filter.EMBOSSING, embossingButton, embossingMenu);

        setDialogListener("gammaDialog", gammaButton, gammaMenu);
        setDialogListener("brightnessDialog", brightnessButton, brightnessMenu);
        setDialogListener("contrastDialog", contrastButton, contrastMenu);
        setDialogListener("gaussianDialog", gaussianButton, gaussianMenu);
        setDialogListener("watercolorDialog", watercolorButton, watercolorMenu);
        setDialogListener("robertsDialog", robertsButton, robertsMenu);
        setDialogListener("sobelDialog", sobelButton, sobelMenu);
        setDialogListener("orderedDitheringDialog", odButton, odMenu);
        setDialogListener("floydDitheringDialog", fsdButton, fsdMenu);
        setDialogListener("rotateDialog", rotateButton, rotateMenu);
        setDialogListener("resizeDialog", zoomButton, zoomMenu);
        setDialogListener("solariseDialog", solariseButton, solariseMenu);

        EventHandler<ActionEvent> originSizeAction = (e) -> {
            if (!model.isImageOpened()) {
                return;
            }
            model.setOriginScale();
            model.applyChanges();
        };
        originSizeButton.setOnAction(originSizeAction);
        originSizeMenu.setOnAction(originSizeAction);

        EventHandler<ActionEvent> viewportSizeAction = (e) -> {
            if (!model.isImageOpened()) {
                return;
            }
            int hbarWidth = 0;
            int vbarWidth = 0;
            Set<Node> nodes = scrollPane.lookupAll(".scroll-bar");
            for (final Node node : nodes) {
                if (node instanceof ScrollBar) {
                    ScrollBar sb = (ScrollBar) node;
                    if (!sb.isVisible()) {
                        continue;
                    }
                    if (sb.getOrientation() == Orientation.VERTICAL) {
                        vbarWidth = (int)sb.getWidth();
                    } else {
                        hbarWidth = (int)sb.getHeight();
                    }
                }
            }
            int viewportWidth = (int)scrollPane.getViewportBounds().getWidth() + vbarWidth;
            int viewportHeight = (int)scrollPane.getViewportBounds().getHeight() + hbarWidth;
            double widthRatio = (double)model.getOriginWidth() / viewportWidth;
            double heightRatio = (double)model.getOriginHeight() / viewportHeight;
            int resultWidth = widthRatio >= heightRatio ? viewportWidth : (int)((double)model.getOriginWidth() / heightRatio);
            int resultHeight = heightRatio >= widthRatio ? viewportHeight : (int)((double)model.getOriginHeight() / widthRatio);
            model.getSettings().setResizeWidth(resultWidth);
            model.getSettings().setResizeHeight(resultHeight);
            model.setScale();
            model.applyChanges();
        };
        reduceToViewportButton.setOnAction(viewportSizeAction);
        reduceToViewportMenu.setOnAction(viewportSizeAction);

        EventHandler<ActionEvent> resetAction = (e) -> model.resetFilters();
        resetButton.setOnAction(resetAction);
        resetMenu.setOnAction(resetAction);

        setLastFilterAction();
    }

    private void setDialogListener(String fxmlName, Button button, MenuItem menu) {
        EventHandler<ActionEvent> action = (e) -> {
            if (!model.isImageOpened()) {
                return;
            }
            Platform.runLater(() -> new DialogWindow("/fxml/dialogs/" + fxmlName + ".fxml", model));
        };
        button.setOnAction(action);
        menu.setOnAction(action);
    }

    private void setFilterListener(Consumer<WritableImage> filterMethod, FiltersSettings.Filter type,
                                   Button button, MenuItem menu) {
        EventHandler<ActionEvent> action = (e) -> {
            if (!model.isImageOpened()) {
                return;
            }
            WritableImage imageToFilter = model.getImageToFilter();
            filterMethod.accept(imageToFilter);
            model.applyChanges();
            model.getSettings().setLastFilter(type);
        };
        button.setOnAction(action);
        menu.setOnAction(action);
    }

    private void setLastFilterAction() {
        lastFilterMenu.setOnAction((e) -> {
            WritableImage imageToFilter = model.getImageToFilter();
            switch (model.getSettings().getLastFilter()) {
                case GREYSCALE:
                    Filters.greyscale(imageToFilter);
                    break;
                case NEGATIVE:
                    Filters.negative(imageToFilter);
                    break;
                case GAUSSIAN:
                    Filters.gaussian(imageToFilter, model.getSettings().getGaussianSize());
                    break;
                case GAMMA:
                    Filters.gammaCorrection(imageToFilter, model.getSettings().getGamma());
                    break;
                case BRIGHTNESS:
                    Filters.brightness(imageToFilter, model.getSettings().getBrightness());
                    break;
                case EMBOSSING:
                    Filters.embossing(imageToFilter);
                    break;
                case SHARPENING:
                    Filters.sharpening(imageToFilter);
                    break;
                case FSD:
                    Filters.floydDithering(imageToFilter, model.getSettings().getFloydRedQuantity(),
                            model.getSettings().getFloydGreenQuantity(), model.getSettings().getFloydBlueQuantity());
                    break;
                case OD:
                    Filters.orderedDithering(imageToFilter, model.getSettings().getOrderedRedQuantity(),
                            model.getSettings().getOrderedGreenQuantity(), model.getSettings().getOrderedBlueQuantity());
                    break;
                case ROBERTS:
                    Filters.robertsEdge(imageToFilter, model.getSettings().getRobertsThreshold());
                    break;
                case SOBEL:
                    Filters.sobelEdge(imageToFilter, model.getSettings().getSobelThreshold());
                    break;
                case WATERCOLOR:
                    Filters.watercolor(imageToFilter, model.getSettings().getWatercolorSize());
                    break;
                case CONTRAST:
                    Filters.contrast(imageToFilter, model.getSettings().getContrast());
                    break;
                case SOLARISE:
                    Filters.solarise(imageToFilter, model.getSettings().getSolariseRedThreshold(),
                            model.getSettings().getSolariseGreenThreshold(), model.getSettings().getSolariseBlueThreshold());
            }
            model.applyChanges();
        });
    }
}
