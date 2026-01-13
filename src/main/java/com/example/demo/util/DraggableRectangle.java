package com.example.demo.util;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class DraggableRectangle extends Rectangle {
    private double dragAnchorX, dragAnchorY;

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
            double newX = e.getX() + dragAnchorX;
            double newY = e.getY() + dragAnchorY;

            // Dynamisches Clamping basierend auf dem sichtbaren Bereich
            Point2D topLeft = zoomGroup.parentToLocal(0, 0);
            Point2D bottomRight = zoomGroup.parentToLocal(boundsProvider.getWidth(), boundsProvider.getHeight());

            // Achtung: Beim Rechteck müssen wir die Breite/Höhe für die rechte/untere Grenze abziehen
            setX(Math.clamp(newX, topLeft.getX(), bottomRight.getX() - getWidth()));
            setY(Math.clamp(newY, topLeft.getY(), bottomRight.getY() - getHeight()));

            e.consume();
        });
    }
}