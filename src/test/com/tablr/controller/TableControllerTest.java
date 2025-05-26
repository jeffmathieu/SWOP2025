package com.tablr.controller;

import com.tablr.model.Column;
import com.tablr.model.ColumnType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TableControllerTest {
    private TableController controller;

    @BeforeEach
    void setUp() {
        controller = new TableController();
        controller.createTable(); // Table1, ID=1
    }

    @Test
    void testCreateAndHasTable() {
        assertTrue(controller.hasTable(1));
        assertTrue(controller.hasTable("Table1"));
        assertFalse(controller.hasTable("NonExistent"));
    }

    @Test
    void testAddAndRemoveTable() {
        int sizeBefore = controller.getTables().size();
        controller.createTable();
        assertEquals(sizeBefore + 1, controller.getTables().size());

        int idToRemove = controller.getTables().get(1).getId();
        controller.deleteTable(idToRemove);
        assertEquals(sizeBefore, controller.getTables().size());
    }

    @Test
    void testDeleteTableFails() {
        assertThrows(IllegalArgumentException.class, () -> controller.deleteTable(999));
    }

    @Test
    void testRenameTable() {
        int id = controller.getTables().getFirst().getId();
        controller.renameTable(id, "RenamedTable");
        assertEquals("RenamedTable", controller.getTable(id).getName());
    }

    @Test
    void testRenameTableFails() {
        int id = controller.getTables().getFirst().getId();
        assertThrows(IllegalArgumentException.class, () -> controller.renameTable(id, ""));
        assertThrows(IllegalArgumentException.class, () -> controller.renameTable(id, "Table1"));
    }

    @Test
    void testColumnOperations() {
        int id = controller.getTables().getFirst().getId();
        controller.addColumnToTable(id);
        Column<?> col = controller.getTable(id).getColumns().getFirst();

        assertTrue(controller.isValidColumnName(id, "ColX"));
        assertFalse(controller.isValidColumnName(id, col.getName()));

        controller.renameColumn(id, col.getId(), "NewCol");
        assertEquals("NewCol", controller.getTable(id).getColumn(col.getId()).getName());

        controller.deleteColumn(id, col.getId());
        assertEquals(0, controller.getTable(id).getColumns().size());
    }

    @Test
    void testInvalidRenameColumn() {
        int id = controller.getTables().getFirst().getId();
        controller.addColumnToTable(id);
        Column<?> col = controller.getTable(id).getColumns().getFirst();
        assertThrows(IllegalArgumentException.class, () -> controller.renameColumn(id, col.getId(), ""));
    }

    @Test
    void testDefaultValueChange() {
        int id = controller.getTables().getFirst().getId();
        controller.addColumnToTable(id);
        Column<?> col = controller.getTable(id).getColumns().getFirst();
        controller.changeDefaultValue(id, col.getId(), "123");
        assertEquals("123", col.getDefaultValue());
    }

    @Test
    void testToggleBlankAndDefaultValue() {
        int id = controller.getTables().getFirst().getId();
        controller.addColumnToTable(id);
        Column<?> col = controller.getTable(id).getColumns().getFirst();
        assertTrue(controller.tryToggleAllowsBlank(id, col.getId(), true));
    }

    @Test
    void testRowOperations() {
        int id = controller.getTables().getFirst().getId();
        controller.addColumnToTable(id);
        controller.addRowToTable(id);
        controller.addRowToTable(id);
        assertEquals(2, controller.getTable(id).getRowCount());

        controller.removeRowFromTable(id, 0);
        assertEquals(1, controller.getTable(id).getRowCount());
    }

    @Test
    void testInvalidRowRemove() {
        int id = controller.getTables().getFirst().getId();
        assertThrows(IndexOutOfBoundsException.class, () -> controller.removeRowFromTable(id, 0));
    }

    @Test
    void testSetValueAndValidation() {
        int id = controller.getTables().getFirst().getId();
        controller.addColumnToTable(id);
        controller.addRowToTable(id);
        Column<?> col = controller.getTable(id).getColumns().getFirst();
        controller.setRowValue(id, col.getId(), 0, "Hello");

        assertTrue(controller.isValidColumnValue(id, col.getId(), "Hello"));
    }

    @Test
    void testTypeConversionValidationAndCycle() {
        int id = controller.getTables().getFirst().getId();
        controller.addColumnToTable(id);
        Column<?> col = controller.getTable(id).getColumns().getFirst();

        assertTrue(controller.isValidTypeConversion(id, col.getId(), ColumnType.EMAIL));
        controller.cycleColumnType(id, col.getId(), ColumnType.EMAIL);
        assertEquals(ColumnType.EMAIL, controller.getTable(id).getColumns().getFirst().getColumnType());
    }

    @Test
    void testGetColumnIdAt() {
        int id = controller.getTables().getFirst().getId();
        controller.addColumnToTable(id);
        int cid = controller.getColumnIdAt(id, 0);
        assertEquals(controller.getTable(id).getColumns().getFirst().getId(), cid);
    }

    @Test
    void testInvalidTableAccess() {
        assertThrows(IllegalArgumentException.class, () -> controller.getTable(999));
    }
}
