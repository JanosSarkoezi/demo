package graph.model;

public class ConnectionModel {
    private final GraphNode start;
    private final GraphNode end;

    public ConnectionModel(GraphNode start, GraphNode end) {
        // Hier kann man später Validierungslogik einbauen:
        // z.B. nur K -> R oder R -> K erlauben.
        this.start = start;
        this.end = end;
    }

    public GraphNode getStart() { return start; }
    public GraphNode getEnd() { return end; }
}