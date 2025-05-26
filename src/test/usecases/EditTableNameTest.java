package usecases;

import com.tablr.controller.TableController;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EditTableNameTest {

    @Test // Use Case 4.2 – Step 1: Rename table with valid name
    void testEditTableNameValid() {
        TableController controller = new TableController();

        // Step 1: Create a table
        controller.createTable();

        // Step 2: Rename the table
        controller.renameTable(1, "NewName");

        // Step 3: Verify new name
        assertEquals("NewName", controller.getTable(1).getName());
    }

    @Test // Use Case 4.2 – Step 2: Rename table with empty name (invalid)
    void testEditTableNameEmptyFails() {
        TableController controller = new TableController();

        // Step 1: Create a table
        controller.createTable();

        // Step 2: Try renaming to an empty string
        assertThrows(IllegalArgumentException.class, () -> controller.renameTable(1, ""));
    }

    @Test // Use Case 4.2 – Step 3: Rename table to duplicate name (invalid)
    void testEditTableNameDuplicateFails() {
        TableController controller = new TableController();

        // Step 1: Create two tables
        controller.createTable(); // Table1
        controller.createTable(); // Table2

        // Step 2: Try renaming second to the name of first
        assertThrows(IllegalArgumentException.class, () -> controller.renameTable(2, "Table1"));
    }
}
