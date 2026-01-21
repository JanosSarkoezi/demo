package com.example.demo.model;

/**
 * Basis für alle rückgängig machbaren Aktionen.
 */
public interface Command {
    void execute();
    void undo();
}