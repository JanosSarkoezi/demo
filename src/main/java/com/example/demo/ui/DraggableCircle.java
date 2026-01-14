package com.example.demo.ui;

import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class DraggableCircle extends Circle {
    private final ShapeTransformHelper transformHelper;

    public DraggableCircle(double x, double y, double radius, Color color, Pane boundsProvider, Group zoomGroup) {
        super(radius, color);
        setCenterX(x);
        setCenterY(y);
        setStroke(Color.BLACK);

        // Delegierung an den Helper
        this.transformHelper = new ShapeTransformHelper(this, boundsProvider, zoomGroup);
    }

    public ShapeTransformHelper getTransformHelper() {
        return transformHelper;
    }
}