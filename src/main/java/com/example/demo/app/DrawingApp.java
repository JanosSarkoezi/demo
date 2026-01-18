package com.example.demo.app;

import com.example.demo.controller.ToolbarController;
import com.example.demo.tool.DrawTool;
import com.example.demo.tool.SelectionTool;
import com.example.demo.tool.Tool;
import com.example.demo.ui.CanvasCamera;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


public class DrawingApp extends Application {

    private Tool currentTool = new DrawTool(ToolbarController.ToolType.NONE);
    private final Group world = new Group();
    private final Label statusLabel = new Label();
    private final CanvasCamera camera = new CanvasCamera(world);

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        Pane canvas = new Pane(world);
        canvas.setStyle("-fx-background-color: #1a1a1a;");

        // Events delegieren
        canvas.addEventHandler(MouseEvent.ANY, e -> currentTool.handle(e, canvas, world));

        // Zoom-Funktionalität
        canvas.addEventHandler(ScrollEvent.SCROLL, camera::handleZoom);

        // UI Setup
        updateStatus();
        root.setCenter(canvas);
        root.setBottom(statusLabel);
        statusLabel.setStyle("-fx-padding: 10; -fx-text-fill: white; -fx-background-color: #333;");

        Scene scene = new Scene(root, 800, 600);

        // Key-Steuerung via Filter (fängt Events ab, bevor Nodes sie konsumieren)
        scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.D) currentTool = new DrawTool(ToolbarController.ToolType.CIRCLE);
            if (e.getCode() == KeyCode.M) currentTool = new SelectionTool();
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