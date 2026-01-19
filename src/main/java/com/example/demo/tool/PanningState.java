package com.example.demo.tool;

import javafx.scene.Group;
import javafx.scene.input.MouseEvent;

public class PanningState implements SelectionState {
    private double startTranslateX;
    private double startTranslateY;
    private double anchorX;
    private double anchorY;

    @Override
    public void onMousePressed(MouseEvent event, SelectionTool tool, Group world) {
        // Wir merken uns die Startposition der Welt und der Maus
        anchorX = event.getSceneX();
        anchorY = event.getSceneY();
        startTranslateX = world.getTranslateX();
        startTranslateY = world.getTranslateY();

        tool.setTarget(world);
        tool.clearSelection(); // Deselektiert aktuelle Shapes beim Panning
        event.consume();
    }

    @Override
    public void onMouseDragged(MouseEvent event, SelectionTool tool, Group world) {
        // Berechnung der Differenz zur Startposition
        double deltaX = event.getSceneX() - anchorX;
        double deltaY = event.getSceneY() - anchorY;

        // Welt verschieben
        world.setTranslateX(startTranslateX + deltaX);
        world.setTranslateY(startTranslateY + deltaY);
        event.consume();
    }

    @Override
    public void onMouseReleased(MouseEvent event, SelectionTool tool, Group world) {
        tool.setCurrentState(new IdleState());
    }
}