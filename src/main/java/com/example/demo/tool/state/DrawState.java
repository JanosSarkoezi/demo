package com.example.demo.tool.state;

import com.example.demo.tool.DrawTool;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public interface DrawState {
    void onMousePressed(MouseEvent e, DrawTool tool, Pane c, Group w);
    void onMouseDragged(MouseEvent e, DrawTool tool, Pane c, Group w);
    void onMouseReleased(MouseEvent e, DrawTool tool, Pane c, Group w);
}