package graph.core.strategy;

import graph.controller.MainController;
import graph.model.GraphNode;
import graph.model.RectangleModel;
import graph.core.factory.NodeViewFactory;
import graph.view.GraphView;
import javafx.scene.Node;

public class RectangleCreationStrategy implements NodeCreationStrategy {
    @Override
    public GraphNode create(double x, double y, GraphView view, MainController main) {
        RectangleModel model = new RectangleModel(x, y);
        model.widthProperty().set(60);
        model.heightProperty().set(60);
        main.getGraphModel().getNodes().add(model);

        // View für das Rechteck -> ShapeLayer
        Node rectView = NodeViewFactory.createRectangleShape(model);
        view.getShapeLayer().getChildren().add(rectView);

        // View für den Text -> TextLayer
        // (Hier müsstest du die Factory evtl. aufteilen)
        Node textView = NodeViewFactory.createNodeTextArea(model);
        view.getTextLayer().getChildren().add(textView);

        return model;
    }
}