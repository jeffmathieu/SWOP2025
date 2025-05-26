package com.tablr.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for core model classes in Tablr.
 * Covers Table, Column, and specific column types (String, Integer, Email, Boolean).
 */
public class ColumnUnitTests {

    @Test
    void testTableCreateAndAddColumn() {
        Table table = new Table("Test", 1);
        table.createColumn();
        assertEquals(1, table.getColumns().size());
    }

    @Test
    void testTableInvalidName() {
        assertThrows(IllegalArgumentException.class, () -> new Table("", 1));
        assertThrows(IllegalArgumentException.class, () -> new Table(null, 1));
    }

    @Test
    void testTableAddRow() {
        Table table = new Table("Test", 1);
        table.createColumn();
        table.createRow();
        assertEquals(1, table.getRowCount());
    }

    @Test
    void testTableRemoveInvalidRow() {
        Table table = new Table("Test", 1);
        assertThrows(IndexOutOfBoundsException.class, () -> table.removeRow(0));
    }

    @Test
    void testTableSetValueAndGetValue() {
        Table table = new Table("Test", 1);
        table.createColumn();
        table.createRow();
        Column col = table.getColumns().getFirst();
        table.setValue(col.getId(), 0, "NewValue");
        assertEquals("NewValue", col.getRowValue(0));
    }

    @Test
    void testStringColumnSetValueValidation() {
        StringColumn col = new StringColumn("Name", false, "Default", 1);
        col.addDefaultValue();
        assertDoesNotThrow(() -> col.setValue(0, "Alice"));
        assertEquals("Alice", col.getRowValue(0));
        assertThrows(IllegalArgumentException.class, () -> col.setValue(0, ""));
        assertThrows(IllegalArgumentException.class, () -> col.setValue(0, null));
    }

    @Test
    void testEmailColumnSetValueValidation() {
        EmailColumn col = new EmailColumn("Email", false, "a@b.com", 1);
        col.addDefaultValue();
        assertDoesNotThrow(() -> col.setValue(0, "x@y.com"));
        assertThrows(IllegalArgumentException.class, () -> col.setValue(0, "invalid.com"));
        assertThrows(IllegalArgumentException.class, () -> col.setValue(0, null));
        assertThrows(IllegalArgumentException.class, () -> col.setValue(0, ""));
    }

    @Test
    void testIntegerColumnSetValueValidation() {
        IntegerColumn col = new IntegerColumn("Age", true, 30, 1);
        col.addDefaultValue();
        assertDoesNotThrow(() -> col.setValue(0, 42));
        assertDoesNotThrow(() -> col.setValue(0, null));
        IntegerColumn col2 = new IntegerColumn("Age", false, 30, 2);
        col2.addDefaultValue();
        assertThrows(IllegalArgumentException.class, () -> col2.setValue(0, null));
    }

    @Test
    void testBooleanColumnCyclingWithBlanks() {
        BooleanColumn col = new BooleanColumn("Flag", true, null, 1);
        assertNull(col.getDefaultValue());
        //col.changeDefaultValue(null);
        Boolean next = col.toggleDefaultValue();
        col.changeDefaultValue(next);
        assertEquals(true, next);
        next = col.toggleDefaultValue();
        col.changeDefaultValue(next);
        assertEquals(false, next);
        next = col.toggleDefaultValue();
        col.changeDefaultValue(next);
        assertNull(col.getDefaultValue());
    }

    @Test
    void testBooleanColumnWithoutBlanks() {
        BooleanColumn col = new BooleanColumn("Flag", false, true, 1);
        Boolean next = col.toggleDefaultValue();
        col.changeDefaultValue(next);
        assertEquals(false, next);
        next = col.toggleDefaultValue();
        col.changeDefaultValue(next);
        assertEquals(true, next);
    }

    @Test
    void testColumnSetNameValidation() {
        StringColumn col = new StringColumn("ValidName", true, "", 1);
        assertThrows(IllegalArgumentException.class, () -> col.setName(" "));
        assertThrows(IllegalArgumentException.class, () -> new StringColumn(" ", true, "", 2));
        assertThrows(IllegalArgumentException.class, () -> new StringColumn(null, true, "", 3));
    }

    @Test
    void testColumnSetValueBlankDisallowed() {
        StringColumn col = new StringColumn("Name", false, "Default", 1);
        col.addDefaultValue();
        assertThrows(IllegalArgumentException.class, () -> col.setValue(0, ""));
        assertThrows(IllegalArgumentException.class, () -> col.setValue(0, null));
    }

    @Test
    void testColumnRemoveInvalidIndex() {
        StringColumn col = new StringColumn("Name", true, "Default", 1);
        assertThrows(IndexOutOfBoundsException.class, () -> col.removeValue(0));
    }

    @Test
    void testColumnInvalidGetRow() {
        StringColumn col = new StringColumn("Name", true, "Default", 1);
        assertThrows(IndexOutOfBoundsException.class, () -> col.getRowValue(0));
    }

    @Test
    void testColumnSize() {
        StringColumn col = new StringColumn("Name", true, "Default", 1);
        assertEquals(0, col.size());
        col.addDefaultValue();
        assertEquals(1, col.size());
    }
}
