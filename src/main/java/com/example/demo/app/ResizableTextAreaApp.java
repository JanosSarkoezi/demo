package com.example.demo.app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class ResizableTextAreaApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        Pane root = new Pane();

        // 1. Das Rechteck erstellen
        Rectangle backgroundRect = new Rectangle(100, 100, 300, 200);
        backgroundRect.setFill(Color.LIGHTBLUE);
        backgroundRect.setStroke(Color.DARKBLUE);

        // 2. Die TextArea erstellen
        TextArea textArea = new TextArea("Schreib etwas...");

        // 3. Binding: TextArea folgt der Position und Größe des Rechtecks
        textArea.layoutXProperty().bind(backgroundRect.xProperty());
        textArea.layoutYProperty().bind(backgroundRect.yProperty());
        textArea.prefWidthProperty().bind(backgroundRect.widthProperty());
        textArea.prefHeightProperty().bind(backgroundRect.heightProperty());

        // Beispiel: Simulation einer Größenänderung nach 3 Sekunden
        // (Hier könntest du später deine Resize-Logik einbauen)
        root.setOnMouseClicked(e -> {
            backgroundRect.setWidth(backgroundRect.getWidth() + 20);
            backgroundRect.setHeight(backgroundRect.getHeight() + 10);
        });

        root.getChildren().addAll(backgroundRect, textArea);

        Scene scene = new Scene(root, 600, 400);
        primaryStage.setTitle("TextArea in Rectangle");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}