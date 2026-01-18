package com.example.demo.model;

import com.example.demo.ui.ShapeAdapter;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class SelectionModel {
    // Die Property hält den aktuellen Adapter des ausgewählten Objekts
    private final ObjectProperty<ShapeAdapter> selectedAdapter = new SimpleObjectProperty<>(null);

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