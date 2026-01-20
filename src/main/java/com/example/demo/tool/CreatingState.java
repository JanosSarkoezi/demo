package com.example.demo.tool;

import com.example.demo.ui.CircleAdapter;
import com.example.demo.ui.RectangleAdapter;
import com.example.demo.ui.ShapeAdapter;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class CreatingState implements DrawState {
    private final ShapeAdapter adapter;
    private final Point2D startPoint;

    public CreatingState(ShapeAdapter adapter, Point2D startPoint) {
        this.adapter = adapter;
        this.startPoint = startPoint;
    }

    @Override
    public void onMousePressed(MouseEvent e, DrawTool tool, Pane c, Group world) {}

    @Override
    public void onMouseDragged(MouseEvent e, DrawTool tool, Pane c, Group world) {
        Point2D currentPos = world.sceneToLocal(e.getSceneX(), e.getSceneY());

        // Berechnung für Rechtecke (Breite/Höhe) oder Kreise (Radius)
        double width = Math.abs(currentPos.getX() - startPoint.getX());
        double height = Math.abs(currentPos.getY() - startPoint.getY());

        if (adapter instanceof RectangleAdapter ra) {
            // Logik für Rechtecke: Startpunkt ist immer die kleinste Koordinate
            ra.getShape().setX(Math.min(startPoint.getX(), currentPos.getX()));
            ra.getShape().setY(Math.min(startPoint.getY(), currentPos.getY()));
            ra.getShape().setWidth(width);
            ra.getShape().setHeight(height);
        } else if (adapter instanceof CircleAdapter ca) {
            // Radius berechnen (z.B. Abstand vom Zentrum)
            double radius = startPoint.distance(currentPos);
            ca.getShape().setRadius(radius);
        }
        e.consume();
    }

    @Override
    public void onMouseReleased(MouseEvent e, DrawTool tool, Pane c, Group world) {
        // Zurück in den Idle-Zustand für die nächste Form
        tool.setCurrentState(new DrawIdleState());
        e.consume();
    }
}
