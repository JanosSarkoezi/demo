package graph.core.factory;

import graph.model.CircleModel;
import graph.model.GraphNode;
import graph.model.RectangleModel;
import graph.core.adapter.CircleAdapter;
import graph.core.adapter.RectangleAdapter;
import graph.core.adapter.ShapeAdapter;

public class AdapterFactory {
    public static ShapeAdapter createAdapter(GraphNode model) {
        if (model instanceof RectangleModel rectangle) {
            return new RectangleAdapter(rectangle);
        } else if (model instanceof CircleModel circle) {
            return new CircleAdapter(circle);
        }
        throw new IllegalArgumentException("Kein Adapter für Modelltyp: " + model.getClass());
    }
}