package com.example.demo.util;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class DraggableRectangle extends Rectangle {
    private double dragAnchorX, dragAnchorY;
    private final double gridSpacing = 50.0; // Rasterweite

    public DraggableRectangle(double x, double y, double width, double height, Color color, Pane boundsProvider, Group zoomGroup) {
        super(width, height, color);
        // Positionierung (obere linke Ecke)
        setX(x);
        setY(y);
        setStroke(Color.BLACK);

        setOnMousePressed(e -> {
            // Wir berechnen den Abstand vom Mauszeiger zur oberen linken Ecke (x, y)
            dragAnchorX = getX()- e.getX();
            dragAnchorY = getY() - e.getY();
            toFront();
            e.consume();
        });

        setOnMouseDragged(e -> {
            // 1. Vorläufige neue Position der oberen linken Ecke
            double newX = e.getX() + dragAnchorX;
            double newY = e.getY() + dragAnchorY;

            // 2. Snap-to-Grid Logik basierend auf der MITTE
            // Wir berechnen die Mitte, runden diese auf das Raster und
            // setzen dann das X/Y wieder zurück auf die Ecke.
            double centerX = newX + getWidth() / 2.0;
            double centerY = newY + getHeight() / 2.0;

            double snappedCenterX = Math.round(centerX / gridSpacing) * gridSpacing;
            double snappedCenterY = Math.round(centerY / gridSpacing) * gridSpacing;

            newX = snappedCenterX - getWidth() / 2.0;
            newY = snappedCenterY - getHeight() / 2.0;

            // 3. Dynamisches Clamping (Sichtbereich einhalten)
            Point2D topLeft = zoomGroup.parentToLocal(0, 0);
            Point2D bottomRight = zoomGroup.parentToLocal(boundsProvider.getWidth(), boundsProvider.getHeight());

            setX(Math.clamp(newX, topLeft.getX(), bottomRight.getX() - getWidth()));
            setY(Math.clamp(newY, topLeft.getY(), bottomRight.getY() - getHeight()));

            e.consume();
        });
    }
}