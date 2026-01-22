package graph.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;

public abstract class GraphNode {
    private final DoubleProperty x = new SimpleDoubleProperty();
    private final DoubleProperty y = new SimpleDoubleProperty();

    public GraphNode(double x, double y) {
        this.x.set(x);
        this.y.set(y);
    }

    public abstract Point2D getCenter();
    public abstract void setCenter(double centerX, double centerY);

    public DoubleProperty xProperty() { return x; }
    public DoubleProperty yProperty() { return y; }
    public abstract DoubleProperty widthProperty();
    public abstract DoubleProperty heightProperty();
}