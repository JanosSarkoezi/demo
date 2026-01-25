package graph.model;

import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;

public class ConnectionModel {
    private final GraphNode startNode;
    private final int startPortIndex;
    private final GraphNode endNode;
    private final int endPortIndex;
    private final List<WaypointModel> waypoints; // Die gelben Punkte als Koordinaten

    public ConnectionModel(GraphNode start, int sIdx, GraphNode end, int eIdx, List<WaypointModel> waypoints) {
        this.startNode = start;
        this.startPortIndex = sIdx;
        this.endNode = end;
        this.endPortIndex = eIdx;
        this.waypoints = new ArrayList<>(waypoints);
    }

    public GraphNode getStartNode() {
        return startNode;
    }

    public int getStartPortIndex() {
        return startPortIndex;
    }

    public GraphNode getEndNode() {
        return endNode;
    }

    public int getEndPortIndex() {
        return endPortIndex;
    }

    public List<WaypointModel> getWaypoints() {
        return waypoints;
    }
}