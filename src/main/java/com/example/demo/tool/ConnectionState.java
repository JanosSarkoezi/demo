package com.example.demo.tool;

import com.example.demo.ui.SmartConnection;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;

public class ConnectionState implements SelectionState {
    private final Circle startDot;
    private SmartConnection activeConnection;

    public ConnectionState(Circle startDot) {
        this.startDot = startDot;
    }

    @Override
    public void onMousePressed(MouseEvent event, SelectionTool tool, Group world) {
        Point2D startPos = world.sceneToLocal(event.getSceneX(), event.getSceneY());

        // Initialisierung der Verbindung
        activeConnection = new SmartConnection(startPos, startPos);
        activeConnection.setStartAdapter(tool.getCurrentAdapter());
        activeConnection.setStartPointName((String) startDot.getProperties().get("pointName"));

        // WICHTIG: Macht die Linie für die Maus unsichtbar, damit der Ziel-Dot getroffen wird
        activeConnection.setMouseTransparent(true);

        world.getChildren().add(activeConnection);
    }

    @Override
    public void onMouseDragged(MouseEvent event, SelectionTool tool, Group world) {
        Point2D mousePos = world.sceneToLocal(event.getSceneX(), event.getSceneY());
        Point2D startPos = activeConnection.getStartAdapter()
                .getConnectionPointPosition(activeConnection.getStartPointName());

        activeConnection.updatePoints(startPos, mousePos);

        // Prüfen, ob wir über einem Ziel schweben (für visuelles Feedback)
        tool.checkTargetHover(event, world);
    }

    @Override
    public void onMouseReleased(MouseEvent event, SelectionTool tool, Group world) {
        Point2D mouseInWorld = world.sceneToLocal(event.getSceneX(), event.getSceneY());
        Circle hitDot = tool.findDotAt(mouseInWorld);

        if (hitDot != null && tool.getCurrentAdapter() != activeConnection.getStartAdapter()) {
            // Verbindung finalisieren
            activeConnection.setEndAdapter(tool.getCurrentAdapter());
            activeConnection.setEndPointName((String) hitDot.getProperties().get("pointName"));

            activeConnection.setMouseTransparent(false); // Wieder anklickbar machen
            tool.getSelectionModel().addConnection(activeConnection);
            tool.getSelectionModel().setStatusMessage("Verbunden!");
        } else {
            world.getChildren().remove(activeConnection);
        }

        tool.setCurrentState(new IdleState()); // Zurück in den Wartemodus
    }
}