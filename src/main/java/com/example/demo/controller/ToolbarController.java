package com.example.demo.controller;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;

public class ToolbarController {
    public enum ToolType { NONE, CIRCLE, RECTANGLE }

    @FXML private ToggleButton circleButton;
    @FXML private ToggleButton rectButton;
    @FXML private ToggleGroup toolGroup;
    @FXML public CheckBox snapToGridCheckbox;
    @FXML public CheckBox stickyCheckbox;

    private final ObjectProperty<ToolType> selectedTool = new SimpleObjectProperty<>(ToolType.NONE);

    @FXML
    public void initialize() {
        // Überwachung der Gruppe
        toolGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle == circleButton) {
                selectedTool.set(ToolType.CIRCLE);
            } else if (newToggle == rectButton) {
                selectedTool.set(ToolType.RECTANGLE);
            } else {
                selectedTool.set(ToolType.NONE);
            }
        });

        // Optional: Logik für Tasten ohne ALT (sobald die Scene verfügbar ist)
        circleButton.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.getAccelerators().put(new KeyCodeCombination(KeyCode.C), () -> circleButton.fire());
                newScene.getAccelerators().put(new KeyCodeCombination(KeyCode.R), () -> rectButton.fire());
            }
        });
    }

    public ObjectProperty<ToolType> selectedToolProperty() { return selectedTool; }
    // Getter, damit der MainController die Properties an den Canvas binden kann
    public BooleanProperty snapToGridProperty() { return snapToGridCheckbox.selectedProperty(); }
    public BooleanProperty stickyProperty() { return stickyCheckbox.selectedProperty(); }
}