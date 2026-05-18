package graph.core.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.UUID;

public class Connection {
    private final UUID id;
    private final UUID sourceId;
    private final UUID targetId;
    private final ObservableList<Double> waypoints = FXCollections.observableArrayList();

    public Connection(UUID sourceId, UUID targetId) {
        this.id = UUID.randomUUID();
        this.sourceId = sourceId;
        this.targetId = targetId;
    }

    public UUID getId() { return id; }
    public UUID getSourceId() { return sourceId; }
    public UUID getTargetId() { return targetId; }
    public ObservableList<Double> getWaypoints() { return waypoints; }
}
