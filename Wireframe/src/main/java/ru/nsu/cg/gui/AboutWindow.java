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
    private static final String text = "ICGWireframe by Nikita Pogodaev, 18203 group, Novosibirsk State University\r\n\r\n" +
            "Usage:\r\n" +
            "1. Rotate a wireframe object using mouse, zoom with mouse scroll,\r\n" +
            "use init button to reset all rotations\r\n" +
            "2. Build a curve using circles (generatrix points) in settings dialog window,\r\n" +
            "add or remove generatrix points using add or remove buttons if necessary.\r\n" +
            "3. Settings parameters: m - length grid size, n - axis grid size,\r\n" +
            "k - axis wireframe smoothness, tN - curve smoothness, (R,G,B) - wireframe color\r\n" +
            "4. Save/Open a model using save/open buttons, model file format is .wfm";
    public AboutWindow() {
        super("About", "/fxml/aboutWindow.fxml", text, 450, 450);
    }
}
