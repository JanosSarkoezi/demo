package graph.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;

public class DrawingModel {
    // Die Quelle der Wahrheit für alle Shapes
    private final ObservableList<Node> shapes = FXCollections.observableArrayList();

    public ObservableList<Node> getShapes() {
        return shapes;
    }

    public void addShape(Node shape) {
        shapes.add(shape);
    }

    public void removeShape(Node shape) {
        shapes.remove(shape);
    }
}