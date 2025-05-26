package com.tablr.controller;

import com.tablr.model.ColumnType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AppControllerTest {
    private AppController app;

    @BeforeEach
    void setUp() {
        app = new AppController();
        int tableId = app.getTableIds().getFirst();
        app.addColumnToTable(tableId);
        app.addRowToTable(tableId);
    }

    @Test
    void testGetTableIdsAndTables() {
        assertFalse(app.getTableIds().isEmpty());
        assertEquals(app.getTableIds().size(), app.getTables().size());
    }

    @Test
    void testCreateAndDeleteTable() {
        int before = app.getTables().size();
        app.createTable();
        assertEquals(before + 1, app.getTables().size());

        int lastId = app.getTableIds().getLast();
        app.deleteTable(lastId);
        assertEquals(before, app.getTables().size());
    }

    @Test
    void testRenameTable() {
        int id = app.getTableIds().getFirst();
        app.renameTable(id, "Renamed");
        assertEquals("Renamed", app.getTable(id).getName());
    }

    @Test
    void testTableValidation() {
        assertTrue(app.tableExists("Table1"));
        assertTrue(app.isValidTableName("UniqueTableName"));
        assertFalse(app.isValidTableName("Table1"));
    }

    @Test
    void testColumnOperations() {
        int tableId = app.getTableIds().getFirst();
        app.addColumnToTable(tableId);
        int colId = app.getColumnIdAt(tableId, 0);
        app.renameColumn(tableId, colId, "MyCol");

        assertTrue(app.isValidColumnName(tableId, "AnotherCol"));
        assertFalse(app.isValidColumnName(tableId, "MyCol"));

        app.deleteColumn(tableId, colId);
    }

    @Test
    void testRowOperations() {
        int tableId = app.getTableIds().getFirst();
        int colId = app.getColumnIdAt(tableId, 0);

        app.setRowValue(tableId, colId, 0, "Hello");
        assertTrue(app.isValidColumnValue(tableId, colId, "World"));

        int before = app.getTable(tableId).getRowCount();
        app.removeRowFromTable(tableId, 0);
        assertEquals(before - 1, app.getTable(tableId).getRowCount());
    }

    @Test
    void testColumnTypeConversion() {
        int tableId = app.getTableIds().getFirst();
        int colId = app.getColumnIdAt(tableId, 0);
        assertTrue(app.isValidColumnTypeConversion(tableId, colId, ColumnType.EMAIL));
        app.cycleColumnType(tableId, colId, ColumnType.EMAIL);
    }

    @Test
    void testUIEventDelegation() {
        app.onClick(10, 10);
        app.onDoubleClick(10, 10);
        app.onControlEnter();
        app.onEnter();
        app.onEscape();
        app.onCharacter('a');
        app.onBackspace();
        app.onDelete();
        app.onMousePressed(10, 10);
        app.onMouseDragged(15, 15);
        app.onMouseReleased();
        app.onCtrlT();
        app.onCtrlF();
        app.onControlN();
        app.onControlD();
        app.onPageUp();
        app.onPageDown();
    }

    @Test
    void testPaintWindows() {
        // Should not throw
        app.paintWindows(new java.awt.image.BufferedImage(10, 10, java.awt.image.BufferedImage.TYPE_INT_ARGB).getGraphics());
    }

    @Test
    void testToggleDefaultValue() {
        app.cycleColumnType(1, 1, ColumnType.EMAIL);
        app.cycleColumnType(1, 1, ColumnType.BOOLEAN);

        app.toggleDefaultValue(1,1);
    }
}
