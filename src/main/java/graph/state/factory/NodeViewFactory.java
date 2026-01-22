package graph.state.factory;

import graph.model.CircleModel;
import graph.model.RectangleModel;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class NodeViewFactory {

    public static Circle createCircleShape(CircleModel model) {
        Circle view = new Circle();
        view.radiusProperty().bind(model.radiusProperty());
        view.setFill(Color.WHITE);
        view.setStroke(Color.BLACK);
        view.setStrokeWidth(3);

        // Bindung an das Model
        view.centerXProperty().bind(model.xProperty());
        view.centerYProperty().bind(model.yProperty());

        view.setUserData(model);
        return view;
    }

    public static Shape createRectangleShape(RectangleModel model) {
        Rectangle rect = new Rectangle();
        rect.setFill(Color.WHITE);
        rect.setStroke(Color.BLACK);
        rect.setStrokeWidth(3);

        // Bindungen an das Model
        rect.widthProperty().bind(model.widthProperty());
        rect.heightProperty().bind(model.heightProperty());
        rect.layoutXProperty().bind(model.xProperty());
        rect.layoutYProperty().bind(model.yProperty());

        rect.setUserData(model);
        return rect;
    }

    public static TextArea createNodeTextArea(RectangleModel model) {
        TextArea textArea = new TextArea(model.getText());
        textArea.setWrapText(true);
        textArea.setMouseTransparent(true);
        textArea.setStyle("-fx-background-color: transparent; -fx-control-inner-background: transparent;");

        // Position und Größe an das Model binden (analog zum Rechteck)
        textArea.layoutXProperty().bind(model.xProperty());
        textArea.layoutYProperty().bind(model.yProperty());
        textArea.prefWidthProperty().bind(model.widthProperty());
        textArea.prefHeightProperty().bind(model.heightProperty());

        // Text-Synchronisation
        textArea.textProperty().addListener((obs, oldVal, newVal) -> model.setText(newVal));

        return textArea;
    }
}
