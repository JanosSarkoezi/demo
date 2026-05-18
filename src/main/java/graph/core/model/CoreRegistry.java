package graph.core.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import java.util.UUID;

public class CoreRegistry {
    private final ObservableMap<UUID, FmcObject> objects = FXCollections.observableHashMap();
    private final ObservableMap<UUID, Connection> connections = FXCollections.observableHashMap();

    public void addObject(FmcObject obj) {
        objects.put(obj.getId(), obj);
    }

    public void removeObject(UUID id) {
        objects.remove(id);
        // Aufräumen: Verbindungen, die an diesem Objekt hängen, müssten auch weg.
        connections.values().removeIf(c -> c.getSourceId().equals(id) || c.getTargetId().equals(id));
    }

    public void addConnection(Connection conn) {
        if (canConnect(conn.getSourceId(), conn.getTargetId())) {
            connections.put(conn.getId(), conn);
        } else {
            throw new IllegalArgumentException("Bipartite Regel verletzt: Verbindung nicht erlaubt.");
        }
    }

    public boolean canConnect(UUID sourceId, UUID targetId) {
        FmcObject source = objects.get(sourceId);
        FmcObject target = objects.get(targetId);

        if (source == null || target == null) return false;

        // Bipartite Regel: Kreis -> Quadrat oder Quadrat -> Kreis
        return source.getType() != target.getType();
    }

    public ObservableMap<UUID, FmcObject> getObjects() { return objects; }
    public ObservableMap<UUID, Connection> getConnections() { return connections; }
}
