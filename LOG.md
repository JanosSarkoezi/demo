# Projekt-Logbuch: FMC Editor Refactoring

## 2026-05-18: Architektur-Umbau auf ID-basiertes Registry-System

### Ziel
Reduzierung der Code-Komplexität durch strikte Trennung von Datenmodell (FmcObject) und UI-Repräsentation (JavaFX Shapes) sowie Implementierung von FMC-spezifischen Regeln (Bipartiter Graph).

### Durchgeführte Änderungen

#### 1. Bereinigung der Git-Historie
- Die letzten drei Commits auf dem Branch `try1` wurden von "asdf"-Nachrichten auf aussagekräftige Beschreibungen umgeschrieben (`git rebase`).

#### 2. Neues Datenmodell (`graph.core.model`)
- **`FmcType`**: Enum zur Unterscheidung zwischen `KREIS` und `QUADRAT`.
- **`FmcObject`**: Zentrales Modell für Knoten mit UUID und JavaFX-Position-Properties für bidirektionales Binding.
- **`Connection`**: Modell für Verbindungen zwischen Objekten via IDs.
- **`CoreRegistry`**: Das zentrale Register. Implementiert die **Bipartit-Prüfung** (Verbindungen nur zwischen unterschiedlichen Typen erlaubt).

#### 3. Brücke zwischen Modell und View (`graph.core.view`)
- **`ViewMapper`**: Implementierung eines Beobachters, der die `CoreRegistry` überwacht und automatisch JavaFX-Shapes erzeugt/entfernt.
- **Dynamic Binding**: Verbindungen (Polylines) folgen nun automatisch den Objekten, wenn diese verschoben werden.

#### 4. Refactoring der Controller & States
- **`CanvasController`**: Initialisiert nun `CoreRegistry` und `ViewMapper`. Die manuelle Shape-Verwaltung wurde entfernt.
- **`IdleCircleState` / `IdleRectangleState`**: Erstellen jetzt logische `FmcObjects` in der Registry, statt direkt Shapes in die View zu pushen.
- **`ConnectionState`**: Nutzt jetzt die Registry zur Erstellung von Verbindungen und validiert diese (Bipartit-Check).

#### 5. System-Konfiguration
- **`GEMINI.md`**: Projekt-Richtlinien für die neue Architektur erstellt.
- **`module-info.java`**: Neue Packages für den Export freigegeben.
- **Obsoleszenz**: `DrawingModel` wurde gelöscht, da die Funktionalität nun in der `CoreRegistry` liegt.

### Status
Die Kern-Architektur ist stabil und auf das ID-System umgestellt. Die Interaktionslogik (Panning, Moving, Connecting) nutzt nun das Modell als "Source of Truth".
