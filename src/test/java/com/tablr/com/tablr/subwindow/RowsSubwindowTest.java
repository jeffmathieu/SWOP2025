package com.tablr.subwindow;

import com.tablr.controller.AppController;
import com.tablr.model.ColumnType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RowsSubwindowTest {
    private AppController app;
    private RowsSubwindow rows;
    private int tableId;

    @BeforeEach
    void setUp() {
        app = new AppController();
        tableId = app.getTableIds().getFirst();

        // Voeg StringColumn toe met default waarde
        app.addColumnToTable(tableId); // default = String
        app.addRowToTable(tableId); // zorgt voor 1 bewerkbare rij

        rows = new RowsSubwindow(tableId, 0, 0, new SubwindowController(app));
    }

    @Test
    void testEditRowValue_setsValueCorrectly() {
        // Step 1: Klik op cel om te beginnen met editen
        int x = rows.tableArea.getTableCells()[0][0].getRegion().x + 2;
        int y = rows.tableArea.getTableCells()[0][0].getRegion().y + 2;
        rows.onBodyClick(x, y);

        // Step 2: Typ "Hello"
        rows.onCharacter('H');
        rows.onCharacter('e');
        rows.onCharacter('l');
        rows.onCharacter('l');
        rows.onCharacter('o');

        // Step 3: Bevestig met Enter
        rows.onEnter();

        // Step 4: Controleer of de waarde correct werd aangepast
        String value = (String) app.getRowValue(tableId,1,0);
        assertEquals("Hello", value);
    }

    @Test
    void testEditIntegerValue_invalidThenValid() {
        // Voeg tweede kolom toe en maak hem van type INTEGER
        app.addColumnToTable(tableId);
        int columnIndex = 1;
        int columnId = app.getTable(tableId).getColumns().get(columnIndex).getId();
        app.cycleColumnType(tableId, columnId, ColumnType.EMAIL);
        app.cycleColumnType(tableId, columnId, ColumnType.INTEGER);

        // Voeg extra rij toe zodat we zeker zijn dat er een rij is
        app.addRowToTable(tableId);
        rows.updateTableArea(); // update view

        // Start edit op cel [1][1] (tweede kolom, tweede rij)
        int x = rows.tableArea.getTableCells()[columnIndex][1].getRegion().x + 2;
        int y = rows.tableArea.getTableCells()[columnIndex][1].getRegion().y + 2;
        rows.onBodyClick(x, y);

        rows.onCharacter('4');
        rows.onCharacter('2');
        rows.onEnter(); // nu is het geldig

        Object valid = app.getRowValue(tableId, 2, columnIndex);
        assertEquals(42, valid);
    }

    @Test
    void testDoubleClickAddsRow() {
        int before = app.getTable(tableId).getRowCount();
        rows.onDoubleClick(5, rows.tableArea.getLowestY() + 10);
        int after = app.getTable(tableId).getRowCount();
        assertEquals(before + 1, after);
    }

    @Test
    void testControlEnterOpensDesignWindow() {
        rows.onControlEnter(); // Should not throw
    }

    @Test
    void testDeleteRow() {
        int before = app.getTable(tableId).getRowCount();
        rows.tableArea.selectRow(0, rows.tableArea.getTableCells()[0][0].getRegion().y);
        rows.onDelete();
        int after = app.getTable(tableId).getRowCount();
        assertTrue(after < before);
    }

    @Test
    void testMouseEvents() {
        rows.onMouseDragged(100, 100);
        rows.onMousePressed(10, 10);
        rows.onMouseReleased();
    }

    @Test
    void testStartNormalCellEdit() {
        int x = rows.tableArea.getTableCells()[0][0].getRegion().x + 2;
        int y = rows.tableArea.getTableCells()[0][0].getRegion().y + 2;
        rows.onBodyClick(x, y);
        rows.onCharacter('7');
        rows.onBackspace();
        rows.onEnter();
    }

    @Test
    void testEscapeCancelsEdit() {
        int x = rows.tableArea.getTableCells()[0][0].getRegion().x + 2;
        int y = rows.tableArea.getTableCells()[0][0].getRegion().y + 2;
        rows.onBodyClick(x, y);
        rows.onEscape();
    }

    @Test
    void testBooleanCellToggle() {
        app.addColumnToTable(tableId); // Add another column to get index 1
        app.cycleColumnType(tableId, app.getTable(tableId).getColumns().get(1).getId(), com.tablr.model.ColumnType.BOOLEAN);
        app.addRowToTable(tableId); // Add a row to affect BooleanCell

        rows.updateTableArea();

        int x = rows.tableArea.getTableCells()[1][0].getRegion().x + 4;
        int y = rows.tableArea.getTableCells()[1][0].getRegion().y + 4;

        rows.onBodyClick(x, y);
        rows.onCharacter(' ');
    }

    @Test
    void testNoFormsubwindow() {
        rows.onControlF(); // no effect
    }
}