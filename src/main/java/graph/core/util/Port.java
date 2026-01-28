package graph.core.util;

import javafx.geometry.Point2D;
import javafx.scene.Node;

public record Port(Point2D position, Node owner, PortPosition side) {}