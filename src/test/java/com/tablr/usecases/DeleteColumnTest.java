package usecases;

import com.tablr.controller.TableController;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DeleteColumnTest {

    @Test // Use Case 4.7 – Step 1: Delete an existing column
    void testDeleteColumn() {
        TableController controller = new TableController();

        // Step 1: Create table and add a column
        controller.createTable();
        controller.addColumnToTable(1);

        // Step 2: Delete the column
        controller.getTable(1).removeColumn(1);

        // Step 3: Verify column is gone
        assertTrue(controller.getTable(1).getColumns().isEmpty());
    }

    @Test // Use Case 4.7 – Step 2: Try deleting a non-existent column
    void testDeleteNonexistentColumnDoesNothing() {
        TableController controller = new TableController();

        // Step 1: Create table but no columns
        controller.createTable();

        // Step 2: Try deleting a column that doesn't exist
        controller.getTable(1).removeColumn(42);

        // Step 3: Verify nothing crashed or was deleted
        assertTrue(controller.getTable(1).getColumns().isEmpty());
    }
}
