package graph.core.command;

import graph.core.model.Connection;
import graph.core.model.CoreRegistry;
import graph.core.model.FmcObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RemoveObjectCommand implements Command {
    private final CoreRegistry registry;
    private final UUID objectId;
    private FmcObject removedObject;
    private final List<Connection> removedConnections = new ArrayList<>();

    public RemoveObjectCommand(CoreRegistry registry, UUID objectId) {
        this.registry = registry;
        this.objectId = objectId;
    }

    @Override
    public void execute() {
        removedObject = registry.getObjects().get(objectId);
        if (removedObject == null) return;

        // Finde alle Verbindungen, die an diesem Objekt hängen, und speichere sie für undo
        removedConnections.clear();
        for (Connection conn : registry.getConnections().values()) {
            if (conn.getSourceId().equals(objectId) || conn.getTargetId().equals(objectId)) {
                removedConnections.add(conn);
            }
        }

        // Führe die Löschung in der Registry durch
        registry.removeObject(objectId);
    }

    @Override
    public void undo() {
        if (removedObject == null) return;

        // Objekt wiederherstellen
        registry.addObject(removedObject);

        // Alle betroffenen Verbindungen wiederherstellen
        for (Connection conn : removedConnections) {
            registry.addConnection(conn);
        }
    }
}
