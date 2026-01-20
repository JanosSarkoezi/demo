package com.example.demo.tool;

import com.example.demo.controller.ToolbarController.ToolType; // Enum importieren
import com.example.demo.model.SelectionModel;
import com.example.demo.tool.state.DrawIdleState;
import com.example.demo.tool.state.DrawState;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class DrawTool implements Tool {
    private final ToolType toolType;
    private final SelectionModel selectionModel;
    private DrawState currentState = new DrawIdleState();

    public DrawTool(ToolType toolType, SelectionModel selectionModel) {
        this.toolType = toolType;
        this.selectionModel = selectionModel;
    }

    public void setCurrentState(DrawState state) {
        this.currentState = state;
    }

    @Override
    public void onMousePressed(MouseEvent e, Pane c, Group w) {
        currentState.onMousePressed(e, this, c, w);
    }

    @Override
    public void onMouseDragged(MouseEvent e, Pane c, Group w) {
        currentState.onMouseDragged(e, this, c, w);
    }

    @Override
    public void onMouseReleased(MouseEvent e, Pane c, Group w) {
        currentState.onMouseReleased(e, this, c, w);
    }

    // Getter für die States
    public ToolType getToolType() { return toolType; }
    public SelectionModel getSelectionModel() { return selectionModel; }

    @Override
    public String getName() {
        return "DrawTool";
    }
}