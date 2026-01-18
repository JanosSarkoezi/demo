package com.example.demo.tool;

import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public interface Tool {
    default void onActivate(Pane canvas, Group world) {}
    default void onDeactivate(Pane canvas, Group world) {}

    default void onMousePressed(MouseEvent e, Pane canvas, Group world) {}
    default void onMouseDragged(MouseEvent e, Pane canvas, Group world) {}
    default void onMouseReleased(MouseEvent e, Pane canvas, Group world) {}

    String getName();
}