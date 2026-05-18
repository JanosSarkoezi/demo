# FMC Editor Projekt-Richtlinien

Dieses Dokument beschreibt die Kernarchitektur und die Design-Prinzipien für den FMC-Editor. Diese Regeln sind bindend für alle zukünftigen Implementierungen, um die Komplexität des Projekts beherrschbar zu halten.

## Kern-Architektur: ID-basiertes Registry-System

Um die Trennung von Datenmodell und visueller Darstellung (JavaFX) strikt einzuhalten, wird ein ID-basiertes System verwendet. Jedes logische Objekt im Editor besitzt eine eindeutige UUID.

### 1. FmcObject (Das Datenmodell)
- Speichert ausschließlich die fachlichen Daten: `UUID id`, `FmcType type` (KREIS, QUADRAT), `double x`, `double y`.
- Besitzt keinerlei Referenzen auf JavaFX-Komponenten oder Farben.
- Dient als "Source of Truth" für die Position und den Typ eines Objekts.

### 2. Connection (Die Verbindung)
- Speichert die logische Verbindung zwischen zwei `FmcObjects` via IDs: `sourceId`, `targetId`.
- Verwaltet optional eine Liste von Wegpunkten (Waypoints) für Polygonzüge.
- Bipartite Regel: Eine Verbindung darf im FMC-Kontext nur zwischen unterschiedlichen Typen (z.B. Kreis zu Quadrat) existieren.

### 3. CoreRegistry (Das Gehirn)
- Die zentrale Instanz zur Verwaltung aller Objekte.
- Hält zwei Maps:
  - `Map<UUID, FmcObject>`
  - `Map<UUID, Connection>`
- Bietet Methoden zum Hinzufügen, Entfernen und Validieren (z.B. Bipartit-Check).
- Alle Änderungen am Graphen müssen über die Registry erfolgen.

### 4. ViewMapper (Der Übersetzer)
- Überwacht die `CoreRegistry`.
- Erzeugt für jedes neue Objekt in der Registry die entsprechende visuelle Repräsentation in JavaFX.
- Synchronisiert Änderungen (z.B. Positions-Updates) zwischen Model und View.
- Stellt sicher, dass die UI-Eigenschaften (Farben, Stroke-Width) zentral gesteuert werden und nicht im Modell liegen.

## Arbeitsweise & Konventionen

- **Keine Daten in UI-Nodes:** Es dürfen keine fachlichen Daten (außer zur Identifikation die ID) in den `Properties` von JavaFX-Nodes gespeichert werden.
- **Interaktionen:** Mouse-Events werden von den `EditorStates` entgegengenommen, die dann die entsprechenden Befehle an die `CoreRegistry` senden.
- **Bipartite Validierung:** Bevor eine Verbindung erstellt wird, muss die `CoreRegistry` prüfen, ob die Verbindung nach FMC-Regeln erlaubt ist.
- **Zustandsmanagement:** Das bestehende State-Pattern (`EditorState`) bleibt für die Interaktionslogik (Panning, Dragging, Connection-Creation) erhalten, wird aber auf die Registry umgestellt.
