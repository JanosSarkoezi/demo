package graph.controller;

import graph.model.GraphModel;
import graph.core.state.IdleState;
import graph.core.state.InteractionState;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;

public class CanvasController implements GraphCanvas {
    @FXML
    private Pane drawingCanvas;

    private final Scale zoomTransform = new Scale(1, 1, 0, 0);
    private SelectionManager selectionManager;

    // Deine Layer-Struktur
    private final Group world = new Group();
    private final Group connectionLayer = new Group();
    private final Group shapeLayer = new Group();
    private final Group textLayer = new Group();
    private final Group uiLayer = new Group();

    private MainController main;

    private final GraphModel graphModel = new GraphModel();
    private InteractionState currentState;

    public void init(MainController main) {
        this.selectionManager = new SelectionManager(main);
        this.currentState = new IdleState(main);
        this.main = main;
    }

    private static final double MIN_SCALE = 0.2;
    private static final double MAX_SCALE = 10.0;

    private void drawGrid(double gridSize) {
        Group gridGroup = new Group();
        // Zeichne Linien für einen Bereich von z.B. -2000 bis 2000
        for (double i = -2000; i <= 2000; i += gridSize) {
            Line xLine = new Line(i, -2000, i, 2000);
            Line yLine = new Line(-2000, i, 2000, i);
            xLine.setStroke(Color.LIGHTGRAY);
            xLine.setStrokeWidth(0.5);
            yLine.setStroke(Color.LIGHTGRAY);
            yLine.setStrokeWidth(0.5);
            gridGroup.getChildren().addAll(xLine, yLine);
        }
        // Als ersten Layer hinzufügen, damit es hinter den Shapes liegt
        world.getChildren().add(0, gridGroup);
    }

    public void handleZoom(ScrollEvent event) {
        double delta = event.getDeltaY();
        if (delta == 0.0 || !event.isControlDown()) return;

        double zoomFactor = (delta > 0) ? 1.1 : 0.9;

        // Aktuellen Scale abrufen
        double oldScale = zoomTransform.getX();
        double newScale = oldScale * zoomFactor;

        if (newScale < MIN_SCALE || newScale > MAX_SCALE) return;

        double mouseX = event.getSceneX();
        double mouseY = event.getSceneY();

        Point2D mouseInWorldBefore = world.sceneToLocal(mouseX, mouseY);

        zoomTransform.setX(newScale);
        zoomTransform.setY(newScale);

        Point2D mouseInWorldAfter = world.sceneToLocal(mouseX, mouseY);

        double deltaX = mouseInWorldAfter.getX() - mouseInWorldBefore.getX();
        double deltaY = mouseInWorldAfter.getY() - mouseInWorldBefore.getY();

        world.setTranslateX(world.getTranslateX() + deltaX * newScale);
        world.setTranslateY(world.getTranslateY() + deltaY * newScale);

        event.consume();
    }

    @FXML
    public void initialize() {
        world.getTransforms().add(zoomTransform);

        drawGrid(40.0);

        world.getChildren().addAll(
                connectionLayer,
                shapeLayer,
                textLayer,
                uiLayer
        );

        Rectangle clip = new Rectangle();
        // Die Maske bindet sich an die tatsächliche Größe des Panes
        clip.widthProperty().bind(drawingCanvas.widthProperty());
        clip.heightProperty().bind(drawingCanvas.heightProperty());
        drawingCanvas.setClip(clip);

        drawingCanvas.getChildren().add(world);

        drawingCanvas.setOnMousePressed(e -> {
            if (currentState != null) currentState.handleMousePressed(e, drawingCanvas);
        });

        drawingCanvas.setOnMouseDragged(e -> {
            if (currentState != null) currentState.handleMouseDragged(e, drawingCanvas);
        });

        drawingCanvas.setOnMouseReleased(e -> {
            if (currentState != null) currentState.handleMouseReleased(e, drawingCanvas);
        });

        // drawingCanvas.setOnScroll(this::handleZoom);
        drawingCanvas.addEventFilter(ScrollEvent.SCROLL, this::handleZoom);
    }

    public Scale getZoomTransform() {
        return zoomTransform;
    }

    @Override
    public Group getUiLayer() {
        return uiLayer;
    }

    @Override
    public Group getShapeLayer() {
        return shapeLayer;
    }

    @Override
    public Group getConnectionLayer() {
        return connectionLayer;
    }

    @Override
    public SelectionManager getSelectionManager() {
        return selectionManager;
    }

    @Override
    public Group getTextLayer() {
        return textLayer;
    }

    @Override
    public GraphModel getGraphModel() {
        return graphModel;
    }

    public Group getWorld() {
        return world;
    }

    public InteractionState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(InteractionState newState) {
        // Namen für die Statuszeile vorbereiten
        String oldStateName = (this.currentState != null)
                ? this.currentState.getClass().getSimpleName()
                : "Initial";
        String newStateName = (newState != null)
                ? newState.getClass().getSimpleName()
                : "null";

        // Den eigentlichen Wechsel vollziehen
        this.currentState = newState;

        // Statuszeile über den MainController aktualisieren
        if (main != null && main.getStatusLabel() != null) {
            String message = String.format("Status-Wechsel: %s -> %s", oldStateName, newStateName);
            main.getStatusLabel().setText(message);
        }

        // Optional: Zusätzlich in die Konsole loggen für den schnellen Check
        System.out.println("LOG: " + oldStateName + " -> " + newStateName);
    }
}