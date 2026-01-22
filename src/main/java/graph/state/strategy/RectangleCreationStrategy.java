package graph.state.strategy;

import graph.controller.GraphCanvas;
import graph.model.GraphNode;
import graph.model.RectangleModel;
import graph.state.factory.NodeViewFactory;
import javafx.scene.Node;

public class RectangleCreationStrategy implements NodeCreationStrategy {
    @Override
    public GraphNode create(double x, double y, GraphCanvas canvas) {
        RectangleModel model = new RectangleModel(x, y);
        model.widthProperty().set(60);
        model.heightProperty().set(60);
        canvas.getGraphModel().getNodes().add(model);

        // View für das Rechteck -> ShapeLayer
        Node rectView = NodeViewFactory.createRectangleShape(model);
        canvas.getShapeLayer().getChildren().add(rectView);

        // View für den Text -> TextLayer
        // (Hier müsstest du die Factory evtl. aufteilen)
        Node textView = NodeViewFactory.createNodeTextArea(model);
        canvas.getTextLayer().getChildren().add(textView);

        return model;
    }
}