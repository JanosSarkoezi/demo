package com.example.demo.tool;

import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Shape;

public class MoveTool implements Tool {
    private javafx.scene.Node target = null;
    private Point2D lastMouseInWorld = null;
    private Point2D currentMouseInWorld = null;

    @Override
    public String getName() {
        return "MOVE & PAN (Objekt oder Welt)";
    }

    @Override
    public void handle(MouseEvent event, Pane canvas, Group world) {
        // Wir rechnen die aktuelle Mausposition IMMER in Welt-Koordinaten um
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
        lastMouseInWorld = currentMouseInWorld;

        if (event.getTarget() instanceof Shape s) {
            target = s;
            target.setCursor(Cursor.CLOSED_HAND);
        } else {
            target = world;
            canvas.setCursor(Cursor.CLOSED_HAND);
        }
        event.consume();
    }

    private void handleMouseDragged(MouseEvent event, Pane canvas, Group world) {
        // Delta im Welt-Koordinatensystem (Zoom/Pan bereits "herausgerechnet")
        double deltaX = currentMouseInWorld.getX() - lastMouseInWorld.getX();
        double deltaY = currentMouseInWorld.getY() - lastMouseInWorld.getY();

        if (target == world) {
            // BEI PANNING: Hier müssen wir einen Trick anwenden.
            // Da wir die Welt selbst bewegen, während wir ihre Koordinaten messen,
            // nutzen wir für das Panning besser die Scene-Deltas.
            double sceneDeltaX = event.getSceneX() - world.localToScene(lastMouseInWorld).getX();
            double sceneDeltaY = event.getSceneY() - world.localToScene(lastMouseInWorld).getY();

            target.setTranslateX(target.getTranslateX() + sceneDeltaX);
            target.setTranslateY(target.getTranslateY() + sceneDeltaY);
        } else {
            // BEI OBJEKTEN: Das Welt-Delta funktioniert perfekt!
            target.setTranslateX(target.getTranslateX() + deltaX);
            target.setTranslateY(target.getTranslateY() + deltaY);
        }

        // WICHTIG: Nach der Bewegung die Position neu berechnen
        lastMouseInWorld = world.sceneToLocal(event.getSceneX(), event.getSceneY());
    }

    private void handleMouseReleased(MouseEvent event, Pane canvas, Group world) {
        target.setCursor(Cursor.DEFAULT);
        canvas.setCursor(Cursor.DEFAULT);

        target = null;
    }
}