package graph.core.strategy;

import graph.controller.MainController;
import graph.model.CircleModel;
import graph.model.GraphNode;
import graph.core.factory.NodeViewFactory;
import graph.view.GraphView;
import javafx.scene.shape.Circle;

public class CircleCreationStrategy implements NodeCreationStrategy {
    @Override
    public GraphNode create(double x, double y, GraphView view, MainController main) {
        CircleModel model = new CircleModel(x, y);
        main.getGraphModel().getNodes().add(model);

        Circle circleView = NodeViewFactory.createCircleShape(model);

        view.getShapeLayer().getChildren().add(circleView);

        return model;
    }
}