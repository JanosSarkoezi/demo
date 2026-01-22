package graph.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;

public class CircleModel extends GraphNode {
    private final DoubleProperty radius = new SimpleDoubleProperty(20);
    private final DoubleProperty width = new SimpleDoubleProperty();
    private final DoubleProperty height = new SimpleDoubleProperty();

    public CircleModel(double x, double y) {
        super(x, y);
        // Bidirektionale Logik: Width/Height sind immer 2 * Radius
        width.bind(radius.multiply(2));
        height.bind(radius.multiply(2));
    }

    @Override
    public Point2D getCenter() {
        return new Point2D(xProperty().get(), yProperty().get());
    }

    @Override
    public void setCenter(double centerX, double centerY) {
        xProperty().set(centerX);
        yProperty().set(centerY);
    }

    public DoubleProperty radiusProperty() { return radius; }
    public DoubleProperty widthProperty() { return width; }
    public DoubleProperty heightProperty() { return height; }
}