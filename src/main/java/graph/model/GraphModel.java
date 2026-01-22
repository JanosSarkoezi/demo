package graph.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class GraphModel {
    private final ObservableList<GraphNode> nodes = FXCollections.observableArrayList();
    private final ObservableList<ConnectionModel> connections = FXCollections.observableArrayList();

    public ObservableList<GraphNode> getNodes() { return nodes; }
    public ObservableList<ConnectionModel> getConnections() { return connections; }
}