package graph.state;

import graph.controller.MainController;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class PanningState implements InteractionState {
    private final MainController main;

    private double startMouseX, startMouseY;
    private double startTranslateX, startTranslateY;

    public PanningState(MainController main) {
        this.main = main;
    }

    @Override
    public void handleMousePressed(MouseEvent event, Pane canvas) {
        // Wir merken uns die Startposition der Maus im Canvas
        startMouseX = event.getX();
        startMouseY = event.getY();

        // Und den aktuellen Versatz der "Welt"
        startTranslateX = main.getCanvas().getWorld().getTranslateX();
        startTranslateY = main.getCanvas().getWorld().getTranslateY();
    }

    @Override
    public void handleMouseDragged(MouseEvent event, Pane canvas) {
        // Differenz zwischen Start-Maus und aktueller Maus berechnen
        double deltaX = event.getX() - startMouseX;
        double deltaY = event.getY() - startMouseY;

        // Die gesamte Welt-Gruppe verschieben
        main.getCanvas().getWorld().setTranslateX(startTranslateX + deltaX);
        main.getCanvas().getWorld().setTranslateY(startTranslateY + deltaY);
    }

    @Override
    public void handleMouseReleased(MouseEvent event, Pane canvas) {
        // Zurück in den IdleState wechseln, damit das System wieder auf Klicks reagiert
        main.getCanvas().setCurrentState(new IdleState(main));
    }
}