package graph.state;

import graph.controller.MainController;
import graph.model.CircleModel;
import graph.model.GraphNode;
import graph.model.RectangleModel;
import graph.state.registry.NodeRegistry;
import graph.state.strategy.NodeCreationStrategy;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class CreateNodeState implements InteractionState {
    private final MainController main;

    public CreateNodeState(MainController main) {
        this.main = main;
    }

    @Override
    public void handleMousePressed(MouseEvent event, Pane canvasPane) {
        Point2D p = main.getCanvas().getShapeLayer().sceneToLocal(event.getSceneX(), event.getSceneY());
        String tool = main.getToolbar().getSelectedTool();
        Node hit = event.getPickResult().getIntersectedNode();
        GraphNode model = findModel(hit);
        if (model != null) return;

        NodeCreationStrategy strategy = NodeRegistry.getStrategy(tool);

        if (strategy != null) {
            strategy.create(p.getX(), p.getY(), main.getCanvas());
        }
    }

    private GraphNode findModel(Node node) {
        if (node == null) return null;
        if (node.getUserData() instanceof GraphNode) return (GraphNode) node.getUserData();
        return findModel(node.getParent());
    }

    @Override public void handleMouseDragged(MouseEvent event, Pane canvas) {}
    @Override public void handleMouseReleased(MouseEvent event, Pane canvas) {
        String selectedTool = main.getToolbar().getSelectedTool();

        if (!selectedTool.equals("NONE")) {
            main.getCanvas().setCurrentState(new CreateNodeState(main));
        } else {
            main.getCanvas().setCurrentState(new IdleState(main));
        }
        event.consume();
    }
}