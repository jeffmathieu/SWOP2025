package com.tablr.undoRedo;

import com.tablr.controller.AppController;
import com.tablr.model.ColumnType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UndoRedoTest {

    private AppController app;

    @BeforeEach
    public void setup() {
        app = new AppController();
        app.initializeTestApp();
    }

    @Test
    public void testCreateTableUndoRedo() {
        int initialCount = app.getTables().size();
        app.createTable();
        assertEquals(initialCount + 1, app.getTables().size());

        app.undo();
        assertEquals(initialCount, app.getTables().size());

        app.redo();
        assertEquals(initialCount + 1, app.getTables().size());
    }

    @Test
    public void testDeleteTableUndoRedo() {
        int initialCount = app.getTables().size()-1;
        int tableId = app.getTables().getFirst().getId();

        app.deleteTable(tableId);
        assertEquals(initialCount - 1, app.getTables().size());

        app.undo();
        assertEquals(initialCount, app.getTables().size());

        app.redo();
        assertEquals(initialCount - 1, app.getTables().size());
    }

    @Test
    public void testRenameTableUndoRedo() {
        int tableId = app.getTables().getFirst().getId();
        String originalName = app.getTable(tableId).getName();
        app.renameTable(tableId, "NewName");
        assertEquals("NewName", app.getTable(tableId).getName());

        app.undo();
        assertEquals(originalName, app.getTable(tableId).getName());

        app.redo();
        assertEquals("NewName", app.getTable(tableId).getName());
    }

    @Test
    public void testAddDeleteColumnUndoRedo() {
        int tableId = app.getTables().getFirst().getId();
        int initialCount = app.getTable(tableId).getColumnCount();

        app.addColumnToTable(tableId);
        assertEquals(initialCount + 1, app.getTable(tableId).getColumnCount());

        int columnId = app.getTable(tableId).getColumns().get(initialCount).getId();
        app.deleteColumn(tableId, columnId);
        assertEquals(initialCount, app.getTable(tableId).getColumnCount());

        app.undo();
        assertEquals(initialCount + 1, app.getTable(tableId).getColumnCount());

        app.undo();
        assertEquals(initialCount, app.getTable(tableId).getColumnCount());

        app.redo();
        assertEquals(initialCount + 1, app.getTable(tableId).getColumnCount());
    }

    @Test
    public void testRenameColumnUndoRedo() {
        int tableId = app.getTables().getFirst().getId();
        app.addColumnToTable(tableId);
        int columnId = app.getTable(tableId).getColumns().getFirst().getId();
        String oldName = app.getTable(tableId).getColumn(columnId).getName();

        app.renameColumn(tableId, columnId, "Renamed");
        assertEquals("Renamed", app.getTable(tableId).getColumn(columnId).getName());

        app.undo();
        assertEquals(oldName, app.getTable(tableId).getColumn(columnId).getName());

        app.redo();
        assertEquals("Renamed", app.getTable(tableId).getColumn(columnId).getName());
    }

    @Test
    public void testSetCellValueUndoRedo() {
        int tableId = app.getTables().getFirst().getId();
        app.addColumnToTable(tableId);
        app.addRowToTable(tableId);
        int columnId = app.getTable(tableId).getColumns().getFirst().getId();

        app.setRowValue(tableId, columnId, 0, "Hello");
        assertEquals("Hello", app.getTable(tableId).getColumn(columnId).getValue(0));

        app.undo();
        assertNull(app.getTable(tableId).getColumn(columnId).getValue(0));

        app.redo();
        assertEquals("Hello", app.getTable(tableId).getColumn(columnId).getValue(0));
    }

    @Test
    public void testAddDeleteRowUndoRedo() {
        int tableId = app.getTables().getFirst().getId();
        int initialRowCount = app.getTable(tableId).getRowCount();

        app.addColumnToTable(tableId);
        app.addRowToTable(tableId);
        assertEquals(initialRowCount + 1, app.getTable(tableId).getRowCount());

        app.removeRowFromTable(tableId, initialRowCount);
        assertEquals(initialRowCount, app.getTable(tableId).getRowCount());

        app.undo();
        assertEquals(initialRowCount + 1, app.getTable(tableId).getRowCount());

        app.undo();
        assertEquals(initialRowCount, app.getTable(tableId).getRowCount());

        app.redo();
        assertEquals(initialRowCount + 1, app.getTable(tableId).getRowCount());
    }

    @Test
    public void testChangeDefaultValueUndoRedo() {
        int tableId = app.getTables().getFirst().getId();
        app.addColumnToTable(tableId);
        int columnId = app.getTable(tableId).getColumns().getFirst().getId();

        app.tryChangeDefaultValue(tableId, columnId, "42");
        assertEquals("42", app.getTable(tableId).getColumn(columnId).getDefaultValue().toString());

        app.undo();
        assertNull(app.getTable(tableId).getColumn(columnId).getDefaultValue());

        app.redo();
        assertEquals("42", app.getTable(tableId).getColumn(columnId).getDefaultValue().toString());

        app.tryChangeDefaultValue(tableId, columnId, null);
        assertNull(app.getTable(tableId).getColumn(columnId).getDefaultValue());

        app.undo();
        assertEquals("42", app.getTable(tableId).getColumn(columnId).getDefaultValue().toString());

        app.redo();
        assertNull(app.getTable(tableId).getColumn(columnId).getDefaultValue());

        app.tryChangeDefaultValue(tableId, columnId, "hello");
        app.tryChangeDefaultValue(tableId, columnId, "");
        assertNull(app.getTable(tableId).getColumn(columnId).getDefaultValue());
    }

    @Test
    public void testRestOfIfStatementsChangeDefaultValueUndoRedo() {
        int tableId = app.getTables().getFirst().getId();
        app.addColumnToTable(tableId);
        int columnId = app.getTable(tableId).getColumns().getFirst().getId();

        app.tryChangeDefaultValue(tableId, columnId, "hello");

    }

    @Test
    public void testToggleAllowsBlankUndoRedo() {
        int tableId = app.getTables().getFirst().getId();
        app.addColumnToTable(tableId);
        int columnId = app.getTable(tableId).getColumns().getFirst().getId();

        app.tryChangeDefaultValue(tableId, columnId, "default");

        boolean original = app.getTable(tableId).getColumn(columnId).allowsBlank();
        boolean toggled = app.tryToggleAllowsBlank(tableId, columnId, !original);
        assertTrue(toggled);

        assertEquals(!original, app.getTable(tableId).getColumn(columnId).allowsBlank());

        app.undo();
        assertEquals(original, app.getTable(tableId).getColumn(columnId).allowsBlank());

        app.redo();
        assertEquals(!original, app.getTable(tableId).getColumn(columnId).allowsBlank());
    }

    @Test
    public void testChangeColumnTypeUndoRedo() {
        int tableId = app.getTables().getFirst().getId();
        app.addColumnToTable(tableId);
        int columnId = app.getTable(tableId).getColumns().getFirst().getId();
        app.addRowToTable(tableId);

        ColumnType originalType = app.getTable(tableId).getColumn(columnId).getColumnType();
        ColumnType newType = ColumnType.INTEGER;
        if (originalType == ColumnType.INTEGER) newType = ColumnType.STRING;

        app.cycleColumnType(tableId, columnId, newType);
        assertEquals(newType, app.getTable(tableId).getColumn(columnId).getColumnType());

        app.undo();
        assertEquals(originalType, app.getTable(tableId).getColumn(columnId).getColumnType());

        app.redo();
        assertEquals(newType, app.getTable(tableId).getColumn(columnId).getColumnType());
    }

    @Test
    public void extraTestChangeColumnTypeUndoRedo() {
        int tableId = app.getTables().getFirst().getId();
        app.addColumnToTable(tableId);
        app.addRowToTable(tableId);
        app.setRowValue(tableId, 1, 0, "hello@test.be");

        app.cycleColumnType(tableId, 1, ColumnType.EMAIL);
        assertEquals(ColumnType.EMAIL, app.getTable(tableId).getColumn(tableId).getColumnType());

        app.undo();
        assertEquals(ColumnType.STRING, app.getTable(tableId).getColumn(tableId).getColumnType());

        app.redo();
        assertEquals(ColumnType.EMAIL, app.getTable(tableId).getColumn(tableId).getColumnType());
    }

    @Test
    public void UndoRedoStacksEmpty() {
        // Nothing happens
        app.undo();

        // Nothing happens
        app.redo();
    }
}
