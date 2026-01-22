package graph.model.adapter;

import graph.model.CircleModel;
import javafx.beans.Observable;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import java.util.List;

public class CircleAdapter implements ShapeAdapter {
    private final CircleModel model;

    public CircleAdapter(CircleModel model) {
        this.model = model;
    }

    @Override
    public void resize(String handleName, Point2D p) {
        // Logik aus deinem Entwurf: Distanz zum Zentrum = neuer Radius
        double dx = p.getX() - model.xProperty().get();
        double dy = p.getY() - model.yProperty().get();
        double newRadius = Math.max(5.0, Math.sqrt(dx * dx + dy * dy));

        // Wir setzen die Property -> Das Binding im Circle-View reagiert sofort
        model.radiusProperty().set(newRadius);
    }

    @Override public double getWidth() { return model.widthProperty().get(); }
    @Override public double getHeight() { return model.heightProperty().get(); }

    // Wenn über das Interface setWidth aufgerufen wird, passen wir den Radius an
    @Override public void setWidth(double w) { model.radiusProperty().set(w / 2.0); }
    @Override public void setHeight(double h) { model.radiusProperty().set(h / 2.0); }

    @Override public Point2D getPosition() { return new Point2D(model.xProperty().get(), model.yProperty().get()); }
    @Override public void setPosition(double x, double y) {
        model.xProperty().set(x);
        model.yProperty().set(y);
    }

    @Override
    public Point2D getHandlePosition(String handleName) {
        double cx = model.xProperty().get();
        double cy = model.yProperty().get();
        double r  = model.radiusProperty().get();

        return switch (handleName) {
            case "N" -> new Point2D(cx, cy - r);
            case "S" -> new Point2D(cx, cy + r);
            case "E" -> new Point2D(cx + r, cy);
            case "W" -> new Point2D(cx - r, cy);
            default -> throw new IllegalArgumentException("Unknown: " + handleName);
        };
    }

    @Override
    public Observable[] getHandleDependencies(String handleName) {
        // Da jeder Handle am Rand des Kreises sitzt, muss er neu berechnet werden,
        // wenn sich entweder das Zentrum (x, y) oder die Größe (radius) ändert.
        return new Observable[] {
                model.xProperty(),
                model.yProperty(),
                model.radiusProperty()
        };
    }

    @Override public List<String> getHandleNames() { return List.of("N", "S", "E", "W"); }
    @Override public Cursor getHandleCursor(String name) { return Cursor.CROSSHAIR; }
    @Override public Point2D getCenter() { return model.getCenter(); }
    @Override public void setCenter(double x, double y) { model.setCenter(x, y); }
}