package com.example.demo.util;

import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Scale;

public class CanvasCamera {
    private final Pane frame;
    private final Group content;
    private final Scale zoomTransform = new Scale(1, 1, 0, 0);

    private double anchorX;
    private double anchorY;

    public CanvasCamera(Pane frame, Group content) {
        this.frame = frame;
        this.content = content;
        this.content.getTransforms().add(zoomTransform);
    }

    // --- ZOOM LOGIK ---
    public void handleZoom(ScrollEvent event) {
        double deltaY = event.getDeltaY();
        if (deltaY == 0.0 || !event.isControlDown()) return;

        double zoomFactor = (deltaY > 0) ? 1.1 : 0.9;
        double newScale = zoomTransform.getX() * zoomFactor;

        // Math.clamp ist neu seit Java 21!
        newScale = Math.clamp(newScale, 0.2, 10.0);

        // Tatsächlicher Zoom-Faktor (kann wegen Clamp abweichen)
        double actualFactor = newScale / zoomTransform.getX();

        double mouseX = event.getX();
        double mouseY = event.getY();

        // Punkt unter Maus stabil halten
        content.setTranslateX(mouseX - (mouseX - content.getTranslateX()) * actualFactor);
        content.setTranslateY(mouseY - (mouseY - content.getTranslateY()) * actualFactor);

        zoomTransform.setX(newScale);
        zoomTransform.setY(newScale);

        event.consume();
    }

    // --- PANNING LOGIK ---
    public void startPan(MouseEvent event) {
        anchorX = event.getX();
        anchorY = event.getY();
    }

    public void updatePan(MouseEvent event) {
        double deltaX = event.getX() - anchorX;
        double deltaY = event.getY() - anchorY;

        content.setTranslateX(content.getTranslateX() + deltaX);
        content.setTranslateY(content.getTranslateY() + deltaY);

        anchorX = event.getX();
        anchorY = event.getY();
    }

    public double getScale() {
        return zoomTransform.getX();
    }
}