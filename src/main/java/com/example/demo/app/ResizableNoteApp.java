package com.example.demo.app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class ResizableNoteApp extends Application {

    private double mouseX, mouseY;

    @Override
    public void start(Stage primaryStage) {
        Pane root = new Pane();
        // Macht die Pane fokussierbar
        root.setFocusTraversable(true);

        // Hintergrund-Klick beendet Edit-Modus
        root.setOnMousePressed(e -> root.requestFocus());

        Rectangle rect = new Rectangle(50, 50, 250, 150);
        rect.setFill(Color.WHITESMOKE);
        rect.setStroke(Color.DARKGRAY);
        rect.setArcWidth(10);
        rect.setArcHeight(10);

        TextArea textArea = new TextArea("Doppelklick zum Editieren...");
        textArea.setWrapText(true);

        textArea.layoutXProperty().bind(rect.xProperty());
        textArea.layoutYProperty().bind(rect.yProperty());
        textArea.prefWidthProperty().bind(rect.widthProperty());
        textArea.prefHeightProperty().bind(rect.heightProperty());

        applyDisplayMode(textArea);

        Rectangle handle = new Rectangle(10, 10, Color.DARKGRAY);
        handle.xProperty().bind(rect.xProperty().add(rect.widthProperty()).subtract(10));
        handle.yProperty().bind(rect.yProperty().add(rect.heightProperty()).subtract(10));
        handle.setCursor(javafx.scene.Cursor.SE_RESIZE);

        // Resize Logik
        handle.setOnMousePressed(e -> {
            mouseX = e.getScreenX();
            mouseY = e.getScreenY();
            e.consume(); // Verhindert, dass der Klick das root-Event auslöst
        });

        handle.setOnMouseDragged(e -> {
            double deltaX = e.getScreenX() - mouseX;
            double deltaY = e.getScreenY() - mouseY;
            rect.setWidth(Math.max(50, rect.getWidth() + deltaX));
            rect.setHeight(Math.max(50, rect.getHeight() + deltaY));
            mouseX = e.getScreenX();
            mouseY = e.getScreenY();
        });

        // Modus-Wechsel Logik
        textArea.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                applyEditMode(textArea);
            }
        });

        textArea.focusedProperty().addListener((obs, oldVal, isFocused) -> {
            if (!isFocused) {
                applyDisplayMode(textArea);
            }
        });

        root.getChildren().addAll(rect, textArea, handle);
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setTitle("Note: Click outside to save");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void applyDisplayMode(TextArea ta) {
        ta.setEditable(false);
        ta.setStyle("-fx-background-color: transparent; " +
                "-fx-control-inner-background: transparent; " +
                "-fx-background-insets: 0; " +
                "-fx-padding: 5;");
    }

    private void applyEditMode(TextArea ta) {
        ta.setEditable(true);
        ta.setStyle("");
    }

    public static void main(String[] args) {
        launch(args);
    }
}