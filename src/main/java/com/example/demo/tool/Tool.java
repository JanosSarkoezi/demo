package com.example.demo.tool;

import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public interface Tool {
    void handle(MouseEvent event, Pane canvas, Group world);

    String getName();
}