package com.example.demo.util;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class DraggableCircle extends Circle {
    private double anchorX, anchorY;

    public DraggableCircle(double x, double y, double radius, Color color, Pane boundsProvider, Group zoomGroup) {
        super(radius, color);
        setCenterX(x);
        setCenterY(y);
        setStroke(Color.BLACK);

        setOnMousePressed(e -> {
            anchorX = getCenterX() - e.getX();
            anchorY = getCenterY() - e.getY();
            toFront();
            e.consume();
        });

        setOnMouseDragged(e -> {
            double newX = e.getX() + anchorX;
            double newY = e.getY() + anchorY;

            // Deine Clamping-Logik ist hier sicher "verstaut"
            Point2D topLeft = zoomGroup.parentToLocal(0, 0);
            Point2D bottomRight = zoomGroup.parentToLocal(boundsProvider.getWidth(), boundsProvider.getHeight());

            setCenterX(Math.clamp(newX, topLeft.getX() + getRadius(), bottomRight.getX() - getRadius()));
            setCenterY(Math.clamp(newY, topLeft.getY() + getRadius(), bottomRight.getY() - getRadius()));

            e.consume();
        });
    }
}