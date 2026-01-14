package com.example.demo.util;

import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
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
        this.boundsProvider = boundsProvider; //
        this.zoomGroup = zoomGroup; //
        setX(x);
        setY(y);
        setStroke(Color.BLACK);

        setupInteractions();
    }

    private void setupInteractions() {
        setOnMousePressed(e -> {
            // Strg + Klick: Handles umschalten
            if (e.isControlDown()) {
                toggleHandles();
            } else {
                hideHandles();
                // Normaler Drag-Start
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

                // Snap-to-Grid Mitte Logik
                double snappedX = Math.round((newX + getWidth()/2) / gridSpacing) * gridSpacing - getWidth()/2;
                double snappedY = Math.round((newY + getHeight()/2) / gridSpacing) * gridSpacing - getHeight()/2;

                // Clamping
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
        if (handlesVisible) {
            hideHandles();
        } else {
            showHandles();
        }
        handlesVisible = !handlesVisible;
    }

    private void showHandles() {
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

        // Trick: Handle-Größe gegen den Zoom skalieren, damit sie immer 8px groß bleiben
        h.scaleXProperty().bind(zoomGroup.scaleXProperty().subtract(zoomGroup.scaleXProperty()).add(1.0).divide(zoomGroup.scaleXProperty()));
        h.scaleYProperty().bind(zoomGroup.scaleYProperty().subtract(zoomGroup.scaleYProperty()).add(1.0).divide(zoomGroup.scaleYProperty()));

        h.setOnMouseDragged(this::handleResize);
        handleShapes.add(h);
        zoomGroup.getChildren().add(h);
    }

    private void handleResize(MouseEvent e) {
        Rectangle h = (Rectangle) e.getSource();
        String pos = (String) h.getUserData();
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
        e.consume();
    }

    private void updateHandlePositions() {
        for (Rectangle h : handleShapes) {
            String pos = (String) h.getUserData();
            double x = getX(), y = getY(), w = getWidth(), h_ = getHeight();

            if (pos.equals("NW")) setH(h, x, y);
            else if (pos.equals("N"))  setH(h, x + w/2, y);
            else if (pos.equals("NE")) setH(h, x + w, y);
            else if (pos.equals("W"))  setH(h, x, y + h_/2);
            else if (pos.equals("E"))  setH(h, x + w, y + h_/2);
            else if (pos.equals("SW")) setH(h, x, y + h_);
            else if (pos.equals("S"))  setH(h, x + w/2, y + h_);
            else if (pos.equals("SE")) setH(h, x + w, y + h_);
        }
    }

    private void setH(Rectangle h, double x, double y) {
        h.setX(x - HANDLE_SIZE / 2);
        h.setY(y - HANDLE_SIZE / 2);
    }

    private void checkCollisions() {
        boolean isColliding = false;

        for (javafx.scene.Node other : zoomGroup.getChildren()) {
            // Wir prüfen nur andere Rechtecke und ignorieren uns selbst sowie die Handles
            if (other instanceof Rectangle && other != this && !(other.getStyleClass().contains("handle"))) {

                // Die magische JavaFX Methode für Kollisionen
                if (this.getBoundsInParent().intersects(other.getBoundsInParent())) {
                    isColliding = true;
                    // Optional: Visuelles Feedback für das getroffene Objekt
                    ((Rectangle) other).setStroke(Color.RED);
                } else {
                    ((Rectangle) other).setStroke(Color.BLACK);
                }
            }
        }

        // Feedback für das gezogene Objekt selbst
        if (isColliding) {
            this.setFill(Color.ORANGERED); // Warnfarbe beim Überlagern
        } else {
            this.setFill(Color.LIGHTGREEN); // Standardfarbe
        }
    }
}