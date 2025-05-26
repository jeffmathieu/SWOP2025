package usecases;

import com.tablr.controller.TableController;
import com.tablr.model.Table;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AddColumnTest {

    @Test // Use Case 4.5 – Step 1: Add a column to a table
    void testAddColumn() {
        TableController controller = new TableController();

        // Step 1: Create a table
        controller.createTable();

        // Step 2: Add a column
        controller.addColumnToTable(1);

        // Step 3: Check column count and name
        Table t = controller.getTable(1);
        assertEquals(1, t.getColumns().size());
        assertEquals("Column1", t.getColumns().getFirst().getName());
    }

    @Test // Use Case 4.5 – Step 2: Add multiple columns and check names
    void testAddMultipleColumnsCreatesUniqueNames() {
        TableController controller = new TableController();

        // Step 1: Create a table
        controller.createTable();

        // Step 2: Add multiple columns
        controller.addColumnToTable(1);
        controller.addColumnToTable(1);

        // Step 3: Check names
        assertEquals("Column1", controller.getTable(1).getColumns().get(0).getName());
        assertEquals("Column2", controller.getTable(1).getColumns().get(1).getName());
    }

    @Test // Use Case 4.5 – Step 3: Add column to non-existent table
    void testAddColumnToNonexistentTableFails() {
        TableController controller = new TableController();

        // Step: Attempt to add to non-existent table
        assertThrows(IllegalArgumentException.class, () -> controller.addColumnToTable(999));
    }
}
