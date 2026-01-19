package com.example.demo.ui;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class ConnectionDot {
    private final Circle circle;
    private final String pointName;

    public static final double DOT_RADIUS = 5.0;

    public ConnectionDot(String pointName, Group parent) {
        this.pointName = pointName;
        this.circle = new Circle(DOT_RADIUS, Color.LIGHTBLUE);
        this.circle.setStroke(Color.DARKBLUE);
        this.circle.setStrokeWidth(1);
        this.circle.setUserData("CONN_POINT");

        // Cursor-Feedback: Hier beginnt oder endet eine Linie
        this.circle.setCursor(javafx.scene.Cursor.CROSSHAIR);
        this.circle.getProperties().put("pointName", pointName);

        parent.getChildren().add(circle);
    }

    public Circle getNode() { return circle; }
    public String getPointName() { return pointName; }
}