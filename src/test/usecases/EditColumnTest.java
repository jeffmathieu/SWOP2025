package usecases;

import com.tablr.controller.TableController;
import com.tablr.model.Column;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EditColumnTest {

    @Test // Use Case 4.6 – Step 1: Rename column with valid name
    void testEditColumnNameValid() {
        TableController controller = new TableController();
        controller.createTable();
        controller.addColumnToTable(1);

        // Step: Rename column
        Column<?> col = controller.getTable(1).getColumn(1);
        col.setName("Renamed");

        // Step: Check name changed
        assertEquals("Renamed", col.getName());
    }

    @Test // Use Case 4.6 – Step 2: Rename column to empty name
    void testEditColumnNameEmptyFails() {
        TableController controller = new TableController();
        controller.createTable();
        controller.addColumnToTable(1);

        Column<?> col = controller.getTable(1).getColumn(1);

        // Step: Try renaming to empty name
        assertThrows(IllegalArgumentException.class, () -> col.setName(""));
    }
}
