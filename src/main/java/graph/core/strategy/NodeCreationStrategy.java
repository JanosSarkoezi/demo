package graph.core.strategy;

import graph.controller.GraphCanvas;
import graph.model.GraphNode;

public interface NodeCreationStrategy {
    GraphNode create(double x, double y, GraphCanvas canvas);
}