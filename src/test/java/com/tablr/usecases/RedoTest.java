package usecases;


import com.tablr.controller.AppController;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RedoTest {

    @Test // Use Case 4.12 – Step 1: Create a table, undo, then redo
    void testRedoCreateTable() {
        AppController app = new AppController();
        app.initializeTestApp();

        app.createTable();
        app.undo();

        assertFalse(app.tableExists("Table6"));

        app.redo();
        assertTrue(app.tableExists("Table6"));
    }

    @Test // Use Case 4.12 – Step 2: Rename a table, undo, redo
    void testRedoRenameTable() {
        AppController app = new AppController();
        app.initializeTestApp();

        int tableId = app.getTableIds().getFirst();
        String original = app.getTable(tableId).getName();

        app.renameTable(tableId, "Updated");
        app.undo();
        assertEquals(original, app.getTable(tableId).getName());

        app.redo();
        assertEquals("Updated", app.getTable(tableId).getName());
    }
}
