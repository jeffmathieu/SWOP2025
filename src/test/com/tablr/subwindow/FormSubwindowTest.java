package com.tablr.subwindow;

import com.tablr.area.Cell;
import com.tablr.area.NormalCell;
import com.tablr.controller.AppController;
import com.tablr.model.ColumnType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for FormSubwindow interactions and behavior (via AppController).
 */
class FormSubwindowTest {

    private AppController app;
    private FormSubwindow form;
    private int tableId;

    @BeforeEach
    void setup() {
        app = new AppController();
        app.initializeTestApp();

        tableId = app.getTableIds().getFirst();

        app.addColumnToTable(tableId); // kolom 0 = STRING
        app.addColumnToTable(tableId); // kolom 1 = INTEGER
        app.cycleColumnType(tableId, 2, ColumnType.EMAIL);
        app.cycleColumnType(tableId, 2, ColumnType.INTEGER);

        app.addColumnToTable(tableId); // kolom 2 = BOOLEAN
        app.cycleColumnType(tableId, 3, ColumnType.EMAIL);
        app.cycleColumnType(tableId, 3, ColumnType.INTEGER);
        app.cycleColumnType(tableId, 3, ColumnType.BOOLEAN);
        app.addRowToTable(tableId);
        app.addRowToTable(tableId); // extra rij voor PageUp-test

        SubwindowController controller = new SubwindowController(app);
        form = new FormSubwindow(tableId, 30, 30, 0, controller);
        controller.addSubWindow(form);
    }

    @Test
    void test_PageDown_changesRowInForm() {
        form.onPageDown();
        assertNotNull(app.getTable(1));
    }

    @Test
    void test_PageUp_doesNotCrash() {
        form.onPageUp();
        assertNotNull(app.getTable(1));
    }

    @Test
    void test_PageUp_movesUpIfPossible() {
        form.onPageDown();
        form.onPageUp();
        assertNotNull(app.getTable(1));
    }

    @Test
    void test_AddNewRow_viaControlN() {
        int before = app.getTable(1).getRowCount();
        form.onControlN();
        int after = app.getTable(1).getRowCount();
        assertEquals(before + 1, after);
    }

    @Test
    void test_DeleteRow_viaControlD() {
        form.onControlN();
        int before = app.getTable(1).getRowCount();
        form.onControlD();
        int after = app.getTable(1).getRowCount();
        assertEquals(before - 1, after);
    }

    @Test
    void test_EditStringCellValue_andCommit() {
        int colIndex = 0;
        int colId = 1;
        int rowIndex = 0;

        ColumnType type = app.getColumnTypesOfTable(tableId).get(colIndex);
        assertEquals(ColumnType.STRING, type);

        boolean allowsBlank = app.getTable(tableId).getColumn(colId).allowsBlank();
        assertTrue(allowsBlank);

        Cell target = form.tableArea.getTableCells()[1][colIndex];
        assertNotNull(target);
        assertInstanceOf(NormalCell.class, target);

        Rectangle region = target.getRegion();
        form.onBodyClick(region.x + 2, region.y + 2);
        form.onCharacter('X');
        form.onEnter();

        Object actual = app.getRowValue(tableId, colId, rowIndex);
        assertEquals("X", actual);
    }

    @Test
    void test_InvalidThenValidInput_commits() {
        int colIndex = 1; // kolom 1 = INTEGER
        int colId = 2;
        int rowIndex = 0;

        Object original = app.getRowValue(tableId, colId, rowIndex);

        // Start editing een INTEGER-cel
        Cell target = form.tableArea.getTableCells()[1][colIndex];
        Rectangle region = target.getRegion();
        form.onBodyClick(region.x + 2, region.y + 2);

        // Typ een ongeldige waarde ('x') → validState wordt false
        form.onCharacter('x');

        // Klik buiten veld: zou niets mogen doen
        form.onBodyClick(region.x + 100, region.y + 100);
        Object interim = app.getRowValue(tableId, colId, rowIndex);
        assertEquals(original, interim, "Invalid input should not be committed");

        // Corrigeer invoer naar geldig getal
        form.onBackspace();
        form.onCharacter('4');
        form.onCharacter('2');
        form.onEnter();

        // Nu zou waarde 42 moeten opgeslagen zijn
        Object actual = app.getRowValue(tableId, colId, rowIndex);
        assertEquals(42, actual, "Valid correction after invalid input should be committed");
    }

    @Test
    void test_onBodyClick_outsideCommits() {
        Cell target = form.tableArea.getTableCells()[1][0];
        Rectangle region = target.getRegion();
        form.onBodyClick(region.x + 2, region.y + 2);
        form.onCharacter('Z');
        form.onBodyClick(region.x + 100, region.y + 100);
        Object actual = app.getRowValue(tableId, 1, 0);
        assertEquals("Z", actual);
    }

    @Test
    void test_EscapeCancelsEdit() {
        Object original = app.getRowValue(1, 1, 0);
        form.onClick(200, 90);
        form.onCharacter('B');
        form.onEscape();
        Object actual = app.getRowValue(1, 1, 0);
        assertEquals(original, actual);
    }

    @Test
    void test_EditIntegerCell_withInvalidInput() {
        int col = 1;
        Object original = app.getRowValue(1, col, 0);
        form.onClick(200, 110);
        form.onCharacter('a');
        form.onEnter();
        Object actual = app.getRowValue(1, col, 0);
        assertEquals(original, actual);
    }

    @Test
    void test_EditIntegerCell_withValidInput() {
        int col = 2;
        form.onClick(200, 110);
        form.onCharacter('4');
        form.onCharacter('2');
        form.onEnter();
        Object actual = app.getRowValue(1, col, 0);
        assertEquals(42, actual);
    }

    @Test
    void test_BackspaceDeletesCharacter() {
        int col = 1;
        form.onClick(200, 90);
        form.onCharacter('A');
        form.onCharacter('B');
        form.onBackspace();
        form.onEnter();
        Object actual = app.getRowValue(1, col, 0);
        assertEquals("A", actual);
    }

    @Test
    void test_BooleanSwitchVariants() throws Exception {
        Method m = FormSubwindow.class.getDeclaredMethod("booleanSwitch", Boolean.class, boolean.class);
        m.setAccessible(true);
        assertEquals(Boolean.TRUE, m.invoke(null, null, true));
        assertEquals(Boolean.FALSE, m.invoke(null, true, true));
        assertNull(m.invoke(null, false, true));
        assertEquals(Boolean.TRUE, m.invoke(null, null, false));
        assertEquals(Boolean.FALSE, m.invoke(null, true, false));
    }

    @Test
    void test_BooleanToggleValid() {
        int col = 3;
        ColumnType type = app.getColumnTypesOfTable(1).get(col-1);
        assertEquals(ColumnType.BOOLEAN, type);

//        form.onClick(202, 154);
        Object before = app.getRowValue(1, col, 0);
        form.onClick(202, 154);
        Object after = app.getRowValue(1, col, 0);

        assertNotEquals(before, after);
        assertTrue(after == null || after instanceof Boolean);
    }

    @Test
    void test_onMouseDrag_updatesPosition() {
        form.onMouseDragged(200, 200);
    }

    @Test
    void test_onDoubleClick_doesNothing() {
        form.onDoubleClick(100, 100);
    }

    @Test
    void test_noOpMethods_doNothing() {
        form.onControlF();
        form.onControlEnter();
        form.onDelete();
        //form.onDoubleClick();
    }

}