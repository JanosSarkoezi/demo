package com.example.demo.model;

import com.example.demo.diagram.shape.ShapeAdapter;

public class TextChangeCommand implements Command {
    private final ShapeAdapter adapter;
    private final String oldText;
    private final String newText;

    public TextChangeCommand(ShapeAdapter adapter, String oldText, String newText) {
        this.adapter = adapter;
        this.oldText = oldText;
        this.newText = newText;
    }

    @Override
    public void execute() {
        adapter.setText(newText);
    }

    @Override
    public void undo() {
        adapter.setText(oldText);
    }
}