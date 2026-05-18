package graph.core.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import java.util.UUID;

public class FmcObject {
    private final UUID id;
    private final FmcType type;
    private final DoubleProperty x = new SimpleDoubleProperty();
    private final DoubleProperty y = new SimpleDoubleProperty();

    public FmcObject(FmcType type, double x, double y) {
        this.id = UUID.randomUUID();
        this.type = type;
        this.x.set(x);
        this.y.set(y);
    }

    public UUID getId() { return id; }
    public FmcType getType() { return type; }

    public double getX() { return x.get(); }
    public void setX(double value) { x.set(value); }
    public DoubleProperty xProperty() { return x; }

    public double getY() { return y.get(); }
    public void setY(double value) { y.set(value); }
    public DoubleProperty yProperty() { return y; }
}
