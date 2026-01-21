package com.example.demo.model;

import com.example.demo.diagram.shape.ShapeAdapter;
import com.example.demo.diagram.connection.SmartConnection;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SelectionModel {
    private final ObjectProperty<ShapeAdapter> selectedAdapter = new SimpleObjectProperty<>(null);
    private final StringProperty statusMessage = new SimpleStringProperty("Bereit");
    private final javafx.collections.ObservableList<SmartConnection> allConnections =
            javafx.collections.FXCollections.observableArrayList();
    private final CommandHistory history = new CommandHistory();

    public CommandHistory getHistory() {
        return history;
    }

    public javafx.collections.ObservableList<SmartConnection> getAllConnections() {
        return allConnections;
    }

    public void addConnection(SmartConnection connection) {
        allConnections.add(connection);
    }

    public StringProperty statusMessageProperty() {
        return statusMessage;
    }

    public void setStatusMessage(String message) {
        statusMessage.set(message);
    }

    public ObjectProperty<ShapeAdapter> selectedAdapterProperty() {
        return selectedAdapter;
    }

    public void setSelectedAdapter(ShapeAdapter adapter) {
        selectedAdapter.set(adapter);
    }

    public ShapeAdapter getSelectedAdapter() {
        return selectedAdapter.get();
    }

    public void clear() {
        selectedAdapter.set(null);
    }
}