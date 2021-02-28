package ru.nsu.cg.gui;

import javafx.scene.control.Label;
import javafx.scene.text.Font;

public class SettingLabel extends Label {
    public SettingLabel(String setting, int fontSize) {
        super(setting);
        setFont(new Font(fontSize));
    }
}
