package graph.core.state;

import graph.controller.MainController;
import graph.model.GraphNode;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class IdleState implements InteractionState {
    private final MainController main;

    public IdleState(MainController main) {
        this.main = main;
    }

    @Override
    public void handleMousePressed(MouseEvent event, Pane canvas) {
        Node hit = event.getPickResult().getIntersectedNode();
        GraphNode model = findModel(hit);

        if (model != null) {
            if (event.isAltDown()) {
                main.getSelectionModel().select(model);
            } else {
                MoveState moveState = new MoveState(model, main);
                main.getCanvas().setCurrentState(moveState);
                moveState.handleMousePressed(event, canvas);
            }
            event.consume();
        } else {
            PanningState panningState = new PanningState(main);
            main.getCanvas().setCurrentState(panningState);

            panningState.handleMousePressed(event, canvas);
            main.getSelectionModel().clear();
        }
    }

    private GraphNode findModel(Node node) {
        if (node == null) return null;
        if (node.getUserData() instanceof GraphNode) return (GraphNode) node.getUserData();
        return findModel(node.getParent());
    }

    @Override public void handleMouseDragged(MouseEvent event, Pane canvas) {}
    @Override public void handleMouseReleased(MouseEvent event, Pane canvas) {}
}