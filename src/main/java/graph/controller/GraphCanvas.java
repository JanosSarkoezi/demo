package graph.controller;

import graph.model.GraphModel;
import graph.state.InteractionState;
import javafx.scene.Group;

public interface GraphCanvas {
    // Zugriff auf die Layer-Struktur
    Group getUiLayer();
    Group getShapeLayer();
    Group getTextLayer();
    Group getWorld();
    Group getConnectionLayer();

    // Zugriff auf die Logik-Komponenten
    SelectionManager getSelectionManager();
    GraphModel getGraphModel();

    // Zustandsverwaltung
    void setCurrentState(InteractionState state);
}