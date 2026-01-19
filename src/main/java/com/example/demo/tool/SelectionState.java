package com.example.demo.tool;

import javafx.scene.Group;
import javafx.scene.input.MouseEvent;

public interface SelectionState {
    void onMousePressed(MouseEvent event, SelectionTool tool, Group world);
    void onMouseDragged(MouseEvent event, SelectionTool tool, Group world);
    void onMouseReleased(MouseEvent event, SelectionTool tool, Group world);
}