package graph.core.strategy;

import graph.controller.MainController;
import graph.model.GraphNode;
import graph.view.GraphView;

public interface NodeCreationStrategy {
    // Liefert das Modell zurück, falls der State danach etwas damit tun will (z.B. Selektieren)
    GraphNode create(double x, double y, GraphView view, MainController main);
}