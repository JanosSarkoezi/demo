package graph.core.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.UUID;

public class Connection {
    private final UUID id;
    private final UUID sourceId;
    private final UUID targetId;
    private final double sourceOffsetX;
    private final double sourceOffsetY;
    private final double targetOffsetX;
    private final double targetOffsetY;
    private final ObservableList<UUID> waypointIds = FXCollections.observableArrayList();

    public Connection(UUID sourceId, double soX, double soY, UUID targetId, double toX, double toY) {
        this.id = UUID.randomUUID();
        this.sourceId = sourceId;
        this.sourceOffsetX = soX;
        this.sourceOffsetY = soY;
        this.targetId = targetId;
        this.targetOffsetX = toX;
        this.targetOffsetY = toY;
    }

    public UUID getId() { return id; }
    public UUID getSourceId() { return sourceId; }
    public UUID getTargetId() { return targetId; }
    public double getSourceOffsetX() { return sourceOffsetX; }
    public double getSourceOffsetY() { return sourceOffsetY; }
    public double getTargetOffsetX() { return targetOffsetX; }
    public double getTargetOffsetY() { return targetOffsetY; }
    public ObservableList<UUID> getWaypointIds() { return waypointIds; }
}
