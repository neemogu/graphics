package ru.nsu.cg.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class CenteredLabelBox extends HBox {
    public CenteredLabelBox(Label label) {
        super(label);
        setMargin(label, new Insets(10, 10, 10, 10));
        setAlignment(Pos.BASELINE_CENTER);
    }
}
