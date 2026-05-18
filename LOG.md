# Projekt-Logbuch: FMC Editor

## 2026-05-18: Architektur-Reset & Bugfixing

### 1. Architektur-Umbau (ID-basiert)
- **Modell-Trennung**: Einführung von `FmcObject` und `Connection` als "Source of Truth" mit eindeutigen UUIDs.
- **CoreRegistry**: Zentrales Register mit integrierter **Bipartit-Prüfung** (Verbindungsregeln).
- **ViewMapper**: Automatisches Rendering und bidirektionales Binding zwischen Modell und JavaFX-Shapes.
- **Clean Code**: Löschung veralteter Klassen (`DrawingModel`) und Umstellung der `EditorStates` auf die Registry.

### 2. Connection-Fixes (Wegpunkte & Anchoring)
- **Path-Support**: `Connection`-Modell speichert nun Wegpunkte; `ConnectionState` extrahiert diese aus der Interaktion.
- **Port-Anchoring**: Fehlerbehebung beim Linien-Versatz ("Springen"). Offsets werden jetzt präzise relativ zum Objekt-Mittelpunkt berechnet.
- **Koordinaten-Konsistenz**: Vereinheitlichung der Koordinatensysteme (Mittelpunkt = 0,0) für alle Shapes im `ViewMapper`.

### Status
- Grundarchitektur ist stabil und reaktiv.
- Bipartite Verbindungen zwischen Kreis und Quadrat funktionieren inklusive Wegpunkten und korrekter Port-Anbindung.
