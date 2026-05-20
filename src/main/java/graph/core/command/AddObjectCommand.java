package graph.core.command;

import graph.core.model.CoreRegistry;
import graph.core.model.FmcObject;

public class AddObjectCommand implements Command {
    private final CoreRegistry registry;
    private final FmcObject object;

    public AddObjectCommand(CoreRegistry registry, FmcObject object) {
        this.registry = registry;
        this.object = object;
    }

    @Override
    public void execute() {
        registry.addObject(object);
    }

    @Override
    public void undo() {
        registry.removeObject(object.getId());
    }
}
