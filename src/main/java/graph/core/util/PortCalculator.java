package graph.core.util;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class PortCalculator {

    public static List<Port> getPortsForNode(Node node) {
        List<Port> ports = new ArrayList<>();

        if (node instanceof Circle c) {
            double worldCX = c.getCenterX() + c.getTranslateX();
            double worldCY = c.getCenterY() + c.getTranslateY();
            double r = c.getRadius();

            ports.add(new Port(new Point2D(worldCX, worldCY - r), node, PortPosition.TOP));
            ports.add(new Port(new Point2D(worldCX, worldCY + r), node, PortPosition.BOTTOM));
            ports.add(new Port(new Point2D(worldCX - r, worldCY), node, PortPosition.LEFT));
            ports.add(new Port(new Point2D(worldCX + r, worldCY), node, PortPosition.RIGHT));
        }
        else if (node instanceof Rectangle r) {
            // Auch hier: Basis-Ecke + aktuelle Verschiebung
            double worldX = r.getX() + r.getTranslateX();
            double worldY = r.getY() + r.getTranslateY();
            double w = r.getWidth();
            double h = r.getHeight();

            ports.add(new Port(new Point2D(worldX + w / 2, worldY), node, PortPosition.TOP));
            ports.add(new Port(new Point2D(worldX + w / 2, worldY + h), node, PortPosition.BOTTOM));
            ports.add(new Port(new Point2D(worldX, worldY + h / 2), node, PortPosition.LEFT));
            ports.add(new Port(new Point2D(worldX + w, worldY + h / 2), node, PortPosition.RIGHT));
        }

        return ports;
    }
}