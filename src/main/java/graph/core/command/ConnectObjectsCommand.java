package graph.core.command;

import graph.core.model.Connection;
import graph.core.model.CoreRegistry;
import graph.core.model.FmcObject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ConnectObjectsCommand implements Command {
    private final CoreRegistry registry;
    private final Connection connection;
    private final List<FmcObject> waypoints = new ArrayList<>();

    public ConnectObjectsCommand(CoreRegistry registry, Connection connection) {
        this.registry = registry;
        this.connection = connection;
        // Speichere die Waypoint-Objekte für den Fall eines Undo/Redo
        for (UUID wpId : connection.getWaypointIds()) {
            FmcObject wp = registry.getObjects().get(wpId);
            if (wp != null) {
                waypoints.add(wp);
            }
        }
    }

    @Override
    public void execute() {
        if (!registry.canConnect(connection.getSourceId(), connection.getTargetId())) {
            throw new IllegalArgumentException("Bipartite Regel verletzt: Verbindung nicht erlaubt.");
        }
        // Stelle sicher, dass alle Wegpunkte in der Registry registriert sind
        for (FmcObject wp : waypoints) {
            if (!registry.getObjects().containsKey(wp.getId())) {
                registry.addObject(wp);
            }
        }
        registry.addConnection(connection);
    }

    @Override
    public void undo() {
        registry.getConnections().remove(connection.getId());
        // Entferne auch die Wegpunkte aus der Registry
        for (FmcObject wp : waypoints) {
            registry.removeObject(wp.getId());
        }
    }
}
