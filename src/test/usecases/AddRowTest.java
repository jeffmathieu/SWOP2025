package usecases;

import com.tablr.controller.TableController;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AddRowTest {

    @Test // Use Case 4.8 – Step 1: Add row to table with column
    void testAddRow() {
        TableController controller = new TableController();

        // Step 1: Create table and column
        controller.createTable();
        controller.addColumnToTable(1);

        // Step 2: Add row
        controller.addRowToTable(1);

        // Step 3: Verify row count
        assertEquals(1, controller.getTable(1).getRowCount());
    }

    @Test // Use Case 4.8 – Step 2: Add row to table without columns
    void testAddRowToTableWithNoColumns() {
        TableController controller = new TableController();

        // Step 1: Create table
        controller.createTable();

        // Step 2: Add row (should be ignored silently)
        controller.addRowToTable(1);

        // Step 3: Confirm no rows (no columns to store values)
        assertEquals(0, controller.getTable(1).getRowCount());
    }

    @Test // Use Case 4.8 – Step 3: Add row to non-existent table
    void testAddRowToNonexistentTableFails() {
        TableController controller = new TableController();

        // Step: Attempt to add row to unknown table
        assertThrows(IllegalArgumentException.class, () -> controller.addRowToTable(42));
    }
}
