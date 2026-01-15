package com.example.demo.ui;

import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.shape.Rectangle;

import java.util.LinkedHashMap;
import java.util.Map;

public class RectangleAdapter implements ShapeAdapter {
    private final Rectangle rect;

    public RectangleAdapter(Rectangle rect) { this.rect = rect; }

    @Override public Rectangle getShape() { return rect; }
    @Override public Point2D getPosition() { return new Point2D(rect.getX(), rect.getY()); }
    @Override public void setPosition(double x, double y) { rect.setX(x); rect.setY(y); }
    @Override public double getWidth() { return rect.getWidth(); }
    @Override public double getHeight() { return rect.getHeight(); }
    @Override public void setWidth(double w) { rect.setWidth(w); }
    @Override public void setHeight(double h) { rect.setHeight(h); }

    @Override
    public Point2D getCenter() {
        return new Point2D(rect.getX() + rect.getWidth() / 2, rect.getY() + rect.getHeight() / 2);
    }

    @Override
    public void setCenter(double centerX, double centerY) {
        rect.setX(centerX - rect.getWidth() / 2);
        rect.setY(centerY - rect.getHeight() / 2);
    }

    @Override
    public Map<String, Cursor> getHandles() {
        Map<String, Cursor> map = new LinkedHashMap<>();
        map.put("NW", Cursor.NW_RESIZE);
        map.put("N",  Cursor.N_RESIZE);
        map.put("NE", Cursor.NE_RESIZE);
        map.put("W",  Cursor.W_RESIZE);
        map.put("E",  Cursor.E_RESIZE);
        map.put("SW", Cursor.SW_RESIZE);
        map.put("S",  Cursor.S_RESIZE);
        map.put("SE", Cursor.SE_RESIZE);
        return map;
    }

    @Override
    public Point2D getHandlePosition(String handleName) {
        double x = rect.getX();
        double y = rect.getY();
        double w = rect.getWidth();
        double h = rect.getHeight();

        return switch (handleName) {
            case "NW" -> new Point2D(x, y);
            case "N"  -> new Point2D(x + w / 2, y);
            case "NE" -> new Point2D(x + w, y);
            case "W"  -> new Point2D(x, y + h / 2);
            case "E"  -> new Point2D(x + w, y + h / 2);
            case "SW" -> new Point2D(x, y + h);
            case "S"  -> new Point2D(x + w / 2, y + h);
            case "SE" -> new Point2D(x + w, y + h);
            default -> throw new IllegalArgumentException("Unknown handle: " + handleName);
        };
    }
}
