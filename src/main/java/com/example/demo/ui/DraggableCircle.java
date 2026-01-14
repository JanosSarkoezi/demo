package com.example.demo.ui;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class DraggableCircle extends Circle {
    private double anchorX, anchorY;
    private final Pane boundsProvider;
    private final Group zoomGroup;
    private final List<Rectangle> handleShapes = new ArrayList<>();
    private static final double HANDLE_SIZE = 8.0;
    private boolean handlesVisible = false;

    public DraggableCircle(double x, double y, double radius, Color color, Pane boundsProvider, Group zoomGroup) {
        super(radius, color);
        this.boundsProvider = boundsProvider;
        this.zoomGroup = zoomGroup;
        setCenterX(x);
        setCenterY(y);
        setStroke(Color.BLACK);

        setupInteractions();
    }

    private void setupInteractions() {
        setOnMousePressed(e -> {
            if (e.isControlDown()) {
                toggleHandles();
            } else {
                hideHandles();
                anchorX = getCenterX() - e.getX();
                anchorY = getCenterY() - e.getY();
                toFront();
            }
            e.consume();
        });

        setOnMouseDragged(e -> {
            if (!e.isControlDown()) {
                double newX = e.getX() + anchorX;
                double newY = e.getY() + anchorY;

                // Snap to Grid
                double gridSpacing = 50.0;
                newX = Math.round(newX / gridSpacing) * gridSpacing;
                newY = Math.round(newY / gridSpacing) * gridSpacing;

                Point2D topLeft = zoomGroup.parentToLocal(0, 0);
                Point2D bottomRight = zoomGroup.parentToLocal(boundsProvider.getWidth(), boundsProvider.getHeight());

                setCenterX(Math.clamp(newX, topLeft.getX() + getRadius(), bottomRight.getX() - getRadius()));
                setCenterY(Math.clamp(newY, topLeft.getY() + getRadius(), bottomRight.getY() - getRadius()));

                if (handlesVisible) updateHandlePositions();
            }
            e.consume();
        });
    }

    private void toggleHandles() {
        if (handlesVisible) hideHandles();
        else showHandles();
        handlesVisible = !handlesVisible;
    }

    private void showHandles() {
        // Bei Kreisen reichen oft N, S, E, W, aber hier sind alle 8:
        String[] positions = {"N", "NE", "E", "SE", "S", "SW", "W", "NW"};
        for (String pos : positions) {
            createHandle(pos);
        }
        updateHandlePositions();
    }

    private void createHandle(String pos) {
        Rectangle h = new Rectangle(HANDLE_SIZE, HANDLE_SIZE, Color.WHITE);
        h.setStroke(Color.DARKRED);
        h.setUserData(pos);
        h.getStyleClass().add("handle");

        // Modernes JavaFX Binding (Java 19+)
        h.scaleXProperty().bind(zoomGroup.scaleXProperty().map(s -> 1.0 / s.doubleValue()));
        h.scaleYProperty().bind(zoomGroup.scaleYProperty().map(s -> 1.0 / s.doubleValue()));

        h.setOnMouseDragged(this::handleResize);
        handleShapes.add(h);
        zoomGroup.getChildren().add(h);
    }

    private void handleResize(MouseEvent e) {
        if (e.getSource() instanceof Rectangle h) {
            Point2D mouseLocal = zoomGroup.sceneToLocal(new Point2D(e.getSceneX(), e.getSceneY()));

            // Logik: Der neue Radius ist der Abstand von der Maus zum Kreismittelpunkt
            double dx = mouseLocal.getX() - getCenterX();
            double dy = mouseLocal.getY() - getCenterY();
            double newRadius = Math.sqrt(dx * dx + dy * dy);

            setRadius(Math.max(10, newRadius));
            updateHandlePositions();
        }
        e.consume();
    }

    private void updateHandlePositions() {
        double cx = getCenterX();
        double cy = getCenterY();
        double r = getRadius();
        // Hilfswert für diagonale Handles (sin/cos 45°)
        double diag = r * Math.sqrt(2) / 2;

        for (Rectangle h : handleShapes) {
            if (h.getUserData() instanceof String pos) {
                switch (pos) {
                    case "N"  -> setH(h, cx, cy - r);
                    case "S"  -> setH(h, cx, cy + r);
                    case "E"  -> setH(h, cx + r, cy);
                    case "W"  -> setH(h, cx - r, cy);
                    case "NE" -> setH(h, cx + diag, cy - diag);
                    case "SE" -> setH(h, cx + diag, cy + diag);
                    case "NW" -> setH(h, cx - diag, cy - diag);
                    case "SW" -> setH(h, cx - diag, cy + diag);
                }
            }
        }
    }

    private void setH(Rectangle h, double x, double y) {
        h.setX(x - HANDLE_SIZE / 2);
        h.setY(y - HANDLE_SIZE / 2);
    }

    private void hideHandles() {
        zoomGroup.getChildren().removeAll(handleShapes);
        handleShapes.clear();
    }
}