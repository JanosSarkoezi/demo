package graph.core.registry;

import graph.core.strategy.NodeCreationStrategy;

import java.util.HashMap;
import java.util.Map;

public class NodeRegistry {
    private static final Map<String, NodeCreationStrategy> registry = new HashMap<>();

    // Hier melden sich neue Tools an
    public static void register(String toolName, NodeCreationStrategy strategy) {
        registry.put(toolName, strategy);
    }

    public static NodeCreationStrategy getStrategy(String toolName) {
        return registry.get(toolName);
    }
}