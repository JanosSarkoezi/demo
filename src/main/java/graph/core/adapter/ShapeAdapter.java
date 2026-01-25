package graph.core.adapter;

import javafx.beans.Observable;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import java.util.List;

public interface ShapeAdapter {
    // Grundlegende Geometrie (Delegation an das jeweilige Model)
    Point2D getPosition();
    void setPosition(double x, double y);

    double getWidth();
    double getHeight();
    void setWidth(double w);
    void setHeight(double h);

    Point2D getCenter();
    void setCenter(double centerX, double centerY);

    // Die "Resize-Logik"
    void resize(String handleName, Point2D mousePos);

    // Hilfsmittel für die UI (ResizeHandles auf dem uiLayer)
    List<String> getHandleNames();
    Point2D getHandlePosition(String handleName);
    Cursor getHandleCursor(String handleName);
    Observable[] getHandleDependencies(String handleName);

    int getPortCount();
    Point2D getPortPosition(int index);
}