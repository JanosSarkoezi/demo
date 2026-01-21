package com.example.demo.tool.state;

import com.example.demo.diagram.shape.ShapeAdapter;
import com.example.demo.model.MoveCommand;
import com.example.demo.tool.SelectionTool;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;

public class MoveState implements SelectionState {
    private final ShapeAdapter adapter;
    private Point2D startCenter;

    public MoveState(ShapeAdapter adapter) {
        this.adapter = adapter; // Jetzt ist der Adapter sicher vorhanden!
    }

    @Override
    public void onMousePressed(MouseEvent event, SelectionTool tool, Group world) {
        // Initialisierung (Anker berechnen)
        Point2D mouseInWorld = world.sceneToLocal(event.getSceneX(), event.getSceneY());
        startCenter = adapter.getCenter();

        // Wir speichern die Ankerwerte direkt im Tool, damit sie konsistent bleiben
        tool.setAnchorX(startCenter.getX() - mouseInWorld.getX());
        tool.setAnchorY(startCenter.getY() - mouseInWorld.getY());

        event.consume();
    }

    @Override
    public void onMouseDragged(MouseEvent event, SelectionTool tool, Group world) {
        Point2D mouseInWorld = world.sceneToLocal(event.getSceneX(), event.getSceneY());

        // Hier passierte der Fehler – jetzt nutzen wir den sicheren 'adapter'
        double rawX = mouseInWorld.getX() + tool.getAnchorX();
        double rawY = mouseInWorld.getY() + tool.getAnchorY();

        double gridSize = 40.0;
        adapter.setCenter(
                Math.round(rawX / gridSize) * gridSize,
                Math.round(rawY / gridSize) * gridSize
        );

        tool.updateUI();
        event.consume();
    }

    @Override
    public void onMouseReleased(MouseEvent event, SelectionTool tool, Group world) {
        Point2D endCenter = adapter.getCenter();

        if (!endCenter.equals(startCenter)) {
            MoveCommand moveCmd = new MoveCommand(adapter, startCenter, endCenter);
            tool.getSelectionModel().getHistory().executeCommand(moveCmd);
        }

        tool.setCurrentState(new IdleState());
        event.consume();
    }
}