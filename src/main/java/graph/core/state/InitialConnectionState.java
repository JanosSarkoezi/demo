package graph.core.state;

import graph.controller.MainController;
import graph.model.GraphNode;
import graph.view.GraphView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;

import java.util.ArrayList;

public record InitialConnectionState(MainController main) implements InteractionState {
    @Override
    public InteractionState handleMousePressed(MouseEvent event, Pane canvas) {
        var hit = event.getPickResult().getIntersectedNode();
        GraphNode node = findModel(hit);

        if (hit instanceof Circle port && "port".equals(port.getStyleClass().toString())) {
            return new ActiveConnectionState(
                    (GraphNode) port.getProperties().get("node"),
                    (int) port.getProperties().get("portIndex"),
                    port, new ArrayList<>(), main
            );
        }

        if (node != null) {
            node.setSelected(!node.isSelected());
            main.getConnectionRenderer().updatePorts(main.getGraphModel().getNodes(), (GraphView) canvas);
            return this;
        }

        return getNextBaseState(main);
    }

    @Override
    public InteractionState handleMouseDragged(MouseEvent e, Pane c) {
        return this;
    }

    @Override
    public InteractionState handleMouseReleased(MouseEvent e, Pane c) {
        return this;
    }
}