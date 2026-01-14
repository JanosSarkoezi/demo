package com.example.demo.ui;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import java.util.ArrayList;
import java.util.List;

public class ShapeTransformHelper {
    private final Shape shape;
    private final Pane boundsProvider;
    private final Group zoomGroup;

    private double anchorX, anchorY;
    private final List<Rectangle> handleShapes = new ArrayList<>();
    private static final double HANDLE_SIZE = 8.0;
    private static final double GRID_SPACING = 50.0;
    private static final double MIN_DIMENSION = 10.0;
    private boolean handlesVisible = false;

    public ShapeTransformHelper(Shape shape, Pane boundsProvider, Group zoomGroup) {
        this.shape = shape;
        this.boundsProvider = boundsProvider;
        this.zoomGroup = zoomGroup;
        setupInteractions();
    }

    private void setupInteractions() {
        shape.setOnMousePressed(e -> {
            if (e.isControlDown()) {
                toggleHandles();
            } else {
                hideHandles();
                // Initialer Ankerpunkt berechnen
                if (shape instanceof Rectangle r) {
                    anchorX = r.getX() - e.getX();
                    anchorY = r.getY() - e.getY();
                } else if (shape instanceof Circle c) {
                    anchorX = c.getCenterX() - e.getX();
                    anchorY = c.getCenterY() - e.getY();
                }
                shape.toFront();
            }
            e.consume();
        });

        shape.setOnMouseDragged(e -> {
            if (!e.isControlDown()) {
                double rawX = e.getX() + anchorX;
                double rawY = e.getY() + anchorY;

                applySnappedAndClampedTranslation(rawX, rawY);

                if (handlesVisible) updateHandlePositions();
            }
            e.consume();
        });
    }

    private void applySnappedAndClampedTranslation(double x, double y) {
        Point2D topLeft = zoomGroup.parentToLocal(0, 0);
        Point2D bottomRight = zoomGroup.parentToLocal(boundsProvider.getWidth(), boundsProvider.getHeight());

        if (shape instanceof Rectangle r) {
            // Snapping: Mitte des Rechtecks am Gitter ausrichten
            double centerX = x + r.getWidth() / 2;
            double centerY = y + r.getHeight() / 2;
            double snappedX = Math.round(centerX / GRID_SPACING) * GRID_SPACING - r.getWidth() / 2;
            double snappedY = Math.round(centerY / GRID_SPACING) * GRID_SPACING - r.getHeight() / 2;

            // Clamping: Ganze Breite/Höhe berücksichtigen
            r.setX(Math.clamp(snappedX, topLeft.getX(), bottomRight.getX() - r.getWidth()));
            r.setY(Math.clamp(snappedY, topLeft.getY(), bottomRight.getY() - r.getHeight()));

        } else if (shape instanceof Circle c) {
            // Snapping: Zentrum am Gitter ausrichten
            double snappedX = Math.round(x / GRID_SPACING) * GRID_SPACING;
            double snappedY = Math.round(y / GRID_SPACING) * GRID_SPACING;

            // Clamping: Radius berücksichtigen, damit der Kreis im Bild bleibt
            c.setCenterX(Math.clamp(snappedX, topLeft.getX() + c.getRadius(), bottomRight.getX() - c.getRadius()));
            c.setCenterY(Math.clamp(snappedY, topLeft.getY() + c.getRadius(), bottomRight.getY() - c.getRadius()));
        }
    }

    private void toggleHandles() {
        if (handlesVisible) hideHandles();
        else showHandles();
        handlesVisible = !handlesVisible;
    }

    private void showHandles() {
        String[] positions = (shape instanceof Rectangle)
                ? new String[]{"NW", "N", "NE", "W", "E", "SW", "S", "SE"}
                : new String[]{"N", "S", "E", "W"};

        for (String pos : positions) {
            createHandle(pos);
        }
        updateHandlePositions();
    }

    private void createHandle(String pos) {
        Rectangle h = new Rectangle(HANDLE_SIZE, HANDLE_SIZE, Color.WHITE);
        h.setStroke(shape instanceof Circle ? Color.DARKRED : Color.DARKBLUE);
        h.setUserData(pos);

        // Anti-Zoom Skalierung bleibt erhalten
        h.scaleXProperty().bind(zoomGroup.scaleXProperty().map(s -> 1.0 / s.doubleValue()));
        h.scaleYProperty().bind(zoomGroup.scaleYProperty().map(s -> 1.0 / s.doubleValue()));

        h.setOnMouseDragged(this::handleResize);
        handleShapes.add(h);
        zoomGroup.getChildren().add(h);
    }

    private void handleResize(MouseEvent e) {
        Point2D p = zoomGroup.sceneToLocal(new Point2D(e.getSceneX(), e.getSceneY()));
        String pos = (String) ((Node)e.getSource()).getUserData();

        if (shape instanceof Rectangle r) {
            if (pos.contains("E")) r.setWidth(Math.max(MIN_DIMENSION, p.getX() - r.getX()));
            if (pos.contains("S")) r.setHeight(Math.max(MIN_DIMENSION, p.getY() - r.getY()));
            if (pos.contains("W")) {
                double oldRight = r.getX() + r.getWidth();
                r.setX(Math.min(p.getX(), oldRight - MIN_DIMENSION));
                r.setWidth(oldRight - r.getX());
            }
            if (pos.contains("N")) {
                double oldBottom = r.getY() + r.getHeight();
                r.setY(Math.min(p.getY(), oldBottom - MIN_DIMENSION));
                r.setHeight(oldBottom - r.getY());
            }
        } else if (shape instanceof Circle c) {
            // Radius berechnen als Distanz von Maus zu Center
            double dx = p.getX() - c.getCenterX();
            double dy = p.getY() - c.getCenterY();
            c.setRadius(Math.max(MIN_DIMENSION, Math.sqrt(dx * dx + dy * dy)));
        }
        updateHandlePositions();
        e.consume();
    }

    private void updateHandlePositions() {
        for (Rectangle h : handleShapes) {
            String pos = (String) h.getUserData();
            if (shape instanceof Rectangle r) {
                double x = r.getX(), y = r.getY(), w = r.getWidth(), ht = r.getHeight();
                switch (pos) {
                    case "NW" -> setHandlePos(h, x, y);
                    case "N"  -> setHandlePos(h, x + w / 2, y);
                    case "NE" -> setHandlePos(h, x + w, y);
                    case "W"  -> setHandlePos(h, x, y + ht / 2);
                    case "E"  -> setHandlePos(h, x + w, y + ht / 2);
                    case "SW" -> setHandlePos(h, x, y + ht);
                    case "S"  -> setHandlePos(h, x + w / 2, y + ht);
                    case "SE" -> setHandlePos(h, x + w, y + ht);
                }
            } else if (shape instanceof Circle c) {
                double cx = c.getCenterX(), cy = c.getCenterY(), rad = c.getRadius();
                switch (pos) {
                    case "N" -> setHandlePos(h, cx, cy - rad);
                    case "S" -> setHandlePos(h, cx, cy + rad);
                    case "E" -> setHandlePos(h, cx + rad, cy);
                    case "W" -> setHandlePos(h, cx - rad, cy);
                }
            }
        }
    }

    private void setHandlePos(Rectangle h, double x, double y) {
        h.setX(x - HANDLE_SIZE / 2);
        h.setY(y - HANDLE_SIZE / 2);
    }

    public void hideHandles() {
        zoomGroup.getChildren().removeAll(handleShapes);
        handleShapes.clear();
        handlesVisible = false;
    }
}