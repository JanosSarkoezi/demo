package graph.state.factory;

import graph.model.CircleModel;
import graph.model.GraphNode;
import graph.model.RectangleModel;
import graph.model.adapter.CircleAdapter;
import graph.model.adapter.RectangleAdapter;
import graph.model.adapter.ShapeAdapter;

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