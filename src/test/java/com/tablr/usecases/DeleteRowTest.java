package usecases;

import com.tablr.controller.TableController;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DeleteRowTest {

    @Test // Use Case 4.10 – Step 1: Delete existing row
    void testDeleteRow() {
        TableController controller = new TableController();

        // Step 1: Setup with row
        controller.createTable();
        controller.addColumnToTable(1);
        controller.addRowToTable(1);

        // Step 2: Delete row
        controller.removeRowFromTable(1, 0);

        // Step 3: Validate
        assertEquals(0, controller.getTable(1).getRowCount());
    }

    @Test // Use Case 4.10 – Step 2: Delete row with invalid index
    void testDeleteRowInvalidIndexFails() {
        TableController controller = new TableController();

        // Step 1: Setup
        controller.createTable();
        controller.addColumnToTable(1);
        controller.addRowToTable(1);

        // Step 2: Try deleting invalid row index
        assertThrows(IndexOutOfBoundsException.class, () -> controller.removeRowFromTable(1, 10));
    }

    @Test // Use Case 4.10 – Step 3: Delete row from non-existent table
    void testDeleteRowFromNonexistentTableFails() {
        TableController controller = new TableController();

        // Step: Try removing from unknown table
        assertThrows(IllegalArgumentException.class, () -> controller.removeRowFromTable(42, 0));
    }
}
