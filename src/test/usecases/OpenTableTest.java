package usecases;

import com.tablr.controller.TableController;
import com.tablr.model.Table;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OpenTableTest {

    @Test // Use Case 4.4 – Step 1: Open an existing table
    void testOpenTableReturnsCorrectTable() {
        TableController controller = new TableController();

        // Step 1: Create a table
        controller.createTable();

        // Step 2: Get table by ID
        Table t = controller.getTable(1);

        // Step 3: Verify name matches
        assertEquals("Table1", t.getName());
    }

    @Test // Use Case 4.4 – Step 2: Try opening a non-existent table
    void testOpenNonexistentTableFails() {
        TableController controller = new TableController();

        // Step: Attempt to open table that doesn't exist
        assertThrows(IllegalArgumentException.class, () -> controller.getTable(100));
    }
}
