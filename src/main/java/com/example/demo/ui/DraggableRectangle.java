package com.example.demo.ui;

import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class DraggableRectangle extends Rectangle {
    private final ShapeTransformHelper transformHelper;

    public DraggableRectangle(double x, double y, double width, double height, Color color, Pane boundsProvider, Group zoomGroup) {
        super(width, height, color);
        setX(x);
        setY(y);
        setStroke(Color.BLACK);

        RectangleAdapter adapter = new RectangleAdapter(this);

        // Die gesamte Logik wird hier delegiert
        this.transformHelper = new ShapeTransformHelper(adapter, zoomGroup);
    }

    public ShapeTransformHelper getTransformHelper() {
        return transformHelper;
    }
}