package com.example.demo.ui;

import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class DraggableRectangle extends Rectangle {
    private double dragAnchorX, dragAnchorY;
    private final double gridSpacing = 50.0;
    private final Pane boundsProvider;
    private final Group zoomGroup;

    private final List<Rectangle> handleShapes = new ArrayList<>();
    private static final double HANDLE_SIZE = 8.0;
    private boolean handlesVisible = false;

    public DraggableRectangle(double x, double y, double width, double height, Color color, Pane boundsProvider, Group zoomGroup) {
        super(width, height, color);
        this.boundsProvider = boundsProvider;
        this.zoomGroup = zoomGroup;
        setX(x);
        setY(y);
        setStroke(Color.BLACK);

        setupInteractions();
    }

    private void setupInteractions() {
        setOnMousePressed(e -> {
            if (e.isControlDown()) {
                toggleHandles();
            } else {
                hideHandles();
                dragAnchorX = getX() - e.getX();
                dragAnchorY = getY() - e.getY();
                toFront();
            }
            e.consume();
        });

        setOnMouseDragged(e -> {
            if (!e.isControlDown()) {
                double newX = e.getX() + dragAnchorX;
                double newY = e.getY() + dragAnchorY;

                // Snap-to-Grid
                double snappedX = Math.round((newX + getWidth() / 2) / gridSpacing) * gridSpacing - getWidth() / 2;
                double snappedY = Math.round((newY + getHeight() / 2) / gridSpacing) * gridSpacing - getHeight() / 2;

                // Clamping mit modernen Point2D Aufrufen
                Point2D topLeft = zoomGroup.parentToLocal(0, 0);
                Point2D bottomRight = zoomGroup.parentToLocal(boundsProvider.getWidth(), boundsProvider.getHeight());

                setX(Math.clamp(snappedX, topLeft.getX(), bottomRight.getX() - getWidth()));
                setY(Math.clamp(snappedY, topLeft.getY(), bottomRight.getY() - getHeight()));

                if (handlesVisible) updateHandlePositions();
                checkCollisions();
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
        // Sequenced Collections könnten hier genutzt werden, aber ein Loop ist für alle 8 effizienter
        String[] positions = {"NW", "N", "NE", "W", "E", "SW", "S", "SE"};
        Cursor[] cursors = {Cursor.NW_RESIZE, Cursor.N_RESIZE, Cursor.NE_RESIZE, Cursor.W_RESIZE,
                Cursor.E_RESIZE, Cursor.SW_RESIZE, Cursor.S_RESIZE, Cursor.SE_RESIZE};

        for (int i = 0; i < positions.length; i++) {
            createHandle(positions[i], cursors[i]);
        }
        updateHandlePositions();
    }

    private void hideHandles() {
        zoomGroup.getChildren().removeAll(handleShapes);
        handleShapes.clear();
    }

    private void createHandle(String pos, Cursor cursor) {
        Rectangle h = new Rectangle(HANDLE_SIZE, HANDLE_SIZE, Color.WHITE);
        h.setStroke(Color.BLUE);
        h.setCursor(cursor);
        h.setUserData(pos);
        // Markierung für die Kollisionsabfrage
        h.getStyleClass().add("handle");

        // Optimiertes Binding: Verwendet die neue divide-Logik sauberer
        h.scaleXProperty().bind(zoomGroup.scaleXProperty().map(s -> 1.0 / s.doubleValue()));
        h.scaleYProperty().bind(zoomGroup.scaleYProperty().map(s -> 1.0 / s.doubleValue()));

        h.setOnMouseDragged(this::handleResize);
        handleShapes.add(h);
        zoomGroup.getChildren().add(h);
    }

    private void handleResize(MouseEvent e) {
        if (e.getSource() instanceof Rectangle h && h.getUserData() instanceof String pos) {
            Point2D p = zoomGroup.sceneToLocal(new Point2D(e.getSceneX(), e.getSceneY()));

            if (pos.contains("E")) setWidth(Math.max(10, p.getX() - getX()));
            if (pos.contains("S")) setHeight(Math.max(10, p.getY() - getY()));
            if (pos.contains("W")) {
                double oldRight = getX() + getWidth();
                setX(Math.min(p.getX(), oldRight - 10));
                setWidth(oldRight - getX());
            }
            if (pos.contains("N")) {
                double oldBottom = getY() + getHeight();
                setY(Math.min(p.getY(), oldBottom - 10));
                setHeight(oldBottom - getY());
            }
            updateHandlePositions();
        }
        e.consume();
    }

    private void updateHandlePositions() {
        double x = getX(), y = getY(), w = getWidth(), h_ = getHeight();

        for (Rectangle h : handleShapes) {
            if (h.getUserData() instanceof String pos) {
                // Java 21 Switch-Expression (Pattern Matching für Strings)
                switch (pos) {
                    case "NW" -> setH(h, x, y);
                    case "N"  -> setH(h, x + w / 2, y);
                    case "NE" -> setH(h, x + w, y);
                    case "W"  -> setH(h, x, y + h_ / 2);
                    case "E"  -> setH(h, x + w, y + h_ / 2);
                    case "SW" -> setH(h, x, y + h_);
                    case "S"  -> setH(h, x + w / 2, y + h_);
                    case "SE" -> setH(h, x + w, y + h_);
                }
            }
        }
    }

    private void setH(Rectangle h, double x, double y) {
        h.setX(x - HANDLE_SIZE / 2);
        h.setY(y - HANDLE_SIZE / 2);
    }

    private void checkCollisions() {
        boolean isColliding = false;

        for (Node other : zoomGroup.getChildren()) {
            // Java 21 Pattern Matching für instanceof
            if (other instanceof Rectangle rect && rect != this && !rect.getStyleClass().contains("handle")) {
                if (this.getBoundsInParent().intersects(rect.getBoundsInParent())) {
                    isColliding = true;
                    rect.setStroke(Color.RED);
                } else {
                    rect.setStroke(Color.BLACK);
                }
            }
        }
        this.setFill(isColliding ? Color.ORANGERED : Color.LIGHTGREEN);
    }
}