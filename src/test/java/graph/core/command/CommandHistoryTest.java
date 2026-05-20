package graph.core.command;

import graph.core.model.Connection;
import graph.core.model.CoreRegistry;
import graph.core.model.FmcObject;
import graph.core.model.FmcType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CommandHistoryTest {
    private CoreRegistry registry;
    private CommandHistory history;

    @BeforeEach
    public void setUp() {
        registry = new CoreRegistry();
        history = new CommandHistory();
    }

    @Test
    public void testAddObjectCommand() {
        FmcObject circle = new FmcObject(FmcType.KREIS, 100, 150);
        AddObjectCommand cmd = new AddObjectCommand(registry, circle);

        assertFalse(registry.getObjects().containsKey(circle.getId()));

        history.executeCommand(cmd);
        assertTrue(registry.getObjects().containsKey(circle.getId()));
        assertEquals(circle, registry.getObjects().get(circle.getId()));

        assertTrue(history.canUndo());
        history.undo();
        assertFalse(registry.getObjects().containsKey(circle.getId()));

        assertTrue(history.canRedo());
        history.redo();
        assertTrue(registry.getObjects().containsKey(circle.getId()));
    }

    @Test
    public void testRemoveObjectCommand() {
        FmcObject circle = new FmcObject(FmcType.KREIS, 100, 150);
        registry.addObject(circle);

        RemoveObjectCommand cmd = new RemoveObjectCommand(registry, circle.getId());
        history.executeCommand(cmd);

        assertFalse(registry.getObjects().containsKey(circle.getId()));

        history.undo();
        assertTrue(registry.getObjects().containsKey(circle.getId()));
    }

    @Test
    public void testMoveObjectCommand() {
        FmcObject circle = new FmcObject(FmcType.KREIS, 100, 150);
        registry.addObject(circle);

        MoveObjectCommand cmd = new MoveObjectCommand(registry, circle.getId(), 100, 150, 200, 250);
        history.executeCommand(cmd);

        assertEquals(200, circle.getX());
        assertEquals(250, circle.getY());

        history.undo();
        assertEquals(100, circle.getX());
        assertEquals(150, circle.getY());

        history.redo();
        assertEquals(200, circle.getX());
        assertEquals(250, circle.getY());
    }

    @Test
    public void testConnectObjectsCommandAndBipartiteRule() {
        FmcObject circle = new FmcObject(FmcType.KREIS, 100, 150);
        FmcObject rect = new FmcObject(FmcType.QUADRAT, 300, 150);
        FmcObject circle2 = new FmcObject(FmcType.KREIS, 400, 150);

        registry.addObject(circle);
        registry.addObject(rect);
        registry.addObject(circle2);

        // 1. Gültige Verbindung: Kreis -> Quadrat
        Connection conn1 = new Connection(circle.getId(), 0, 0, rect.getId(), 0, 0);
        ConnectObjectsCommand cmd1 = new ConnectObjectsCommand(registry, conn1);
        history.executeCommand(cmd1);

        assertTrue(registry.getConnections().containsKey(conn1.getId()));

        // 2. Ungültige Verbindung: Kreis -> Kreis (Bipartit-Regel verletzt)
        Connection conn2 = new Connection(circle.getId(), 0, 0, circle2.getId(), 0, 0);
        ConnectObjectsCommand cmd2 = new ConnectObjectsCommand(registry, conn2);

        assertThrows(IllegalArgumentException.class, () -> {
            history.executeCommand(cmd2);
        });

        // 3. Undo der gültigen Verbindung
        history.undo();
        assertFalse(registry.getConnections().containsKey(conn1.getId()));

        // 4. Redo der gültigen Verbindung
        history.redo();
        assertTrue(registry.getConnections().containsKey(conn1.getId()));
    }
}
