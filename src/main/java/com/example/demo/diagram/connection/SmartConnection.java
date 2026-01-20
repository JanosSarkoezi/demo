package com.example.demo.diagram.connection;

import com.example.demo.diagram.shape.ShapeAdapter;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;

public class SmartConnection extends Group {
    private final Polyline line = new Polyline();
    private final Polygon arrowhead = new Polygon();

    private ShapeAdapter startAdapter;
    private String startPointName;
    private ShapeAdapter endAdapter;
    private String endPointName;

    public void setStartAdapter(ShapeAdapter startAdapter) {
        this.startAdapter = startAdapter;
    }

    public void setStartPointName(String startPointName) {
        this.startPointName = startPointName;
    }

    public void setEndAdapter(ShapeAdapter endAdapter) {
        this.endAdapter = endAdapter;
    }

    public void setEndPointName(String endPointName) {
        this.endPointName = endPointName;
    }

    public Polygon getArrowhead() {
        return arrowhead;
    }

    public ShapeAdapter getStartAdapter() {
        return startAdapter;
    }

    public String getStartPointName() {
        return startPointName;
    }

    public ShapeAdapter getEndAdapter() {
        return endAdapter;
    }

    public String getEndPointName() {
        return endPointName;
    }

    public SmartConnection(Point2D start, Point2D end) {
        // Linie konfigurieren
        line.setStroke(Color.BLACK);
        line.setStrokeWidth(2);

        // Initial zwei Punkte: Start und Ende
        line.getPoints().addAll(
                start.getX(), start.getY(),
                end.getX(), end.getY()
        );

        // Pfeilspitze (Dreieck)
        arrowhead.getPoints().addAll(0.0, 0.0, -10.0, -5.0, -10.0, 5.0);
        arrowhead.setFill(Color.BLACK);

        getChildren().addAll(line, arrowhead);
        updateArrowhead();
    }

    /**
     * Aktualisiert die Positionen der Linien-Endpunkte.
     * Da wir später Knickpunkte dazwischen haben können,
     * ändern wir immer nur das erste und das letzte Paar.
     */
    public void updatePoints(Point2D start, Point2D end) {
        ObservableList<Double> pts = line.getPoints();

        // Erster Punkt (Start)
        pts.set(0, start.getX());
        pts.set(1, start.getY());

        // Letzter Punkt (Ziel/Maus)
        int size = pts.size();
        pts.set(size - 2, end.getX());
        pts.set(size - 1, end.getY());

        updateArrowhead();
    }

    /**
     * Die Magie für später: Ein neues Gelenk einfügen.
     */
    public void insertVertex(double x, double y) {
        int size = line.getPoints().size();
        // Wir fügen den Punkt vor dem Endpunkt ein
        line.getPoints().add(size - 2, x);
        line.getPoints().add(size - 2, y);
    }

    private void updateArrowhead() {
        ObservableList<Double> pts = line.getPoints();
        int size = pts.size();

        double endX = pts.get(size - 2);
        double endY = pts.get(size - 1);
        double prevX = pts.get(size - 4);
        double prevY = pts.get(size - 3);

        arrowhead.setTranslateX(endX);
        arrowhead.setTranslateY(endY);

        // Winkel berechnen, damit die Spitze in Linienrichtung zeigt
        double angle = Math.toDegrees(Math.atan2(endY - prevY, endX - prevX));
        arrowhead.setRotate(angle);
    }

    public Polyline getLine() { return line; }
}