package graph.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class WaypointModel {
    private final DoubleProperty x = new SimpleDoubleProperty();
    private final DoubleProperty y = new SimpleDoubleProperty();

    public WaypointModel(double x, double y) {
        setX(x);
        setY(y);
    }

    // Standard-Setter (wichtig für manuelle Zuweisung)
    public final void setX(double value) { x.set(value); }
    public final void setY(double value) { y.set(value); }

    // Standard-Getter
    public final double getX() { return x.get(); }
    public final double getY() { return y.get(); }

    // Property-Getter (entscheidend für das Binding im Renderer!)
    public DoubleProperty xProperty() { return x; }
    public DoubleProperty yProperty() { return y; }
}