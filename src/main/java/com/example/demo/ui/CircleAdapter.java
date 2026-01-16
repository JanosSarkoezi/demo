package com.example.demo.ui;

import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.shape.Circle;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CircleAdapter implements ShapeAdapter {
    private final Circle circle;

    public CircleAdapter(Circle circle) { this.circle = circle; }

    @Override public Circle getShape() { return circle; }
    @Override public Point2D getPosition() { return new Point2D(circle.getCenterX(), circle.getCenterY()); }
    @Override public void setPosition(double x, double y) { circle.setCenterX(x); circle.setCenterY(y); }
    @Override public double getWidth() { return circle.getRadius() * 2; }
    @Override public double getHeight() { return circle.getRadius() * 2; }
    @Override public void setWidth(double w) { circle.setRadius(w / 2); }
    @Override public void setHeight(double h) { circle.setRadius(h / 2); }

    @Override
    public Point2D getCenter() {
        return new Point2D(circle.getCenterX(), circle.getCenterY());
    }

    @Override
    public void setCenter(double centerX, double centerY) {
        circle.setCenterX(centerX);
        circle.setCenterY(centerY);
    }

    // In CircleAdapter.java
    @Override
    public void resize(String handleName, Point2D p) {
        double dx = p.getX() - circle.getCenterX();
        double dy = p.getY() - circle.getCenterY();
        circle.setRadius(Math.max(5.0, Math.sqrt(dx * dx + dy * dy)));
    }

    @Override
    public List<String> getHandleNames() {
        return List.of("N", "W", "E", "S");
    }

    @Override
    public Cursor getHandleCursor(String name) {
        return switch (name) {
            case "NW", "SE" -> Cursor.NW_RESIZE;
            case "NE", "SW" -> Cursor.NE_RESIZE;
            case "N", "S"   -> Cursor.N_RESIZE;
            case "E", "W"   -> Cursor.E_RESIZE;
            default -> Cursor.DEFAULT;
        };
    }

    @Override
    public Point2D getHandlePosition(String handleName) {
        double cx = circle.getCenterX();
        double cy = circle.getCenterY();
        double r  = circle.getRadius();

        return switch (handleName) {
            case "N" -> new Point2D(cx, cy - r);
            case "S" -> new Point2D(cx, cy + r);
            case "E" -> new Point2D(cx + r, cy);
            case "W" -> new Point2D(cx - r, cy);
            default -> throw new IllegalArgumentException("Unknown handle: " + handleName);
        };
    }

    @Override
    public List<String> getConnectionPointNames() {
        return List.of("N", "S", "E", "W");
    }

    @Override
    public Point2D getConnectionPointPosition(String name) {
        double cx = circle.getCenterX(); //
        double cy = circle.getCenterY(); //
        double r  = circle.getRadius();  //

        return switch (name) {
            case "N" -> new Point2D(cx, cy - r);
            case "S" -> new Point2D(cx, cy + r);
            case "E" -> new Point2D(cx + r, cy);
            case "W" -> new Point2D(cx - r, cy);
            default -> throw new IllegalArgumentException("Unbekannter Punkt: " + name);
        };
    }
}
