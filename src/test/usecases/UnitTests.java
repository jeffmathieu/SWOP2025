package usecases;

import com.tablr.controller.TableController;
import com.tablr.model.Table;
import com.tablr.model.StringColumn;
import com.tablr.model.Column;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Tablr use cases, covering both valid and invalid scenarios.
 * Each test includes step-by-step comments for clarity and traceability.
 */
public class UnitTests {

    @Test // 4.1 Create Table – valid
    void testCreateTable() {
        // Step 1: Create controller
        TableController controller = new TableController();

        // Step 2: Simulate double-click to create a table
        controller.createTable();

        // Step 3: Verify table added with expected name
        List<Table> tables = controller.getTables();
        assertEquals(1, tables.size());
        assertEquals("Table1", tables.getFirst().getName());
    }

    @Test // 4.2 Edit Table Name – valid
    void testEditTableNameValid() {
        // Step 1: Create table
        TableController controller = new TableController();
        controller.createTable();

        // Step 2: Rename table
        controller.renameTable(1, "NewName");

        // Step 3: Verify
        assertEquals("NewName", controller.getTable(1).getName());
    }

    @Test // 4.2 Edit Table Name – empty name (invalid)
    void testEditTableNameEmptyFails() {
        // Step 1: Create table
        TableController controller = new TableController();
        controller.createTable();

        // Step 2: Attempt to rename to an empty name
        assertThrows(IllegalArgumentException.class, () ->
                controller.renameTable(1, ""));
    }

    @Test // 4.2 Edit Table Name – duplicate name (invalid)
    void testEditTableNameDuplicateFails() {
        // Step 1: Create two tables
        TableController controller = new TableController();
        controller.createTable(); // Table1
        controller.createTable(); // Table2

        // Step 2: Attempt to rename Table2 to "Table1"
        assertThrows(IllegalArgumentException.class, () ->
                controller.renameTable(2, "Table1"));
    }

    @Test // 4.3 Delete Table – valid
    void testDeleteTable() {
        // Step 1: Create table
        TableController controller = new TableController();
        controller.createTable();

        // Step 2: Delete it
        controller.deleteTable(1);

        // Step 3: Verify it's gone
        assertTrue(controller.getTables().isEmpty());
    }

    @Test // 4.3 Delete Table – invalid ID
    void testDeleteNonexistentTableFails() {
        // Step 1: Create controller
        TableController controller = new TableController();

        // Step 2: Attempt to delete a non-existent table
        assertThrows(IllegalArgumentException.class, () ->
                controller.deleteTable(99));
    }

    @Test // 4.4 Open Table – valid
    void testOpenTableReturnsCorrectTable() {
        // Step 1: Create table
        TableController controller = new TableController();
        controller.createTable();

        // Step 2: Retrieve it
        Table t = controller.getTable(1);

        // Step 3: Verify contents
        assertEquals("Table1", t.getName());
    }

    @Test // 4.4 Open Table – invalid ID
    void testOpenNonexistentTableFails() {
        // Step 1: Setup controller
        TableController controller = new TableController();

        // Step 2: Try accessing non-existing table
        assertThrows(IllegalArgumentException.class, () -> controller.getTable(100));
    }

    @Test // 4.5 Add Column – valid
    void testAddColumn() {
        // Step 1: Setup
        TableController controller = new TableController();
        controller.createTable();

        // Step 2: Add column
        controller.addColumnToTable(1);

        // Step 3: Validate
        Table t = controller.getTable(1);
        assertEquals(1, t.getColumns().size());
        assertEquals("Column1", t.getColumns().getFirst().getName());
    }

    @Test // 4.6 Edit Column Characteristic – rename valid
    void testEditColumnNameValid() {
        // Step 1: Setup
        TableController controller = new TableController();
        controller.createTable();
        controller.addColumnToTable(1);

        // Step 2: Rename
        Column col = controller.getTable(1).getColumn(1);
        col.setName("Renamed");

        // Step 3: Verify
        assertEquals("Renamed", col.getName());
    }

    @Test // 4.6 Edit Column Characteristic – empty name (invalid)
    void testEditColumnNameEmptyFails() {
        // Step 1: Setup
        TableController controller = new TableController();
        controller.createTable();
        controller.addColumnToTable(1);

        // Step 2: Set empty name
        Column col = controller.getTable(1).getColumn(1);
        assertThrows(IllegalArgumentException.class, () -> col.setName(""));
    }

    @Test // 4.7 Delete Column – valid
    void testDeleteColumn() {
        // Step 1: Setup
        TableController controller = new TableController();
        controller.createTable();
        controller.addColumnToTable(1);

        // Step 2: Delete column
        controller.getTable(1).removeColumn(1);

        // Step 3: Confirm deletion
        assertTrue(controller.getTable(1).getColumns().isEmpty());
    }

    @Test // 4.7 Delete Column – invalid column ID
    void testDeleteInvalidColumnFails() {
        // Step 1: Setup
        TableController controller = new TableController();
        controller.createTable();

        // Step 2: Attempt to remove non-existing column
        assertDoesNotThrow(() -> controller.getTable(1).removeColumn(123));
    }

    @Test // 4.8 Add Row – valid
    void testAddRow() {
        // Step 1: Setup
        TableController controller = new TableController();
        controller.createTable();
        controller.addColumnToTable(1);

        // Step 2: Add row
        controller.addRowToTable(1);

        // Step 3: Check row count
        Table t = controller.getTable(1);
        assertEquals(1, t.getRowCount());
    }

    @Test // 4.9 Edit Row Value – valid
    void testEditRowValueValid() {
        // Step 1: Setup
        TableController controller = new TableController();
        controller.createTable();
        controller.addColumnToTable(1);
        controller.addRowToTable(1);

        // Step 2: Set value
        StringColumn col = (StringColumn) controller.getTable(1).getColumn(1);
        col.setValue(0, "Hello");

        // Step 3: Verify
        String val = (String) controller.getTable(1).getColumn(1).getRowValue(0);
        assertEquals("Hello", val);
    }

    @Test // 4.9 Edit Row Value – invalid index
    void testEditRowValueInvalidIndexFails() {
        // Step 1: Setup
        TableController controller = new TableController();
        controller.createTable();
        controller.addColumnToTable(1);
        controller.addRowToTable(1);

        // Step 2: Attempt to write to invalid row index
        StringColumn col = (StringColumn) controller.getTable(1).getColumn(1);
        assertThrows(IndexOutOfBoundsException.class, () -> col.setValue(5, "Hello"));
    }

    @Test // 4.10 Delete Row – valid
    void testDeleteRow() {
        // Step 1: Setup
        TableController controller = new TableController();
        controller.createTable();
        controller.addColumnToTable(1);
        controller.addRowToTable(1);

        // Step 2: Remove row
        controller.removeRowFromTable(1, 0);

        // Step 3: Confirm it's gone
        assertEquals(0, controller.getTable(1).getRowCount());
    }

    @Test // 4.10 Delete Row – invalid row index
    void testDeleteInvalidRowFails() {
        // Step 1: Setup
        TableController controller = new TableController();
        controller.createTable();
        controller.addColumnToTable(1);

        // Step 2: Attempt to delete row that doesn't exist
        assertThrows(IndexOutOfBoundsException.class, () ->
                controller.removeRowFromTable(1, 0));
    }
}
