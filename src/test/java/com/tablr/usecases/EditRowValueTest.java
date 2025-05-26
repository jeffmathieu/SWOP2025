package usecases;

import com.tablr.controller.TableController;
import com.tablr.model.StringColumn;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EditRowValueTest {

    @Test // Use Case 4.9 – Step 1: Edit row with valid value
    void testEditRowValueValid() {
        TableController controller = new TableController();

        // Step 1: Setup table with column and row
        controller.createTable();
        controller.addColumnToTable(1);
        controller.addRowToTable(1);

        // Step 2: Edit row value
        StringColumn col = (StringColumn) controller.getTable(1).getColumn(1);
        col.setValue(0, "Hello");

        // Step 3: Validate
        assertEquals("Hello", col.getRowValue(0));
    }

    @Test // Use Case 4.9 – Step 2: Set null on column that disallows blank
    void testEditRowValueWithNullFailsIfDisallowed() {
        TableController controller = new TableController();

        // Step 1: Setup
        controller.createTable();
        controller.addColumnToTable(1);
        controller.addRowToTable(1);

        // Step 2: Set allowsBlank = false
        StringColumn col = (StringColumn) controller.getTable(1).getColumn(1);
        col.changeDefaultValue("hello");
        col.setValue(0, "Hello");
        col.setAllowsBlank(false);

        // Step 3: Try setting null
        assertThrows(IllegalArgumentException.class, () -> col.setValue(0, null));
    }

    @Test // Use Case 4.9 – Step 3: Set value on invalid row index
    void testEditRowInvalidIndexFails() {
        TableController controller = new TableController();

        // Step 1: Setup with no rows
        controller.createTable();
        controller.addColumnToTable(1);

        // Step 2: Try setting value at row index 5
        StringColumn col = (StringColumn) controller.getTable(1).getColumn(1);
        assertThrows(IndexOutOfBoundsException.class, () -> col.setValue(5, "oops"));
    }
}
