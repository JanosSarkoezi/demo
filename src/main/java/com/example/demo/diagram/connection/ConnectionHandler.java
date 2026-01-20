package com.example.demo.diagram.connection;

import com.example.demo.diagram.shape.ShapeAdapter;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Verwaltet das Erstellen und Manipulieren von Verbindungen für ein Shape.
 */
public class ConnectionHandler {
    private final ShapeAdapter adapter;
    private final Group zoomGroup;

    // Die aktuell im Bau befindliche Verbindung
    private SmartConnection activeConnection;

    // Referenz auf alle existierenden Verbindungen (statisch, damit alle Handler darauf zugreifen)
    private static final List<SmartConnection> ALL_CONNECTIONS = new ArrayList<>();

    public ConnectionHandler(ShapeAdapter adapter, Group zoomGroup) {
        this.adapter = adapter;
        this.zoomGroup = zoomGroup;
    }

    /**
     * Startet eine neue Verbindung von einem ConnectionDot aus.
     */
    public void handleConnectionPress(MouseEvent e, String pointName) {
        // 1. Startposition vom Adapter holen
        Point2D startPos = adapter.getConnectionPointPosition(pointName);

        // 2. Neue SmartConnection erzeugen (Anfangs- und Endpunkt sind gleich)
        activeConnection = new SmartConnection(startPos, startPos);

        // 3. Zur View hinzufügen
        zoomGroup.getChildren().add(activeConnection);

        e.consume();
    }

    /**
     * Aktualisiert die Verbindung während des Ziehens mit der Maus.
     */
    public void handleConnectionDrag(MouseEvent e, String pointName) {
        if (activeConnection == null) return;

        Point2D mousePos = zoomGroup.sceneToLocal(e.getSceneX(), e.getSceneY());
        Point2D startPos = adapter.getConnectionPointPosition(pointName);

        // Hier rufen wir die SmartConnection auf
        activeConnection.updatePoints(startPos, mousePos);
        e.consume();
    }

    /**
     * Schließt den Vorgang ab und prüft, ob die Verbindung eingerastet ist.
     */
    public void handleConnectionRelease(MouseEvent e, String pointName) {
        if (activeConnection == null) return;

        // TODO: Hier prüfen wir später, ob ein Ziel-Dot unter der Maus liegt.
        // Falls kein Ziel gefunden wurde, löschen wir die Linie vorerst wieder:
        if (!isOverTarget(e)) {
            zoomGroup.getChildren().remove(activeConnection);
        } else {
            ALL_CONNECTIONS.add(activeConnection);
            // Hier würde man die Verbindung permanent registrieren
        }

        activeConnection = null;
        e.consume();
    }

    /**
     * Hilfsmethode, um alle Linien zu aktualisieren, die an diesem Shape hängen.
     * Wird vom ShapeTransformHelper aufgerufen, wenn das Shape verschoben wird.
     */
//    public void updateAttachedConnections() {
//        for (SmartConnection conn : ALL_CONNECTIONS) {
//            // Wenn dieses Shape der Startpunkt der Verbindung ist
//            if (conn.getStartAdapter() == this.adapter) {
//                Point2D newStart = adapter.getConnectionPointPosition(conn.getStartPointName());
//                conn.updateStartPoint(newStart);
//            }
//            // Wenn dieses Shape der Endpunkt ist
//            if (conn.getEndAdapter() == this.adapter) {
//                Point2D newEnd = adapter.getConnectionPointPosition(conn.getEndPointName());
//                conn.updateEndPoint(newEnd);
//            }
//        }
//    }

    private boolean isOverTarget(MouseEvent e) {
        // Wir suchen in der zoomGroup nach Objekten an der Mausposition
        // e.getPickResult().getIntersectedNode() ist hier sehr hilfreich
        Node hit = e.getPickResult().getIntersectedNode();

        // Prüfen, ob das getroffene Objekt zu einem ConnectionDot gehört
        // (Dafür könnten wir im ConnectionDot den Node markieren)
        return hit != null && hit.getUserData() instanceof String;
        // Annahme: Wir haben dot.getNode().setUserData("CONN_POINT") gesetzt
    }
}