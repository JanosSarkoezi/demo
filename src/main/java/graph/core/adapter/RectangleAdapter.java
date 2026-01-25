package graph.core.adapter;

import graph.model.RectangleModel;
import javafx.beans.Observable;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import java.util.List;

public class RectangleAdapter implements ShapeAdapter {
    private final RectangleModel model;
    private static final double MIN_DIM = 10.0;

    public RectangleAdapter(RectangleModel model) {
        this.model = model;
    }

    @Override
    public void resize(String handleName, Point2D p) {
        if (handleName.contains("E")) {
            model.widthProperty().set(Math.max(MIN_DIM, p.getX() - model.xProperty().get()));
        }

        if (handleName.contains("S")) {
            model.heightProperty().set(Math.max(MIN_DIM, p.getY() - model.yProperty().get()));
        }

        if (handleName.contains("W")) {
            double oldRight = model.xProperty().get() + model.widthProperty().get();
            double newX = Math.min(p.getX(), oldRight - MIN_DIM);
            model.xProperty().set(newX);
            model.widthProperty().set(oldRight - newX);
        }

        if (handleName.contains("N")) {
            double oldBottom = model.yProperty().get() + model.heightProperty().get();
            double newY = Math.min(p.getY(), oldBottom - MIN_DIM);
            model.yProperty().set(newY);
            model.heightProperty().set(oldBottom - newY);
        }
    }

    // Delegation an das Model
    @Override public Point2D getPosition() { return new Point2D(model.xProperty().get(), model.yProperty().get()); }
    @Override public void setPosition(double x, double y) { model.xProperty().set(x); model.yProperty().set(y); }
    @Override public double getWidth() { return model.widthProperty().get(); }
    @Override public double getHeight() { return model.heightProperty().get(); }
    @Override public void setWidth(double w) { model.widthProperty().set(w); }
    @Override public void setHeight(double h) { model.heightProperty().set(h); }

    @Override
    public Point2D getCenter() {
        return new Point2D(model.xProperty().get() + model.widthProperty().get() / 2,
                model.yProperty().get() + model.heightProperty().get() / 2);
    }

    @Override
    public void setCenter(double centerX, double centerY) {
        model.xProperty().set(centerX - model.widthProperty().get() / 2);
        model.yProperty().set(centerY - model.heightProperty().get() / 2);
    }

    @Override
    public Cursor getHandleCursor(String name) {
        return switch (name) {
            case "NW", "SE" -> Cursor.NW_RESIZE;
            case "NE", "SW" -> Cursor.NE_RESIZE;
            case "N", "S"   -> Cursor.N_RESIZE;
            case "E", "W"   -> Cursor.E_RESIZE;
            default -> Cursor.DEFAULT;
        };
    }

    // Diese Methode wird für die Positionierung der ResizeHandles auf dem UI-Layer genutzt
    @Override
    public Point2D getHandlePosition(String handleName) {
        double x = model.xProperty().get();
        double y = model.yProperty().get();
        double w = model.widthProperty().get();
        double h = model.heightProperty().get();

        return switch (handleName) {
            case "NW" -> new Point2D(x, y);
            case "N"  -> new Point2D(x + w / 2, y);
            case "NE" -> new Point2D(x + w, y);
            case "W"  -> new Point2D(x, y + h / 2);
            case "E"  -> new Point2D(x + w, y + h / 2);
            case "SW" -> new Point2D(x, y + h);
            case "S"  -> new Point2D(x + w / 2, y + h);
            case "SE" -> new Point2D(x + w, y + h);
            default -> throw new IllegalArgumentException("Unknown handle: " + handleName);
        };
    }

    @Override
    public Observable[] getHandleDependencies(String handleName) {
        return switch (handleName) {
            // Ecken
            case "NW" -> new Observable[]{model.xProperty(), model.yProperty()};
            case "NE" -> new Observable[]{model.xProperty(), model.yProperty(), model.widthProperty()};
            case "SW" -> new Observable[]{model.xProperty(), model.yProperty(), model.heightProperty()};
            case "SE" -> new Observable[]{model.xProperty(), model.yProperty(), model.widthProperty(), model.heightProperty()};

            // Kanten-Mitten
            case "N"  -> new Observable[]{model.xProperty(), model.yProperty(), model.widthProperty()};
            case "S"  -> new Observable[]{model.xProperty(), model.yProperty(), model.widthProperty(), model.heightProperty()};
            case "W"  -> new Observable[]{model.xProperty(), model.yProperty(), model.heightProperty()};
            case "E"  -> new Observable[]{model.xProperty(), model.yProperty(), model.widthProperty(), model.heightProperty()};

            default -> new Observable[]{model.xProperty(), model.yProperty(), model.widthProperty(), model.heightProperty()};
        };
    }

    @Override public List<String> getHandleNames() { return List.of("NW", "N", "NE", "W", "E", "SW", "S", "SE"); }
}