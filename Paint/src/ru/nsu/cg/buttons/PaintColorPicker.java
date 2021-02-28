package ru.nsu.cg.buttons;

import javafx.scene.control.ColorPicker;
import javafx.scene.control.Tooltip;
import javafx.scene.text.Font;

public class PaintColorPicker extends ColorPicker {
    public PaintColorPicker() {
        super();
        Tooltip tooltip = new Tooltip("Pick a color");
        tooltip.setFont(new Font(12));
        setTooltip(tooltip);
        setFocusTraversable(false);
    }
}
