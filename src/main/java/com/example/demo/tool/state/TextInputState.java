package com.example.demo.tool.state;

import com.example.demo.diagram.shape.RectangleAdapter;
import com.example.demo.tool.SelectionTool;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

public class TextInputState implements SelectionState {
    private final RectangleAdapter adapter;

    public TextInputState(RectangleAdapter adapter) {
        this.adapter = adapter;
        this.adapter.applyEditMode(); // TextArea scharf schalten
    }

    @Override
    public void onMousePressed(MouseEvent e, SelectionTool tool, Group world) {
        Node hit = e.getPickResult().getIntersectedNode();

        if (hit != adapter.getTextArea() && hit != adapter.getShape()) {
            adapter.applyDisplayMode();
            tool.setCurrentState(new IdleState());

            // Wichtig: Den Klick weiterreichen, damit man z.B.
            // sofort ein anderes Shape auswählen kann
            tool.onMousePressed(e, null, world);
        }
    }

    @Override public void onMouseDragged(MouseEvent e, SelectionTool tool, Group world) {}
    @Override public void onMouseReleased(MouseEvent e, SelectionTool tool, Group world) {}
}