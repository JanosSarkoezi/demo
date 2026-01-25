package graph.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;

public abstract class GraphNode {
    private final DoubleProperty x = new SimpleDoubleProperty();
    private final DoubleProperty y = new SimpleDoubleProperty();
    private final BooleanProperty selected = new SimpleBooleanProperty(false);

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
    public boolean isSelected() { return selected.get(); }
    public void setSelected(boolean value) { selected.set(value); }
    public BooleanProperty selectedProperty() { return selected; }
}