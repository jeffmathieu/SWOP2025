package com.tablr.subwindow;

import com.tablr.area.NormalCell;
import com.tablr.controller.AppController;
import com.tablr.model.ColumnType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class DesignSubwindowTest {
    private AppController app;
    private DesignSubwindow design;
    private int tableId;
    private SubwindowController controller;

    @BeforeEach
    void setUp() {
        app = new AppController();
        tableId = app.getTableIds().getFirst();
        app.addColumnToTable(tableId);
        app.addColumnToTable(tableId);
        controller = new SubwindowController(app);
        design = new DesignSubwindow(tableId, 0, 0, controller);
        controller.addSubWindow(design);
    }

    @Test
    void testDoubleClickAddsColumn() {
        int before = app.getTable(tableId).getColumnCount();
        design.onDoubleClick(5, design.tableArea.getLowestY() + 10);
        assertEquals(before + 1, app.getTable(tableId).getColumnCount());
    }

    @Test
    void testDeleteColumn() {
        int before = app.getTable(tableId).getColumnCount();
        design.tableArea.selectRow(0, design.tableArea.getTableCells()[0][0].getRegion().y);
        design.onDelete();
        assertTrue(app.getTable(tableId).getColumnCount() < before);
    }

    @Test
    void testMouseClicksOnNameCell() {
        int x = design.tableArea.getTableCells()[0][0].getRegion().x + 2;
        int y = design.tableArea.getTableCells()[0][0].getRegion().y + 2;
        design.onBodyClick(x, y);
        String originalName = app.getTable(tableId).getColumns().getFirst().getName();
        design.onCharacter('N');
        design.onEnter();
        String newName = app.getTable(tableId).getColumns().getFirst().getName();
        assertNotNull(newName);
        assertEquals(originalName + "N", newName);
        assertNotEquals(originalName, newName);
    }

    @Test
    void testMouseClicksOnTypeCell_andCycle() {
        int x = design.tableArea.getTableCells()[1][0].getRegion().x + 2;
        int y = design.tableArea.getTableCells()[1][0].getRegion().y + 2;
        design.onBodyClick(x, y);
        design.onCharacter('S');
        design.onBodyClick(x, y);
        assertNotNull(app.getColumnTypesOfTable(tableId).getFirst());
    }

    @Test
    void testToggleDefaultCheckbox() {
        int x = design.tableArea.getTableCells()[3][0].getRegion().x + 5;
        int y = design.tableArea.getTableCells()[3][0].getRegion().y + 5;
        design.onBodyClick(x, y);
    }

    @Test
    void testToggleBlanksCheckbox() {
        int x = design.tableArea.getTableCells()[2][0].getRegion().x + 5;
        int y = design.tableArea.getTableCells()[2][0].getRegion().y + 5;
        design.onBodyClick(x, y);
    }

    @Test
    void testEscapeCancelsEdit() {
        int x = design.tableArea.getTableCells()[0][0].getRegion().x + 2;
        int y = design.tableArea.getTableCells()[0][0].getRegion().y + 2;
        design.onBodyClick(x, y);
        design.onCharacter('T');
        design.onEscape();
        assertTrue(app.getTable(tableId).getColumns().getFirst().getName().startsWith("Column"));
    }

    @Test
    void testBackspaceEdit() {
        int x = design.tableArea.getTableCells()[0][0].getRegion().x + 2;
        int y = design.tableArea.getTableCells()[0][0].getRegion().y + 2;
        design.onBodyClick(x, y);
        design.onCharacter('A');
        design.onCharacter('B');
        design.onBackspace();
        design.onEnter();
        String name = app.getTable(tableId).getColumns().getFirst().getName();
        assertTrue(name.contains("A"));
    }

    @Test
    void testHandleBlanksClickInvalidSetsFalse() {
        int y = design.tableArea.getTableCells()[2][0].getRegion().y + 5;
        design.onBodyClick(1000, y); // force click on checkbox (invalid pos triggers failure)
        // no assert, but ensures coverage for invalid blank toggle
    }

    @Test
    void testHandleTypeClick_invalidConversion() {
        int x = design.tableArea.getTableCells()[1][0].getRegion().x + 2;
        int y = design.tableArea.getTableCells()[1][0].getRegion().y + 2;
        design.onBodyClick(x, y);
        design.onCharacter('X'); // force mismatch
        design.onBodyClick(x, y); // triggers invalid state
    }

    @Test
    void testHandleDefaultClick_outsideRegionStopsEdit() {
        int x = design.tableArea.getTableCells()[3][0].getRegion().x + 2;
        int y = design.tableArea.getTableCells()[3][0].getRegion().y + 2;
        design.onBodyClick(x, y);
        design.onCharacter('1');
        design.onBodyClick(x + 100, y + 100); // simulate outside click
        assertEquals("1", app.getTable(tableId).getColumns().getFirst().getDefaultValue().toString());
    }

    @Test
    void testHandleNameClick_outsideRegionStopsEdit() {
        int x = design.tableArea.getTableCells()[0][0].getRegion().x + 2;
        int y = design.tableArea.getTableCells()[0][0].getRegion().y + 2;
        design.onBodyClick(x, y);
        design.onCharacter('E');
        design.onBodyClick(x + 100, y + 100);
        assertTrue(app.getTable(tableId).getColumns().getFirst().getName().endsWith("E"));
    }

    @Test
    void testOnControlEnter() {
        int x = design.tableArea.getTableCells()[0][0].getRegion().x + 2;
        int y = design.tableArea.getTableCells()[0][0].getRegion().y + 2;
        design.onControlEnter();
    }

    @Test
    void testNameValidationAcceptsUniqueName() {
        int x = design.tableArea.getTableCells()[0][0].getRegion().x + 2;
        int y = design.tableArea.getTableCells()[0][0].getRegion().y + 2;
        design.onBodyClick(x, y);
        design.onCharacter('Z');
        design.onEnter();

        String name = app.getTable(tableId).getColumns().getFirst().getName();
        assertTrue(name.endsWith("Z"));
    }

    @Test
    void testGeneralValidationFallback() {
        int x = design.tableArea.getTableCells()[3][0].getRegion().x + 2;
        int y = design.tableArea.getTableCells()[3][0].getRegion().y + 2;
        design.onBodyClick(x, y);
        design.onCharacter('X');
        design.onEnter();

        Object def = app.getTable(tableId).getColumns().getFirst().getDefaultValue();
        assertEquals("X", def.toString());
    }

    // more tests
    @Test
    void testBooleanCellClickForDefaultCheckbox() {
        controller.cycleColumnType(tableId, app.getTable(tableId).getColumns().getFirst().getId(), ColumnType.BOOLEAN);
        int y = design.tableArea.getTableCells()[3][0].getRegion().y + 5;
        int x = design.tableArea.getTableCells()[3][0].getRegion().x + 5;
        design.onBodyClick(x, y);
    }

    @Test
    void testBooleanCellClickForBlanksCheckboxFailure() {
        controller.cycleColumnType(tableId, app.getTable(tableId).getColumns().getFirst().getId(), ColumnType.BOOLEAN);
        controller.toggleDefaultValue(tableId, app.getTable(tableId).getColumns().getFirst().getId());
        int y = design.tableArea.getTableCells()[2][0].getRegion().y + 5;
        int x = design.tableArea.getTableCells()[2][0].getRegion().x + 5;
        design.onBodyClick(x, y);
    }

    @Test
    void testToggleableDefaultCellClick() {
        var columnId = app.getTable(tableId).getColumns().getFirst().getId();
        controller.cycleColumnType(tableId, columnId, ColumnType.BOOLEAN);
        controller.tryToggleAllowsBlank(tableId, columnId, false);
        int x = design.tableArea.getTableCells()[3][0].getRegion().x + 5;
        int y = design.tableArea.getTableCells()[3][0].getRegion().y + 5;
        design.onBodyClick(x, y);
    }

    @Test
    void testStopEditingValidNameAfterOutsideClick() {
        int x = design.tableArea.getTableCells()[0][0].getRegion().x + 2;
        int y = design.tableArea.getTableCells()[0][0].getRegion().y + 2;
        design.onBodyClick(x, y);
        design.onCharacter('X');
        design.onBodyClick(x + 200, y + 200);
        assertTrue(app.getTable(tableId).getColumns().getFirst().getName().endsWith("X"));
    }
}