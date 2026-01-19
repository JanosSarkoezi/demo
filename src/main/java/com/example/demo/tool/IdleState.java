package com.example.demo.tool;

import com.example.demo.ui.ShapeAdapter;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class IdleState implements SelectionState {
    // In IdleState.java
    @Override
    public void onMousePressed(MouseEvent event, SelectionTool tool, Group world) {
        Node hit = event.getPickResult().getIntersectedNode();

        // 1. Logik-Entscheidung: Welcher State ist der nächste?
        SelectionState nextState = null;

        if (event.isControlDown()) {
            if (tool.isConnectionDot(hit)) {
                nextState = new ConnectionState((Circle) hit);
            } else if (tool.isShape(hit)) {
                ShapeAdapter sa = (ShapeAdapter) hit.getUserData();
                tool.setCurrentAdapter(sa);
                tool.showConnectionPoints(world);
                return; // WICHTIG: Hier abbrechen, kein nextState!
            }
        } else if (event.isAltDown()) {
            if (tool.isHandle(hit)) {
                nextState = new ResizeState((Rectangle) hit);
            } else if (tool.isShape(hit)) {
                ShapeAdapter sa = (ShapeAdapter) hit.getUserData();
                tool.setCurrentAdapter(sa);
                tool.showHandles(world);
                return; // WICHTIG: Hier abbrechen!
            }
        } else {
            if (tool.isShape(hit)) {
                ShapeAdapter sa = (ShapeAdapter) hit.getUserData();
                tool.setCurrentAdapter(sa);
                nextState = new MoveState(sa);
                tool.clearHandlesFromUI();
                tool.clearConnectionsFromUI();
            } else {
                nextState = new PanningState();
            }
        }

        // 2. State-Wechsel vollziehen
        if (nextState != null) {
            tool.setCurrentState(nextState);
            nextState.onMousePressed(event, tool, world);
        }
    }

    @Override
    public void onMouseDragged(MouseEvent event, SelectionTool tool, Group world) {
    }

    @Override
    public void onMouseReleased(MouseEvent event, SelectionTool tool, Group world) {
    }
}