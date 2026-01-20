package com.example.demo.diagram.shape;

import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.List;

public class RectangleAdapter implements ShapeAdapter {
    private final Rectangle rect;
    private final Label label;

    public RectangleAdapter(Rectangle rect) {
        this.rect = rect;
        this.label = new Label();

        // Konfiguration aus deinem bewährten Code
        label.setMouseTransparent(true);
        label.setWrapText(true);
        label.setAlignment(javafx.geometry.Pos.CENTER);
        label.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        label.setStyle("-fx-background-color: transparent;");
        label.setPadding(new javafx.geometry.Insets(5));

        // Die "Magie": Bindungen statt manueller Updates
        // Position binden
        label.layoutXProperty().bind(rect.xProperty());
        label.layoutYProperty().bind(rect.yProperty());

        // Größe binden (mit 10px Puffer)
        label.prefWidthProperty().bind(rect.widthProperty());
        label.prefHeightProperty().bind(rect.heightProperty());
    }

    @Override public Rectangle getShape() { return rect; }
    @Override public Point2D getPosition() { return new Point2D(rect.getX(), rect.getY()); }
    @Override public void setPosition(double x, double y) { rect.setX(x); rect.setY(y); }
    @Override public double getWidth() { return rect.getWidth(); }
    @Override public double getHeight() { return rect.getHeight(); }
    @Override public void setWidth(double w) { rect.setWidth(w); }
    @Override public void setHeight(double h) { rect.setHeight(h); }

    @Override
    public Point2D getCenter() {
        return new Point2D(rect.getX() + rect.getWidth() / 2, rect.getY() + rect.getHeight() / 2);
    }

    @Override
    public void setCenter(double centerX, double centerY) {
        rect.setX(centerX - rect.getWidth() / 2);
        rect.setY(centerY - rect.getHeight() / 2);
    }

    private static final double MIN_DIM = 10.0;

    @Override
    public void resize(String handleName, Point2D p) {
        if (handleName.contains("E")) rect.setWidth(Math.max(MIN_DIM, p.getX() - rect.getX()));
        if (handleName.contains("S")) rect.setHeight(Math.max(MIN_DIM, p.getY() - rect.getY()));
        if (handleName.contains("W")) {
            double oldRight = rect.getX() + rect.getWidth();
            rect.setX(Math.min(p.getX(), oldRight - MIN_DIM));
            rect.setWidth(oldRight - rect.getX());
        }
        if (handleName.contains("N")) {
            double oldBottom = rect.getY() + rect.getHeight();
            rect.setY(Math.min(p.getY(), oldBottom - MIN_DIM));
            rect.setHeight(oldBottom - rect.getY());
        }
    }

    @Override
    public List<String> getHandleNames() {
        return List.of("NW", "N", "NE", "W", "E", "SW", "S", "SE");
    }

    @Override
    public Cursor getHandleCursor(String name) {
        return switch (name) {
            case "NW" -> Cursor.NW_RESIZE;
            case "SE" -> Cursor.SE_RESIZE;
            case "NE" -> Cursor.NE_RESIZE;
            case "SW" -> Cursor.SW_RESIZE;
            case "N", "S" -> Cursor.N_RESIZE;
            case "E", "W" -> Cursor.E_RESIZE;
            default -> Cursor.DEFAULT;
        };
    }

    @Override
    public Point2D getHandlePosition(String handleName) {
        double x = rect.getX();
        double y = rect.getY();
        double w = rect.getWidth();
        double h = rect.getHeight();

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
    public List<String> getConnectionPointNames() {
        return List.of("N", "S", "E", "W");
    }

    @Override
    public Point2D getConnectionPointPosition(String name) {
        double x = rect.getX();      //
        double y = rect.getY();      //
        double w = rect.getWidth();  //
        double h = rect.getHeight(); //

        return switch (name) {
            case "N" -> new Point2D(x + w / 2, y);
            case "S" -> new Point2D(x + w / 2, y + h);
            case "E" -> new Point2D(x + w, y + h / 2);
            case "W" -> new Point2D(x, y + h / 2);
            default -> throw new IllegalArgumentException("Unbekannter Punkt: " + name);
        };
    }

    public Label getLabel() {
        return label;
    }

    @Override
    public void setText(String value) {
        label.setText(value);
    }

    @Override
    public String getText() {
        return label.getText();
    }
}
