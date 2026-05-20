package graph.core.command;

import graph.core.model.CoreRegistry;
import graph.core.model.FmcObject;
import java.util.UUID;

public class MoveObjectCommand implements Command {
    private final CoreRegistry registry;
    private final UUID objectId;
    private final double oldX;
    private final double oldY;
    private final double newX;
    private final double newY;

    public MoveObjectCommand(CoreRegistry registry, UUID objectId, double oldX, double oldY, double newX, double newY) {
        this.registry = registry;
        this.objectId = objectId;
        this.oldX = oldX;
        this.oldY = oldY;
        this.newX = newX;
        this.newY = newY;
    }

    @Override
    public void execute() {
        FmcObject obj = registry.getObjects().get(objectId);
        if (obj != null) {
            obj.setX(newX);
            obj.setY(newY);
        }
    }

    @Override
    public void undo() {
        FmcObject obj = registry.getObjects().get(objectId);
        if (obj != null) {
            obj.setX(oldX);
            obj.setY(oldY);
        }
    }
}
