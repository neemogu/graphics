package ru.nsu.cg.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class AboutWindow extends Stage {
    private static final String text = "ICGPaint by Nikita Pogodaev, 18203 group, Novosibirsk State University\r\n\r\n" +
            "Usage:\r\n1. Pick tools using special buttons on the toolbar or in the menu: LINE - draw a line with your mouse," +
            " STAMP - draw a polygon or a star with one click, FILL - click to fill pixel area with picked color\r\n" +
            "2. Select color using color picker on toolbar\r\n3. You can clear your workspace using clear button\r\n" +
            "4. Also explore tool's and workspace settings using Settings button\r\n5. Save and open workspaces using " +
            "Save and Open buttons on the toolbar or in the menu";
    public AboutWindow() {
        super();
        setTitle("About");
        initStyle(StageStyle.UTILITY);
        centerOnScreen();
        initModality(Modality.APPLICATION_MODAL);
        Text text = new Text();
        text.setWrappingWidth(400);
        text.setText(AboutWindow.text);
        text.setFont(new Font(16));
        VBox.setMargin(text, new Insets(15, 15, 15, 15));
        VBox root = new VBox(text);
        root.setAlignment(Pos.CENTER);
        Scene scene = new Scene(root);
        setScene(scene);
    }
}
