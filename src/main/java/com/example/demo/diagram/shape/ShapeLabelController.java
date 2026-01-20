package com.example.demo.diagram.shape;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class ShapeLabelController {
    private final Label label = new Label();

    public ShapeLabelController(Pane parent, ShapeAdapter adapter) {
        label.setMouseTransparent(true);
        label.setWrapText(true);
        label.setStyle("-fx-background-color: transparent;");
        parent.getChildren().add(label);

        if (adapter.getShape() instanceof Rectangle r) {
            label.layoutXProperty().bind(r.xProperty());
            label.layoutYProperty().bind(r.yProperty());
            label.prefWidthProperty().bind(r.widthProperty().subtract(10));
            label.prefHeightProperty().bind(r.heightProperty().subtract(10));
            label.setPadding(new Insets(5));
        } else if (adapter.getShape() instanceof Circle c) {
            double side = c.getRadius() * Math.sqrt(2);
            label.layoutXProperty().bind(Bindings.subtract(c.centerXProperty(), side / 2));
            label.layoutYProperty().bind(Bindings.subtract(c.centerYProperty(), side / 2));
            label.prefWidthProperty().bind(Bindings.createDoubleBinding(() -> c.getRadius() * Math.sqrt(2), c.radiusProperty()));
            label.prefHeightProperty().bind(label.prefWidthProperty());
        }
    }

    public void updateText(String text) { label.setText(text); }
}
