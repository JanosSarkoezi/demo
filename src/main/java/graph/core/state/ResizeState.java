package graph.core.state;

import graph.controller.MainController;
import graph.core.adapter.ShapeAdapter;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.input.MouseEvent;

public class ResizeState implements InteractionState {
    private final ShapeAdapter adapter;
    private final String handleName;
    private final MainController main;

    // Snapshots für das Undo-System
    private Point2D startPos;
    private double startW;
    private double startH;

    public ResizeState(ShapeAdapter adapter, String handleName, MainController main) {
        this.adapter = adapter;
        this.handleName = handleName;
        this.main = main;

        // Initialzustand VOR der Änderung sichern
        this.startPos = adapter.getPosition();
        this.startW = adapter.getWidth();
        this.startH = adapter.getHeight();
    }

    @Override
    public void handleMousePressed(MouseEvent event, Pane canvas) {
        event.consume();
    }

    @Override
    public void handleMouseDragged(MouseEvent event, Pane canvas) {
        // Mausposition in Welt-Koordinaten umrechnen (wichtig für Zoom/Panning)
        Point2D mouseInWorld = main.getCanvas().getWorld().sceneToLocal(event.getSceneX(), event.getSceneY());

        // Die Logik steckt im Adapter (Rechteck verschiebt Kanten, Kreis ändert Radius)
        adapter.resize(handleName, mouseInWorld);

        event.consume();
    }

    @Override
    public void handleMouseReleased(MouseEvent event, Pane canvas) {
        // Endzustand prüfen
        double endW = adapter.getWidth();
        double endH = adapter.getHeight();
        Point2D endPos = adapter.getPosition();

        // Nur ein Command erstellen, wenn sich die Größe oder Position wirklich geändert hat
//        if (startW != endW || startH != endH || !startPos.equals(endPos)) {
//            ResizeCommand cmd = new ResizeCommand(
//                    adapter,
//                    startPos, startW, startH,
//                    endPos, endW, endH
//            );
//            // In die History pushen (Undo/Redo Support)
//            main.getHistory().executeCommand(cmd);
//        }

        // Zurück in den IdleState (Wartet auf neue Aktionen)
        main.getCanvas().setCurrentState(new IdleState(main));
        event.consume();
    }
}