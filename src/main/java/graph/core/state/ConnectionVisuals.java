package graph.core.state;

import graph.model.WaypointModel;
import graph.view.GraphView;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;

import java.util.List;

public class ConnectionVisuals {

    private static final String PREVIEW_LINE_ID = "connection-preview-line";

    /**
     * Aktualisiert oder erstellt die Vorschau-Linie und stellt sicher,
     * dass sie auf dem richtigen Layer liegt.
     */
    public static void updatePreview(GraphView view, Circle startPort, List<WaypointModel> waypoints, MouseEvent event) {
        var uiLayer = view.getUiLayer();

        // 1. Preview-Line finden oder erstellen
        Polyline line = (Polyline) uiLayer.lookup("#" + PREVIEW_LINE_ID);
        if (line == null) {
            line = new Polyline();
            line.setId(PREVIEW_LINE_ID);
            line.setStroke(Color.CORNFLOWERBLUE);
            line.setStrokeWidth(2.0);
            line.getStrokeDashArray().addAll(6.0, 4.0);
            line.setMouseTransparent(true);
            uiLayer.getChildren().add(line);
        }

        line.getPoints().clear();

        // 2. Startpunkt (Mitte des Ports) berechnen
        // Wir transformieren die Port-Mitte in UI-Layer-Koordinaten
        Point2D startP = uiLayer.sceneToLocal(startPort.localToScene(
                startPort.getCenterX(), startPort.getCenterY()
        ));
        line.getPoints().addAll(startP.getX(), startP.getY());

        // 3. Bestehende Wegpunkte hinzufügen
        for (WaypointModel wp : waypoints) {
            line.getPoints().addAll(wp.getX(), wp.getY());
        }

        // 4. Aktuelle Mausposition hinzufügen
        Point2D mouseP = uiLayer.sceneToLocal(event.getSceneX(), event.getSceneY());
        line.getPoints().addAll(mouseP.getX(), mouseP.getY());
    }

    /**
     * Entfernt alle temporären Visualisierungen (Linie & gelbe Punkte).
     */
    public static void removeVisuals(GraphView view) {
        var uiLayer = view.getUiLayer();

        // Linie entfernen
        uiLayer.getChildren().removeIf(node -> PREVIEW_LINE_ID.equals(node.getId()));

        // Gelbe Wegpunkt-Kreise entfernen
        // (Wir identifizieren sie hier über die Farbe, besser wäre eine CSS-Klasse)
        uiLayer.getChildren().removeIf(node ->
                node instanceof Circle c && Color.GOLD.equals(c.getFill())
        );
    }

    /**
     * Erstellt einen neuen gelben Wegpunkt-Kreis für die UI.
     */
    public static void addWaypointCircle(GraphView view, Point2D p) {
        Circle circle = new Circle(p.getX(), p.getY(), 4, Color.GOLD);
        circle.setMouseTransparent(true);
        view.getUiLayer().getChildren().add(circle);
    }
}