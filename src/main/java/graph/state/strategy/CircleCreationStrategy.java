package graph.state.strategy;

import graph.controller.GraphCanvas;
import graph.model.CircleModel;
import graph.model.GraphNode;
import graph.state.factory.NodeViewFactory;
import javafx.scene.shape.Circle;

public class CircleCreationStrategy implements NodeCreationStrategy {
    @Override
    public GraphNode create(double x, double y, GraphCanvas canvas) {
        CircleModel model = new CircleModel(x, y);
        canvas.getGraphModel().getNodes().add(model);

        Circle circleView = NodeViewFactory.createCircleShape(model);

        canvas.getShapeLayer().getChildren().add(circleView);

        return model;
    }
}