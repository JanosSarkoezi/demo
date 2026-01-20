package com.example.demo.tool.state;

import com.example.demo.diagram.shape.ShapeAdapter;
import com.example.demo.tool.SelectionTool;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;

public class ResizeState implements SelectionState {
    private final String handleName;

    public ResizeState(Rectangle handle) {
        // Wir extrahieren den Namen des Handles aus dem UserData
        this.handleName = (String) handle.getUserData();
    }

    @Override
    public void onMousePressed(MouseEvent event, SelectionTool tool, Group world) {
        // Initialisierung bereits durch Konstruktor erfolgt
        event.consume();
    }

    @Override
    public void onMouseDragged(MouseEvent event, SelectionTool tool, Group world) {
        Point2D mouseInWorld = world.sceneToLocal(event.getSceneX(), event.getSceneY());
        ShapeAdapter adapter = tool.getCurrentAdapter();

        if (adapter != null) {
            // Führt die Größenänderung im Adapter aus
            adapter.resize(handleName, mouseInWorld);
            // Aktualisiert Handles und Linien, da sich die Grenzen verschoben haben
            tool.updateUI();
        }

        event.consume();
    }

    @Override
    public void onMouseReleased(MouseEvent event, SelectionTool tool, Group world) {
        tool.setCurrentState(new IdleState());
    }
}