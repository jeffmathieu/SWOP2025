package usecases;

import com.tablr.controller.AppController;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UndoTest {


    @Test // Use Case 4.11 – Step 1: Create a table and undo it
    void testUndoCreateTable() {
        AppController app = new AppController();
        app.initializeTestApp();

        // Step 1: Create a new table
        app.createTable();
        int newId = app.getTableIds().getLast();
        assertTrue(app.tableExists("Table6")); // Assuming 5 preloaded tables

        // Step 2: Undo the creation
        app.undo();

        // Step 3: The table should be gone
        assertFalse(app.tableExists("Table6"));
    }

    @Test // Use Case 4.11 – Step 2: Rename a table and undo
    void testUndoRenameTable() {
        AppController app = new AppController();
        app.initializeTestApp();

        int tableId = app.getTableIds().getFirst();
        String originalName = app.getTable(tableId).getName();

        // Step 1: Rename the table
        app.renameTable(tableId, "NewName");
        assertEquals("NewName", app.getTable(tableId).getName());

        // Step 2: Undo
        app.undo();

        // Step 3: Name should be back to original
        assertEquals(originalName, app.getTable(tableId).getName());
    }
}
