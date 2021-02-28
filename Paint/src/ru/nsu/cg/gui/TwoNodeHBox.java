package ru.nsu.cg.gui;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;

public class TwoNodeHBox extends HBox {
    public TwoNodeHBox(Node node1, Node node2) {
        super(node1, node2);
        setSpacing(10);
        setAlignment(Pos.CENTER);
    }
}
