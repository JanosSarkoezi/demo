package graph.core.state;

import graph.controller.MainController;
import graph.core.registry.NodeRegistry;
import graph.core.strategy.NodeCreationStrategy;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public record CreateNodeState(MainController main) implements InteractionState {

    @Override
    public InteractionState handleMousePressed(MouseEvent event, Pane canvasPane) {
        // Umrechnung der Klick-Position in Welt-Koordinaten
        Point2D p = main.getCanvas().getView().getShapeLayer()
                .sceneToLocal(event.getSceneX(), event.getSceneY());

        String tool = main.getToolbar().getSelectedTool();
        Node hit = event.getPickResult().getIntersectedNode();

        // Prüfung, ob bereits ein Modell an dieser Stelle existiert
        if (findModel(hit) != null) {
            return this;
        }

        // Strategie-Pattern zur Erzeugung des Knotens (Kreis, Rechteck, etc.)
        NodeCreationStrategy strategy = NodeRegistry.getStrategy(tool);
        if (strategy != null) {
            strategy.create(p.getX(), p.getY(), main.getCanvas().getView(), main);
        }

        return this;
    }

    @Override
    public InteractionState handleMouseReleased(MouseEvent event, Pane canvas) {
        // Nutzt die zentrale Logik im Interface, um basierend auf dem
        // ausgewählten Tool zum nächsten Basis-Zustand zu wechseln
        return getNextBaseState(main);
    }

    @Override
    public InteractionState handleMouseDragged(MouseEvent event, Pane canvas) {
        return this;
    }
}