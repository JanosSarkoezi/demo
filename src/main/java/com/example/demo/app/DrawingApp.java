package com.example.demo.app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

// 1. Das Tool-Interface für unser State-Pattern
interface Tool {
    void handle(MouseEvent event, Pane canvas);
    String getName();
}

// 2. Konkrete Werkzeuge (Java 21 Records für Kompaktheit)
record DrawTool() implements Tool {
    public String getName() { return "ZEICHNEN (Klick auf Hintergrund)"; }
    @Override
    public void handle(MouseEvent event, Pane canvas) {
        if (event.getEventType() == MouseEvent.MOUSE_CLICKED && event.getTarget() == canvas) {
            Circle c = new Circle(event.getX(), event.getY(), 20, Color.CYAN);
            c.setStroke(Color.WHITE);
            canvas.getChildren().add(c);
        }
    }
}

record SelectTool() implements Tool {
    public String getName() { return "AUSWAHL (Klick auf Objekte)"; }
    @Override
    public void handle(MouseEvent event, Pane canvas) {
        if (event.getEventType() == MouseEvent.MOUSE_CLICKED) {
            // Java 21 Pattern Matching: Prüfen, Casten und Filtern in einem Schritt
            if (event.getTarget() instanceof Shape s) {
                s.setFill(Color.LIME);
                s.setScaleX(s.getScaleX() * 1.1); // Ein bisschen Feedback
                s.setScaleY(s.getScaleY() * 1.1);
            }
        }
    }
}

// 3. Die Hauptanwendung
public class DrawingApp extends Application {

    private Tool currentTool = new DrawTool();
    private final Label statusLabel = new Label();

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        Pane canvas = new Pane();
        canvas.setStyle("-fx-background-color: #1a1a1a;"); // Dunkles Zen-Schwarz

        // Initialer Inhalt
        Rectangle rect = new Rectangle(50, 50, 100, 100);
        rect.setFill(Color.ORANGERED);
        canvas.getChildren().add(rect);

        // Der Event-Manager / Mediator
        canvas.addEventHandler(MouseEvent.ANY, e -> currentTool.handle(e, canvas));

        // UI-Steuerung
        updateStatus();
        root.setCenter(canvas);
        root.setBottom(statusLabel);
        statusLabel.setStyle("-fx-padding: 10; -fx-text-fill: white; -fx-background-color: #333;");

        Scene scene = new Scene(root, 600, 400);

        // Werkzeugwechsel via Tastatur
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.D) currentTool = new DrawTool();
            if (e.getCode() == KeyCode.S) currentTool = new SelectTool();
            updateStatus();
        });

        primaryStage.setTitle("JavaFX Meditation: [D] Zeichnen | [S] Selektieren");
        primaryStage.setScene(scene);
        primaryStage.show();

        canvas.requestFocus(); // Damit Tastatureingaben direkt funktionieren
    }

    private void updateStatus() {
        statusLabel.setText("Aktives Werkzeug: " + currentTool.getName() + " | Drücke [D] oder [S] zum Wechseln");
    }

    public static void main(String[] args) {
        launch(args);
    }
}