package ru.nsu.cg.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ru.nsu.cg.controllers.MessageWindowController;

import java.io.IOException;

public class AboutWindow extends InfoWindow {
    private static final String text = "ICGFilter by Nikita Pogodaev, 18203 group, Novosibirsk State University\r\n\r\n" +
            "Usage:\r\n" +
            "1. Open your image and then pick a filter.\r\n" +
            "2. Choose parameters for any filter in dialog windows.\r\n" +
            "3. Click on reset button to reset all filters from your image.\r\n" +
            "4. You can double click on image to switch between original and filtered images\r\n" +
            "5. Click on save button to save filtered image.\r\n" +
            "6. You can resize an image to viewport size, to original size or any size you want.\r\n" +
            "7. Also you can rotate your image with \"rotate\" option";
    public AboutWindow() {
        super("About", "/fxml/aboutWindow.fxml", text, 450, 450);
    }
}
