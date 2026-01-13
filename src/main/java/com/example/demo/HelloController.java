package com.example.demo;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;

public class HelloController {

    @FXML private StackPane circleTool;
    @FXML private Pane drawingCanvas;

    // Ein Container für alle gezeichneten Objekte, der skaliert wird
    private final Group zoomGroup = new Group();
    private final Scale zoomTransform = new Scale(1, 1, 0,0);

    @FXML
    public void initialize() {
        // --- 1. SETUP: HIERARCHIE & CLIPPING ---
        // Die zoomGroup kommt in das drawingCanvas.
        // So bleibt das Koordinatensystem des Canvas stabil, während der Inhalt skaliert.
        drawingCanvas.getChildren().add(zoomGroup);
        zoomGroup.getTransforms().add(zoomTransform);

        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(drawingCanvas.widthProperty());
        clip.heightProperty().bind(drawingCanvas.heightProperty());
        drawingCanvas.setClip(clip);

        // --- 2. EVENTS ---
        circleTool.setOnDragDetected(getNewCircle());
        drawingCanvas.setOnDragOver(getOnDragOver());
        drawingCanvas.setOnDragDropped(getOnDragDropped());

        // Scrollen auf dem drawingCanvas (dem stabilen Rahmen) hören
        drawingCanvas.setOnScroll(getScrollEventEventHandler());
    }

    private EventHandler<ScrollEvent> getScrollEventEventHandler() {
        return event -> {
            double deltaY = event.getDeltaY();
            // Zoom nur mit gedrückter Strg-Taste (wie in deinem Code)
            if (deltaY == 0.0 || !event.isControlDown()) return;

            double zoomFactor = (deltaY > 0) ? 1.1 : 0.9;
            double oldScale = zoomTransform.getX();
            double newScale = oldScale * zoomFactor;

            // Zoom-Grenzen einhalten
            if (newScale > 0.2 && newScale < 10.0) {

                // 1. Mausposition relativ zum drawingCanvas (Parent)
                double mouseX = event.getX();
                double mouseY = event.getY();

                // 2. Aktuelle Verschiebung (Translate) abrufen
                double curTranslateX = zoomGroup.getTranslateX();
                double curTranslateY = zoomGroup.getTranslateY();

                /* * 3. Die entscheidende Formel:
                 * Wir berechnen, wie weit die Maus vom Ursprung der Group (inkl. Translation) entfernt ist
                 * und passen die Translation so an, dass dieser Punkt trotz neuem Scale stabil bleibt.
                 */
                zoomGroup.setTranslateX(mouseX - (mouseX - curTranslateX) * zoomFactor);
                zoomGroup.setTranslateY(mouseY - (mouseY - curTranslateY) * zoomFactor);

                // 4. Den eigentlichen Scale-Wert setzen
                zoomTransform.setX(newScale);
                zoomTransform.setY(newScale);
            }
            event.consume();
        };
    }

    private EventHandler<DragEvent> getOnDragDropped() {
        return event -> {
            if ("NEW_CIRCLE".equals(event.getDragboard().getString())) {
                // Umrechnung: Maus-Position im Fenster -> Position innerhalb der skalierten Group
                Point2D mouseInScene = new Point2D(event.getSceneX(), event.getSceneY());
                Point2D mouseInGroup = zoomGroup.sceneToLocal(mouseInScene);

                Circle newCircle = createDraggableCircle(mouseInGroup.getX(), mouseInGroup.getY());
                zoomGroup.getChildren().add(newCircle);
            }
            event.setDropCompleted(true);
            event.consume();
        };
    }

    private Circle createDraggableCircle(double x, double y) {
        Circle c = new Circle(25, Color.DODGERBLUE);
        c.setStroke(Color.BLACK);
        c.setCenterX(x);
        c.setCenterY(y);

        // Variablen zum Speichern der Klick-Offset-Position
        final double[] offset = new double[2];

        c.setOnMousePressed(e -> {
            // Offset merken: Klickposition innerhalb des Kreises
            offset[0] = c.getCenterX() - e.getX();
            offset[1] = c.getCenterY() - e.getY();
            c.toFront(); // Kreis nach vorne
        });

        c.setOnMouseDragged(e -> {
            // Neue Position berechnen
            double newX = e.getX() + offset[0];
            double newY = e.getY() + offset[1];

            double radius = c.getRadius();

            // --- Clamping im lokalen Group-Koordinatensystem ---
            double maxX = drawingCanvas.getWidth() / zoomTransform.getX() - radius;
            double maxY = drawingCanvas.getHeight() / zoomTransform.getY() - radius;

            double minX = radius;
            double minY = radius;

            if (newX < minX) newX = minX;
            if (newX > maxX) newX = maxX;

            if (newY < minY) newY = minY;
            if (newY > maxY) newY = maxY;

            c.setCenterX(newX);
            c.setCenterY(newY);
        });

        return c;
    }

    private static EventHandler<DragEvent> getOnDragOver() {
        return event -> {
            if (event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        };
    }

    private EventHandler<MouseEvent> getNewCircle() {
        return event -> {
            Dragboard db = circleTool.startDragAndDrop(TransferMode.COPY);
            ClipboardContent content = new ClipboardContent();
            content.putString("NEW_CIRCLE");
            db.setContent(content);
            event.consume();
        };
    }
}