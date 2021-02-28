package ru.nsu.cg.buttons;

import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import resources.ResourceLoader;

import java.io.InputStream;

public abstract class ToolbarToggleButton extends ToggleButton {
    public ToolbarToggleButton(String iconFileName, String text, String tooltipText) {
        super();
        setMinSize(25, 25);
        setMaxSize(100, 40);
        setPrefSize(25, 25);
        InputStream iconStream = ResourceLoader.class.getResourceAsStream(iconFileName);
        if (iconStream != null) {
            Image buttonImg = new Image(iconStream,
                    20, 20, true, true);
            setGraphic(new ImageView(buttonImg));
        } else {
            setText(text);
        }
        Tooltip tooltip = new Tooltip(tooltipText);
        tooltip.setFont(new Font(12));
        setTooltip(tooltip);
        setFocusTraversable(false);
    }
}
