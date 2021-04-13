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
    private static final String text = "ICGMap by Nikita Pogodaev, 18203 group, Novosibirsk State University\r\n\r\n" +
            "Usage:\r\n" +
            "1. Choose properties by using settings or by opening a txt file.\r\n" +
            "2. Use show/hide buttons to display or hide isolines and grid.\r\n" +
            "3. Use display mode buttons to change display mode between smooth and discrete color maps.\r\n" +
            "4. Click on any point on image to show a dynamic isoline\r\n" +
            "5. Save result image by using save button\r\n\r\n" +
            "Properties file format:\r\n\r\n" +
            "startX endX startY endY\r\n" +
            "gridWidth gridHeight\r\n" +
            "isolinesCount\r\n" +
            "//red, green, blue is [0, 255]\r\n" +
            "R0 G0 B0\r\n" +
            "...\r\n" +
            "Rk Gk Bk // k is isolinesCount\r\n" +
            "//isolines color\r\n" +
            "Rz Gz Bz\r\n";
    public AboutWindow() {
        super("About", "/fxml/aboutWindow.fxml", text, 450, 450);
    }
}
