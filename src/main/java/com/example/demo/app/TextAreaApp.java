package com.example.demo.app;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class TextAreaApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // 1. Die TextArea erstellen
        TextArea textArea = new TextArea("Schreibe hier etwas...");
        textArea.setMaxSize(300, 200);

        textArea.getStyleClass().add("transparent-area");

        // 2. Das Rectangle als Rahmen erstellen
        Rectangle frame = new Rectangle();
        // Bindet die Größe des Rectangles an die TextArea (plus etwas Puffer)
        frame.widthProperty().bind(textArea.widthProperty().add(2));
        frame.heightProperty().bind(textArea.heightProperty().add(2));

        frame.setFill(Color.TRANSPARENT);
        frame.setStroke(Color.DARKSLATEBLUE);
        frame.setStrokeWidth(3); // Die gewünschte StrokeWidth

        // 3. Layout: StackPane stapelt Rectangle und TextArea übereinander
        StackPane root = new StackPane();
        root.setPadding(new Insets(20));
        root.getChildren().addAll(frame, textArea);

        Scene scene = new Scene(root, 400, 300);
        scene.getStylesheets().add(getClass().getResource("css/asdf.css").toExternalForm());

        primaryStage.setTitle("JavaFX TextArea im Rahmen");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}