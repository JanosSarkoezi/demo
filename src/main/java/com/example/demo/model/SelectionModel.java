package com.example.demo.model;

import com.example.demo.ui.ShapeAdapter;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SelectionModel {
    private final ObjectProperty<ShapeAdapter> selectedAdapter = new SimpleObjectProperty<>(null);
    private final StringProperty statusMessage = new SimpleStringProperty("Bereit");

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