package graph.view;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;

public class GraphView extends Pane {
    // Die Layer-Struktur
    private final Group world = new Group();
    private final Group connectionLayer = new Group();
    private final Group shapeLayer = new Group();
    private final Group textLayer = new Group();
    private final Group uiLayer = new Group();

    private static final double MIN_SCALE = 0.2;
    private static final double MAX_SCALE = 10.0;

    private final Scale zoomTransform = new Scale(1, 1, 0, 0);

    public GraphView() {
        setupLayers();
        drawGrid(40.0);

        // Clipping aktivieren, damit Zeichnungen nicht über den Rand ragen
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(this.widthProperty());
        clip.heightProperty().bind(this.heightProperty());
        this.setClip(clip);
    }

    private void setupLayers() {
        // Die Reihenfolge entscheidet über die Z-Ebene (was liegt vorne?)
        world.getChildren().addAll(connectionLayer, shapeLayer, textLayer, uiLayer);
        world.getTransforms().add(zoomTransform);
        this.getChildren().add(world);
    }

    private void drawGrid(double gridSize) {
        Group gridGroup = new Group();
        for (double i = -2000; i <= 2000; i += gridSize) {
            Line vertical = new Line(i, -2000, i, 2000);
            vertical.setStroke(Color.LIGHTGRAY.deriveColor(0, 1, 1, 0.2));
            Line horizontal = new Line(-2000, i, 2000, i);
            horizontal.setStroke(Color.LIGHTGRAY.deriveColor(0, 1, 1, 0.2));
            gridGroup.getChildren().addAll(vertical, horizontal);
        }
        // Das Grid kommt ganz nach hinten
        world.getChildren().add(0, gridGroup);
    }

    // Hilfsmethode für die Koordinaten-Umrechnung (für die States)
    public Point2D screenToWorld(double sceneX, double sceneY) {
        return world.sceneToLocal(sceneX, sceneY);
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

    // Getter für die Layer (für Renderer und Controller)
    public Group getShapeLayer() { return shapeLayer; }
    public Group getUiLayer() { return uiLayer; }
    public Group getConnectionLayer() { return connectionLayer; }
    public Group getTextLayer() { return textLayer; }
    public Group getWorld() { return world; }
    public Scale getZoomTransform() { return zoomTransform; }
}