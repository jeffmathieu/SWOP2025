package usecases;

import com.tablr.controller.TableController;
import com.tablr.model.Table;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CreateTableTest {

    @Test // Use Case 4.1 – Create Table
    void testCreateTable() {
        // Step 1: Set up controller
        TableController controller = new TableController();

        // Step 2: Create a new table
        controller.createTable();

        // Step 3: Verify the new table is added
        List<Table> tables = controller.getTables();
        assertEquals(1, tables.size());

        // Step 4: Check the name and properties
        assertEquals("Table1", tables.getFirst().getName());
    }
}
