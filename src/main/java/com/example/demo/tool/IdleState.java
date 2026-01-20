package com.example.demo.tool;

import com.example.demo.ui.ShapeAdapter;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class IdleState implements SelectionState {
    @Override
    public void onMousePressed(MouseEvent event, SelectionTool tool, Group world) {
        Node hit = event.getPickResult().getIntersectedNode();
        SelectionState nextState = null;

        if (event.getClickCount() == 2 && tool.isShape(hit)) {
            ShapeAdapter sa = (ShapeAdapter) hit.getUserData();
            tool.editText(sa, world);
            event.consume();
            return;
        }

        // Wenn wir ein Handle treffen, wollen wir IMMER resizen, egal ob Alt gedrückt ist oder nicht.
        if (tool.isHandle(hit)) {
            nextState = new ResizeState((Rectangle) hit);
        } else if (tool.isConnectionDot(hit)) {
            nextState = new ConnectionState((Circle) hit);
        } else if (tool.isShape(hit)) {
            ShapeAdapter sa = (ShapeAdapter) hit.getUserData();
            tool.setCurrentAdapter(sa);

            if (event.isControlDown()) {
                tool.showConnectionPoints(world);
                return; // Wir bleiben im Idle, Dots sind jetzt da
            } else if (event.isAltDown()) {
                tool.showHandles(world);
                return; // Wir bleiben im Idle, Handles sind jetzt da
            } else {
                // Normaler Klick auf Shape -> Bewegen
                nextState = new MoveState(sa);
                tool.clearHandlesFromUI();
                tool.clearConnectionsFromUI();
            }
        } else {
            nextState = new PanningState();
        }

        tool.setCurrentState(nextState);
        nextState.onMousePressed(event, tool, world);
        event.consume(); // Sauber das Event stoppen
    }

    @Override
    public void onMouseDragged(MouseEvent event, SelectionTool tool, Group world) {
    }

    @Override
    public void onMouseReleased(MouseEvent event, SelectionTool tool, Group world) {
    }
}