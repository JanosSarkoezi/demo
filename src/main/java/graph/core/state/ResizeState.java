package graph.core.state;

import graph.controller.MainController;
import graph.core.adapter.ShapeAdapter;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public record ResizeState(
        ShapeAdapter adapter,
        String handleName,
        Point2D startPos,
        double startW,
        double startH,
        MainController main
) implements InteractionState {

    // Bequemer Konstruktor: Wir sichern die Werte automatisch beim Start
    public ResizeState(ShapeAdapter adapter, String handleName, MainController main) {
        this(
                adapter,
                handleName,
                adapter.getPosition(),
                adapter.getWidth(),
                adapter.getHeight(),
                main
        );
    }

    @Override
    public InteractionState handleMousePressed(MouseEvent event, Pane canvas) {
        return this; // Bereits initialisiert
    }

    @Override
    public InteractionState handleMouseDragged(MouseEvent event, Pane canvas) {
        // Umrechnung in Welt-Koordinaten (wichtig für Zoom/Panning)
        Point2D mouseInWorld = main.getCanvas().getView().getWorld()
                .sceneToLocal(event.getSceneX(), event.getSceneY());

        // Die Logik bleibt im Adapter (Rechteck verschiebt Kanten, Kreis ändert Radius)
        adapter.resize(handleName, mouseInWorld);

        return this;
    }

    @Override
    public InteractionState handleMouseReleased(MouseEvent event, Pane canvas) {
        // Am Ende prüfen wir die Differenz für das Undo-System
        double endW = adapter.getWidth();
        double endH = adapter.getHeight();
        Point2D endPos = adapter.getPosition();

        if (startW != endW || startH != endH || !startPos.equals(endPos)) {
            // Hier würde dein Command/History System getriggert werden
            // main.getHistory().executeCommand(new ResizeCommand(adapter, startPos, startW, startH, endPos, endW, endH));
            System.out.println("Resize beendet: Command erstellt.");
        }

        return getNextBaseState(main);
    }
}