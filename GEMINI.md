# FMC Editor Projekt-Richtlinien

Dieses Dokument beschreibt die Kernarchitektur und die Design-Prinzipien für den neuen FMC-Editor. Diese Regeln sind bindend für alle Implementierungen, um Codequalität, Testbarkeit und Erweiterbarkeit zu garantieren.

## 1. Kern-Architektur: ID-basiertes Registry-System

Um die Trennung von Datenmodell und visueller Darstellung (JavaFX) strikt einzuhalten, wird ein ID-basiertes System verwendet. Jedes logische Objekt im Editor besitzt eine eindeutige UUID.

### FmcObject (Das Datenmodell)
- Speichert ausschließlich die fachlichen Daten: `UUID id`, `FmcType type` (KREIS, QUADRAT), `double x`, `double y` sowie die zugewiesene `UUID layerId`.
- Besitzt keinerlei Referenzen auf JavaFX-Komponenten oder Farben.
- Dient als "Source of Truth" für die Position, den Typ und die Zugehörigkeit eines Objekts.

### Connection (Die Verbindung)
- Speichert die logische Verbindung zwischen zwei `FmcObjects` via IDs: `sourceId`, `targetId`.
- Verwaltet optional eine geordnete Liste von Wegpunkten (Waypoints) für Polygonzüge.
- Bipartite Regel: Eine Verbindung darf im FMC-Kontext nur zwischen unterschiedlichen Typen (z.B. Kreis zu Quadrat) existieren.

### CoreRegistry (Das Gehirn)
- Die zentrale Instanz zur Verwaltung aller Objekte, Verbindungen und Layer.
- Hält die Maps für Objekte, Verbindungen und Layer-Definitionen.
- Bietet Methoden zum Hinzufügen, Entfernen und Validieren (z.B. Bipartit-Check).
- Alle Änderungen am Graphen müssen über Commands an der Registry ausgeführt werden.

---

## 2. Layer-System (Mehrschichtigkeit)

Das Layer-System erlaubt es, Objekte und Verbindungen auf verschiedenen logischen Ebenen zu organisieren.

- **Layer-Datenmodell**: Jeder Layer besitzt eine `UUID`, einen Namen (z.B. "Systemgrenze", "Kanäle"), eine Z-Ordnung (Z-Index), ein Sichtbarkeits-Flag (`visible`) und ein Sperr-Flag (`locked`).
- **Aktiver Layer**: Es gibt immer genau einen aktiven Layer. Neue Objekte werden automatisch dem aktiven Layer hinzugefügt.
- **Sichtbarkeit und Interaktion**: Objekte auf unsichtbaren Layern werden nicht gerendert. Objekte auf gesperrten Layern sind sichtbar, können aber nicht verschoben, gelöscht oder editiert werden.
- **Verbindungen**: Eine Verbindung kann layerübergreifend sein, gehört standardmäßig jedoch dem Layer des Quellobjekts an.

---

## 3. Command Pattern (Befehlsmuster für Undo/Redo)

Sämtliche zustandsverändernden Operationen auf dem Datenmodell müssen über das Command Pattern gekapselt werden, um ein sauberes Undo/Redo-System zu ermöglichen.

- **Command Interface**: 
  ```java
  public interface Command {
      void execute();
      void undo();
  }
  ```
- **CommandHistory**: Verwaltet zwei Stacks (`undoStack` und `redoStack`) für die Ausführung und Rücknahme von Befehlen.
- **Konkrete Commands**:
  - `AddObjectCommand`: Erstellt ein neues `FmcObject` auf dem aktiven Layer.
  - `RemoveObjectCommand`: Löscht ein Objekt und alle daran hängenden Verbindungen.
  - `MoveObjectCommand`: Verschiebt ein oder mehrere Objekte (unterstützt Zusammenfassung von Drag-Vorgängen).
  - `ConnectObjectsCommand`: Erstellt eine Verbindung zwischen zwei Objekten nach erfolgreicher Bipartit-Validierung.
  - `AddWaypointCommand`: Fügt einer Verbindung einen neuen Zwischenpunkt hinzu.
  - `MoveWaypointCommand`: Verschiebt einen Verbindungspunkt.
- **Keine direkten Registry-Modifikationen**: UI-Controller rufen niemals direkt modifizierende Methoden der Registry auf, sondern reichen Commands an den `CommandHistory`-Manager weiter.

---

## 4. Strategy Pattern (Strategiemuster für flexible Verbindungslayouts)

Das Zeichnen und Berechnen von Verbindungslinien wird über das Strategy Pattern entkoppelt. Dies ermöglicht es, das Routing-Verhalten dynamisch zur Laufzeit zu ändern.

- **RoutingStrategy Interface**:
  ```java
  public interface RoutingStrategy {
      Path calculatePath(FmcObject source, FmcObject target, List<Point2D> waypoints);
  }
  ```
- **Konkrete Strategien**:
  - `DirectRoutingStrategy`: Zeichnet eine direkte gerade Linie zwischen den Objekten (bzw. durch die Wegpunkte).
  - `OrthogonalRoutingStrategy`: Berechnet einen rechtwinkligen (manhattan-artigen) Pfad zwischen den Objekten und Wegpunkten.
- **Verwendung**: Der `ViewMapper` nutzt die aktuell aktive `RoutingStrategy`, um den visuellen Pfad (JavaFX `Path` oder `Polyline`) für jede Verbindung zu berechnen und darzustellen.

---

## 5. Arbeitsweise & Konventionen

- **Keine Daten in UI-Nodes**: Es dürfen keine fachlichen Daten (außer zur Identifikation die ID) in den `Properties` von JavaFX-Nodes gespeichert werden.
- **Interaktionen**: Mouse-Events werden von den `EditorStates` entgegengenommen, die dann entsprechende Commands erzeugen und ausführen.
- **Bipartite Validierung**: Bevor ein `ConnectObjectsCommand` ausgeführt wird, muss die `CoreRegistry` prüfen, ob die Verbindung nach FMC-Regeln erlaubt ist.
- **Zustandsmanagement**: Das bestehende State-Pattern (`EditorState`) bleibt für die Interaktionslogik (Panning, Dragging, Connection-Creation) erhalten, interagiert aber ausschließlich über das Command-System mit der Registry.
- **Bewegbare Verbindungspunkte**: Wegpunkte (Waypoints) einer Verbindung müssen in der UI als interaktive Handles gerendert werden. Wenn ein Handle gezogen wird, wird ein entsprechendes Command zur Positionsaktualisierung ausgelöst.
