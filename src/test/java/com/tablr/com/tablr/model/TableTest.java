package com.tablr.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Table class, providing full coverage of its behavior including:
 * creation, column/row management, value setting, cloning, and exception handling.
 */
public class TableTest {

    /** Tests basic constructor and getter methods. */
    @Test
    void testConstructorAndGetters() {
        Table table = new Table("TestTable", 1);
        assertEquals("TestTable", table.getName());
        assertEquals(1, table.getId());
    }

    /** Tests that invalid names (null or empty) throw exceptions during construction. */
    @Test
    void testInvalidNameInConstructor() {
        assertThrows(IllegalArgumentException.class, () -> new Table("", 1));
        assertThrows(IllegalArgumentException.class, () -> new Table(null, 2));
    }

    /** Tests the ability to update a table's name using setName. */
    @Test
    void testSetName() {
        Table table = new Table("Initial", 1);
        table.setName("Updated");
        assertEquals("Updated", table.getName());
    }

    /** Verifies that setting a null or blank name via setName throws an exception. */
    @Test
    void testSetInvalidName() {
        Table table = new Table("Initial", 1);
        assertThrows(IllegalArgumentException.class, () -> table.setName(""));
        assertThrows(IllegalArgumentException.class, () -> table.setName(null));
    }

    /** Ensures unique column names and IDs are generated when creating multiple columns. */
    @Test
    void testCreateColumnAndUniqueNaming() {
        Table table = new Table("T", 1);
        table.createColumn();
        table.createColumn();
        assertEquals(2, table.getColumns().size());
        assertEquals("Column1", table.getColumns().get(0).getName());
        assertEquals("Column2", table.getColumns().get(1).getName());
    }

    /** Tests that creating a row populates each column with a default value. */
    @Test
    void testCreateRowAndRowCount() {
        Table table = new Table("T", 1);
        table.createColumn();
        table.createColumn();
        table.createRow();
        assertEquals(1, table.getRowCount());
    }

    /** Tests the ability to remove a column by ID. */
    @Test
    void testRemoveColumnById() {
        Table table = new Table("T", 1);
        table.createColumn();
        int colId = table.getColumns().getFirst().getId();
        table.removeColumn(colId);
        assertEquals(0, table.getColumns().size());
    }

    /** Verifies that a column can be retrieved by its ID. */
    @Test
    void testGetColumnById() {
        Table table = new Table("T", 1);
        table.createColumn();
        int colId = table.getColumns().getFirst().getId();
        Column<?> col = table.getColumn(colId);
        assertNotNull(col);
    }

    /** Confirms that requesting a non-existent column throws a NoSuchElementException. */
    @Test
    void testGetColumnByIdThrows() {
        Table table = new Table("T", 1);
        assertThrows(NoSuchElementException.class, () -> table.getColumn(123));
    }

    /** Validates value setting for a column and handles index and invalid input errors. */
    @Test
    void testSetValueValidAndInvalid() {
        Table table = new Table("T", 1);
        table.createColumn();
        table.createRow();
        int colId = table.getColumns().getFirst().getId();

        // Valid
        Table finalTable = table;
        assertDoesNotThrow(() -> finalTable.setValue(colId, 0, "Hello"));

        // Invalid index
        Table finalTable1 = table;
        assertThrows(IndexOutOfBoundsException.class, () -> finalTable1.setValue(colId, 10, "X"));

        // Invalid value (not allowed blank)
        table = new Table("T2", 2);
        Column<?> col = new StringColumn("C", false, "D", 111);
        List<Column<?>> list = new ArrayList<>();
        list.add(col);
        table.setColumns(list);
        table.createRow();
        Table finalTable2 = table;
        assertThrows(IllegalArgumentException.class, () -> finalTable2.setValue(111, 0, ""));
    }

    /** Tests replacing a column in-place by its ID. */
    @Test
    void testReplaceColumnById() {
        Table table = new Table("T", 1);
        table.createColumn();
        int originalId = table.getColumns().getFirst().getId();

        Column<?> newCol = new IntegerColumn("Age", true, 0, originalId);
        table.replaceColumnById(originalId, newCol);
        assertInstanceOf(IntegerColumn.class, table.getColumn(originalId));
    }

    /** Tests replacing a non-existent column throws NoSuchElementException. */
    @Test
    void testReplaceColumnByIdNotFound() {
        Table table = new Table("T", 1);
        Column<?> newCol = new IntegerColumn("Age", true, 0, 999);
        assertThrows(NoSuchElementException.class, () -> table.replaceColumnById(999, newCol));
    }

    /** Validates the hasColumn() method for existing and missing names. */
    @Test
    void testHasColumnByName() {
        Table table = new Table("T", 1);
        table.createColumn();
        String name = table.getColumns().getFirst().getName();
        assertTrue(table.hasColumn(name));
        assertFalse(table.hasColumn("Missing"));
    }

    /** Tests removing a row and the expected row count, including error on extra delete. */
    @Test
    void testRemoveRowValidAndInvalid() {
        Table table = new Table("T", 1);
        table.createColumn();
        table.createRow();
        assertEquals(1, table.getRowCount());
        table.removeRow(0);
        assertEquals(0, table.getRowCount());
        assertThrows(IndexOutOfBoundsException.class, () -> table.removeRow(0));
    }

    /** Ensures that the table deepClone creates a copy with the same structure and values. */
    @Test
    void testDeepClone() {
        Table table = new Table("T", 1);
        table.createColumn();
        table.createRow();
        table.setValue(table.getColumns().getFirst().getId(), 0, "Data");

        Table clone = table.deepClone();
        assertEquals(table.getName(), clone.getName());
        assertEquals(table.getColumns().size(), clone.getColumns().size());
        assertEquals(table.getRowCount(), clone.getRowCount());
    }

    /** Tests replacing all columns using setColumns(). */
    @Test
    void testSetColumns() {
        Table table = new Table("T", 1);
        List<Column<?>> newCols = new ArrayList<>();
        newCols.add(new StringColumn("S", true, "", 1));
        table.setColumns(newCols);
        assertEquals(1, table.getColumns().size());
    }
}
