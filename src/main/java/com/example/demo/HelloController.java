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
    private final Scale zoomTransform = new Scale(1, 1);

    @FXML
    public void initialize() {
        // --- 1. SCHUTZ & HINTERGRUND ---
        // WICHTIG: Ein Hintergrund ist nötig, damit das Canvas Events fängt!
        drawingCanvas.setStyle("-fx-background-color: #f4f4f4;"); // Hellgrauer Hintergrund

        drawingCanvas.getChildren().add(zoomGroup);
        zoomGroup.getTransforms().add(zoomTransform);

        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(drawingCanvas.widthProperty());
        clip.heightProperty().bind(drawingCanvas.heightProperty());
        drawingCanvas.setClip(clip);

        // --- 2. DRAG & DROP ---
        circleTool.setOnDragDetected(getNewCircle());
        drawingCanvas.setOnDragOver(getOnDragOver());
        drawingCanvas.setOnDragDropped(getOnDragDropped());

        // --- 3. ZOOM-LOGIK (VERBESSERT) ---
        // Wir nutzen addEventFilter, um sicherzugehen, dass das Event ankommt
        drawingCanvas.addEventFilter(ScrollEvent.SCROLL, event -> {
            // Überprüfung: Scrollst du mit oder ohne STRG?
            // Ich nehme die STRG-Abfrage mal raus oder mache sie optional,
            // damit du sofort siehst, ob es klappt.

            double deltaY = event.getDeltaY();
            if (deltaY == 0.0) return;
            System.out.println(" deltaY " + deltaY);
            double zoomFactor = (deltaY > 0) ? 1.1 : 0.9;

            double oldScaleX = zoomTransform.getX();
            double oldScaleY = zoomTransform.getY();
            double newScaleX = oldScaleX * zoomFactor;
            double newScaleY = oldScaleY * zoomFactor;

            System.out.println("[" + oldScaleX + ", " + oldScaleY + "] [" + newScaleX + ", " + newScaleY + "] " );

            if (newScaleX > 0.1 && newScaleX < 10.0) {
                // Pivot-Punkt ist die Mausposition relativ zum Canvas
                zoomTransform.setPivotX(event.getX());
                zoomTransform.setPivotY(event.getY());

                zoomTransform.setX(newScaleX);
                zoomTransform.setY(newScaleY);
            }

            // Verhindert, dass das Scrollen das ganze Fenster bewegt
            event.consume();
        });
    }

    private EventHandler<ScrollEvent> getScrollEventEventHandler() {
        return event -> {
            if (event.isControlDown()) {
                double zoomFactor = (event.getDeltaY() > 0) ? 2.0 : 0.9;

                double newScaleX = zoomTransform.getX() * zoomFactor;
                double newScaleY = zoomTransform.getY() * zoomFactor;

                // Grenzen: 20% bis 1000% Zoom
                if (newScaleX > 0.2 && newScaleX < 10.0) {
                    // WICHTIG: Pivot auf die aktuelle Mausposition setzen
                    // Da wir das Event vom drawingCanvas erhalten, sind event.getX/Y stabil.
                    //zoomTransform.setPivotX(event.getX());
                    //zoomTransform.setPivotY(event.getY());

                    zoomTransform.setX(newScaleX);
                    zoomTransform.setY(newScaleY);
                }
                event.consume();
            }
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
            // Wir merken uns, wo im Kreis wir geklickt haben
            offset[0] = c.getCenterX() - e.getX();
            offset[1] = c.getCenterY() - e.getY();
            c.toFront(); // Kreis nach vorne holen
        });

        c.setOnMouseDragged(e -> {
            // Da der Kreis Kind der zoomGroup ist, sind e.getX() bereits im skalierten System!
            double newX = e.getX() + offset[0];
            double newY = e.getY() + offset[1];

            // Optionales Clamping (hier relativ zur ursprünglichen Canvas-Größe)
            double radius = c.getRadius();
            if (newX >= radius && newX <= drawingCanvas.getWidth() - radius) {
                c.setCenterX(newX);
            }
            if (newY >= radius && newY <= drawingCanvas.getHeight() - radius) {
                c.setCenterY(newY);
            }
        });

        return c;
    }

    private EventHandler<DragEvent> getOnDragOver() {
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