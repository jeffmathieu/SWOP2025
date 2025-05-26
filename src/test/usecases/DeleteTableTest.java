package usecases;

import com.tablr.controller.TableController;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DeleteTableTest {

    @Test // Use Case 4.3 – Step 1: Delete valid table
    void testDeleteTable() {
        TableController controller = new TableController();

        // Step 1: Create a table
        controller.createTable();

        // Step 2: Delete it
        controller.deleteTable(1);

        // Step 3: Verify deletion
        assertTrue(controller.getTables().isEmpty());
    }

    @Test // Use Case 4.3 – Step 2: Try deleting non-existent table
    void testDeleteNonexistentTableFails() {
        TableController controller = new TableController();

        // Step: Attempt to delete non-existent table
        assertThrows(IllegalArgumentException.class, () -> controller.deleteTable(99));
    }
}
