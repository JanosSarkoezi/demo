package com.example.demo.tool;

import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Shape;

public class MoveTool implements Tool {
    private javafx.scene.Node target = null;
    private Point2D currentMouseInWorld = null;

    // Anker-Variablen für stabiles Verschieben/Snapping
    private double anchorX;
    private double anchorY;

    private final double gridSize = 40.0;

    @Override
    public String getName() {
        return "MOVE & PAN (Objekt oder Welt)";
    }

    @Override
    public void handle(MouseEvent event, Pane canvas, Group world) {
        currentMouseInWorld = world.sceneToLocal(event.getSceneX(), event.getSceneY());

        if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
            handleMousePressed(event, canvas, world);
        } else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED && target != null) {
            handleMouseDragged(event, canvas, world);
        } else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
            handleMouseReleased(event, canvas, world);
        }
    }

    private void handleMousePressed(MouseEvent event, Pane canvas, Group world) {
        if (event.getTarget() instanceof Shape s) {
            target = s;
            // ANKER: Abstand vom Mauszeiger zum Ursprung des Objekts in Welt-Koordinaten
            anchorX = target.getTranslateX() - currentMouseInWorld.getX();
            anchorY = target.getTranslateY() - currentMouseInWorld.getY();

            target.setCursor(Cursor.CLOSED_HAND);
            target.toFront();
        } else {
            target = world;
            // Für das Panning speichern wir den Klick-Punkt in Scene-Koordinaten
            anchorX = event.getSceneX() - world.getTranslateX();
            anchorY = event.getSceneY() - world.getTranslateY();
            canvas.setCursor(Cursor.CLOSED_HAND);
        }
        event.consume();
    }

    private void handleMouseDragged(MouseEvent event, Pane canvas, Group world) {
        if (target == world) {
            // PANNING: Absolute Positionierung der Welt relativ zur Scene
            target.setTranslateX(event.getSceneX() - anchorX);
            target.setTranslateY(event.getSceneY() - anchorY);
        } else {
            // OBJEKT-MOVE: Theoretische neue Position basierend auf Anker
            double rawX = currentMouseInWorld.getX() + anchorX;
            double rawY = currentMouseInWorld.getY() + anchorY;

            // SNAP-LOGIK: Wir runden die Zielposition, nicht das Delta
            double snappedX = Math.round(rawX / gridSize) * gridSize;
            double snappedY = Math.round(rawY / gridSize) * gridSize;

            target.setTranslateX(snappedX);
            target.setTranslateY(snappedY);
        }
    }

    private void handleMouseReleased(MouseEvent event, Pane canvas, Group world) {
        if (target != null) {
            target.setCursor(Cursor.DEFAULT);
        }
        canvas.setCursor(Cursor.DEFAULT);
        target = null;
    }
}