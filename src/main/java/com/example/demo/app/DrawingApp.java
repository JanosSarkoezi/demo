package com.example.demo.app;

import com.example.demo.ui.CanvasCamera;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

// 1. Tool-Interface
interface Tool {
    void handle(MouseEvent event, Pane canvas, Group world);

    String getName();
}

// 2. Die Werkzeuge
record DrawTool() implements Tool {
    public String getName() {
        return "ZEICHNEN (Klick auf Hintergrund)";
    }

    @Override
    public void handle(MouseEvent event, Pane canvas, Group world) {
        if (event.getEventType() == MouseEvent.MOUSE_CLICKED && event.getTarget() == canvas) {
            // Umrechnung: Wo ist der Klick relativ zur skalierten/verschobenen Welt?
            Point2D pos = world.sceneToLocal(event.getSceneX(), event.getSceneY());
            Circle c = new Circle(pos.getX(), pos.getY(), 20, Color.CYAN);
            c.setStroke(Color.WHITE);
            world.getChildren().add(c);
        }
    }
}

class MoveTool implements Tool {
    private javafx.scene.Node target = null;
    private Point2D lastMouseInWorld = null;

    @Override
    public String getName() {
        return "MOVE & PAN (Objekt oder Welt)";
    }

    @Override
    public void handle(MouseEvent event, Pane canvas, Group world) {
        // Wir rechnen die aktuelle Mausposition IMMER in Welt-Koordinaten um
        Point2D currentMouseInWorld = world.sceneToLocal(event.getSceneX(), event.getSceneY());

        if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
            lastMouseInWorld = currentMouseInWorld;

            if (event.getTarget() instanceof Shape s) {
                target = s;
            } else {
                target = world;
            }
            event.consume();
        } else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED && target != null) {
            // Delta im Welt-Koordinatensystem (Zoom/Pan bereits "herausgerechnet")
            double deltaX = currentMouseInWorld.getX() - lastMouseInWorld.getX();
            double deltaY = currentMouseInWorld.getY() - lastMouseInWorld.getY();

            target.setTranslateX(target.getTranslateX() + deltaX);
            target.setTranslateY(target.getTranslateY() + deltaY);

            // WICHTIG: Nach der Bewegung die Position neu berechnen
            lastMouseInWorld = world.sceneToLocal(event.getSceneX(), event.getSceneY());
        } else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
            target = null;
        }
    }
}

// 3. Hauptanwendung
public class DrawingApp extends Application {

    private Tool currentTool = new DrawTool();
    private final Group world = new Group();
    private final Label statusLabel = new Label();
    private final CanvasCamera camera = new CanvasCamera(world);

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        Pane canvas = new Pane(world);
        canvas.setStyle("-fx-background-color: #1a1a1a;");

        // Clipping: Verhindert, dass gezoomte Objekte aus dem Canvas "rauslaufen"
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(canvas.widthProperty());
        clip.heightProperty().bind(canvas.heightProperty());
        canvas.setClip(clip);

        // Events delegieren
        canvas.addEventHandler(MouseEvent.ANY, e -> currentTool.handle(e, canvas, world));

        // Zoom-Funktionalität
        canvas.addEventHandler(ScrollEvent.SCROLL, e -> camera.handleZoom(e));

        // UI Setup
        updateStatus();
        root.setCenter(canvas);
        root.setBottom(statusLabel);
        statusLabel.setStyle("-fx-padding: 10; -fx-text-fill: white; -fx-background-color: #333;");

        Scene scene = new Scene(root, 800, 600);

        // Key-Steuerung via Filter (fängt Events ab, bevor Nodes sie konsumieren)
        scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.D) currentTool = new DrawTool();
            if (e.getCode() == KeyCode.M) currentTool = new MoveTool();
            updateStatus();
        });

        primaryStage.setTitle("JavaFX Infinite Canvas: [D] Draw | [M] Move & Pan | Scroll to Zoom");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void updateStatus() {
        statusLabel.setText("Tool: " + currentTool.getName() + " | [D] Zeichnen [M] Verschieben");
    }

    public static void main(String[] args) {
        launch(args);
    }
}