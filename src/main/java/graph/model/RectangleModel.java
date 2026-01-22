package graph.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Point2D;

public class RectangleModel extends GraphNode {
    private final DoubleProperty width = new SimpleDoubleProperty(80);
    private final DoubleProperty height = new SimpleDoubleProperty(40);
    private final StringProperty text = new SimpleStringProperty("");
    private final BooleanProperty hasLoop = new SimpleBooleanProperty(false); // Der gekrümmte Pfeil

    public RectangleModel(double x, double y) {
        super(x, y);
    }

    public StringProperty textProperty() { return text; }
    public BooleanProperty hasLoopProperty() { return hasLoop; }
    public DoubleProperty widthProperty() { return width; }
    public DoubleProperty heightProperty() { return height; }
    public String getText() { return text.get(); }
    public void setText(String value) { this.text.set(value); }

    @Override
    public Point2D getCenter() {
        return new Point2D(xProperty().get() + widthProperty().get() / 2,
                yProperty().get() + heightProperty().get() / 2);
    }

    @Override
    public void setCenter(double centerX, double centerY) {
        xProperty().set(centerX - widthProperty().get() / 2);
        yProperty().set(centerY - heightProperty().get() / 2);
    }
}