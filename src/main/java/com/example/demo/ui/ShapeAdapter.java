package com.example.demo.ui;

import javafx.geometry.Point2D;
import javafx.scene.shape.Shape;
import javafx.scene.Cursor;

import java.util.List;
import java.util.Map;

public interface ShapeAdapter {

    Shape getShape();

    Point2D getPosition();
    void setPosition(double x, double y);

    double getWidth();
    double getHeight();
    void setWidth(double w);
    void setHeight(double h);

    /** Liefert die Handle-Positionen: Map<HandleName, Cursor> */
    Map<String, Cursor> getHandles();
    /** Liefert die Namen der verfügbaren Andockstellen (z.B. "N", "S", "E", "W") */
    List<String> getConnectionPointNames();
    /** Liefert die aktuelle Position einer Andockstelle basierend auf ihrem Namen */
    Point2D getConnectionPointPosition(String name);

    Point2D getCenter();
    void setCenter(double centerX, double centerY);
    void resize(String handleName, Point2D mousePos);

    /** Liefert die Position eines Handles (in lokalen Shape-Koordinaten) */
    Point2D getHandlePosition(String handleName);
}
