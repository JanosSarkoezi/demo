package graph.core.command;

import graph.core.model.Connection;
import graph.core.model.CoreRegistry;
import java.util.UUID;

public class RemoveConnectionCommand implements Command {
    private final CoreRegistry registry;
    private final UUID connectionId;
    private Connection connection;

    public RemoveConnectionCommand(CoreRegistry registry, UUID connectionId) {
        this.registry = registry;
        this.connectionId = connectionId;
    }

    @Override
    public void execute() {
        connection = registry.getConnections().remove(connectionId);
    }

    @Override
    public void undo() {
        if (connection != null) {
            registry.addConnection(connection);
        }
    }
}
