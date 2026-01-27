package graph.core.state;

import graph.controller.MainController;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public record PanningState(
        double startMouseX,
        double startMouseY,
        double startTX,
        double startTY,
        MainController main
) implements InteractionState {

    @Override
    public InteractionState handleMousePressed(MouseEvent event, Pane canvas) {
        return this; // Schon initialisiert
    }

    @Override
    public InteractionState handleMouseDragged(MouseEvent event, Pane canvas) {
        // Differenz berechnen
        double deltaX = event.getX() - startMouseX;
        double deltaY = event.getY() - startMouseY;

        // Welt verschieben
        main.getCanvas().getView().getWorld().setTranslateX(startTX + deltaX);
        main.getCanvas().getView().getWorld().setTranslateY(startTY + deltaY);

        return this; // Wir bleiben im Panning
    }

    @Override
    public InteractionState handleMouseReleased(MouseEvent event, Pane canvas) {
        // Nutzt die neue zentrale Tool-Logik aus dem Interface
        return getNextBaseState(main);
    }
}