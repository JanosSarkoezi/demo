package com.example.demo.ui;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;

public class OrthogonalArrow extends Group {
    private final Polyline line = new Polyline();
    private final Polygon tip = new Polygon(0,0, -10,-5, -10,5); // Einfache Pfeilspitze

    public OrthogonalArrow(Point2D start) {
        line.setStrokeWidth(2);
        getChildren().addAll(line, tip);
        updatePath(start, start.add(1, 1)); // Initial ganz kurz
    }

    public void updatePath(Point2D start, Point2D end) {
        line.getPoints().clear();

        // Die Logik für die L-Form (Horizontal dann Vertikal)
        // Punkt 1: Start
        line.getPoints().addAll(start.getX(), start.getY());
        // Punkt 2: Knickpunkt (X vom Ende, Y vom Start)
        line.getPoints().addAll(end.getX(), start.getY());
        // Punkt 3: Ende
        line.getPoints().addAll(end.getX(), end.getY());

        // Pfeilspitze am Ende positionieren und ausrichten
        tip.setTranslateX(end.getX());
        tip.setTranslateY(end.getY());
        // (Rotation der Spitze könnte man hier noch basierend auf der Richtung berechnen)
    }
}