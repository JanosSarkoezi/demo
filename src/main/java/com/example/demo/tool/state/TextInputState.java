package com.example.demo.tool.state;

import com.example.demo.diagram.shape.RectangleAdapter;
import com.example.demo.model.Command;
import com.example.demo.model.TextChangeCommand;
import com.example.demo.tool.SelectionTool;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

public class TextInputState implements SelectionState {
    private final RectangleAdapter adapter;
    private final String initialText;

    public TextInputState(RectangleAdapter adapter) {
        this.adapter = adapter;
        this.initialText = adapter.getText();
        this.adapter.applyEditMode();
    }

    @Override
    public void onMousePressed(MouseEvent e, SelectionTool tool, Group world) {
        Node hit = e.getPickResult().getIntersectedNode();

        if (hit != adapter.getTextArea() && hit != adapter.getShape()) {
            String finalText = adapter.getText();

            // Wenn der Text geändert wurde -> Command erstellen
            if (!initialText.equals(finalText)) {
                Command textCmd = new TextChangeCommand(adapter, initialText, finalText);
                tool.getSelectionModel().getHistory().executeCommand(textCmd);
            }

            adapter.applyDisplayMode();
            tool.setCurrentState(new IdleState());
            tool.onMousePressed(e, null, world);
        }
    }

    @Override public void onMouseDragged(MouseEvent e, SelectionTool tool, Group world) {}
    @Override public void onMouseReleased(MouseEvent e, SelectionTool tool, Group world) {}
}