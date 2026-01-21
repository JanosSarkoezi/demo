package com.example.demo.tool.state;

import com.example.demo.diagram.shape.ShapeAdapter;
import com.example.demo.model.ResizeCommand;
import com.example.demo.tool.SelectionTool;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;

public class ResizeState implements SelectionState {
    private final String handleName;

    // Merker für den Ausgangszustand
    private Point2D startPos;
    private double startW;
    private double startH;

    public ResizeState(Rectangle handle) {
        this.handleName = (String) handle.getUserData();
    }

    @Override
    public void onMousePressed(MouseEvent event, SelectionTool tool, Group world) {
        ShapeAdapter adapter = tool.getCurrentAdapter();
        if (adapter != null) {
            // Zustand VOR der Änderung sichern
            this.startPos = adapter.getPosition();
            this.startW = adapter.getWidth();
            this.startH = adapter.getHeight();
        }
        event.consume();
    }

    @Override
    public void onMouseDragged(MouseEvent event, SelectionTool tool, Group world) {
        Point2D mouseInWorld = world.sceneToLocal(event.getSceneX(), event.getSceneY());
        ShapeAdapter adapter = tool.getCurrentAdapter();

        if (adapter != null) {
            adapter.resize(handleName, mouseInWorld);
            tool.updateUI();
        }
        event.consume();
    }

    @Override
    public void onMouseReleased(MouseEvent event, SelectionTool tool, Group world) {
        ShapeAdapter adapter = tool.getCurrentAdapter();

        if (adapter != null) {
            Point2D endPos = adapter.getPosition();
            double endW = adapter.getWidth();
            double endH = adapter.getHeight();

            // Nur speichern, wenn sich wirklich etwas geändert hat
            if (startW != endW || startH != endH || !startPos.equals(endPos)) {
                ResizeCommand resizeCmd = new ResizeCommand(
                        adapter,
                        startPos, startW, startH,
                        endPos, endW, endH
                );
                tool.getSelectionModel().getHistory().executeCommand(resizeCmd);
            }
        }

        tool.setCurrentState(new IdleState());
        event.consume();
    }
}