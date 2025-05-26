package usecases;

import com.tablr.controller.AppController;
import com.tablr.model.ColumnType;
import com.tablr.subwindow.FormSubwindow;
import com.tablr.subwindow.SubwindowController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Use Case Tests for FormSubwindow
 */
public class FormSubwindowUseCasesTest {

    private AppController app;
    private SubwindowController controller;
    private FormSubwindow form;
    private int tableId;

    @BeforeEach
    void setup() {
        app = new AppController();
        app.initializeTestApp();
        tableId = app.getTableIds().getFirst();

        // Add 3 columns of different types and 2 rows
        app.addColumnToTable(tableId);  // STRING
        app.addColumnToTable(tableId);  // INTEGER
        app.cycleColumnType(tableId, 2, ColumnType.INTEGER);
        app.addColumnToTable(tableId);  // BOOLEAN
        app.cycleColumnType(tableId, 3, ColumnType.BOOLEAN);
        app.addRowToTable(tableId);
        app.addRowToTable(tableId);

        controller = new SubwindowController(app);
        form = new FormSubwindow(tableId, 30, 30, 0, controller);
    }

    @Test // Use Case 4.9 – Edit a STRING cell and commit
    void testEditStringValue() {
        form.onClick(190, 90);
        form.onCharacter('H');
        form.onCharacter('i');
        form.onEnter();

        Object result = app.getRowValue(tableId, 1, 0);
        assertEquals("Hi", result);
    }

    @Test // Use Case 4.9 – Cancel edit with Escape
    void testEscapeCancelsEdit() {
        app.setRowValue(tableId, 1, 0, "Original");
        form.onClick(190, 90);
        form.onCharacter('Z');
        form.onEscape();
        assertEquals("Original", app.getRowValue(tableId, 1, 0));
    }

    @Test // Use Case 4.9 – Invalid INTEGER input is rejected
    void testInvalidIntegerInputRejected() {
        app.setRowValue(tableId, 2, 0, 123);
        form.onClick(190, 110);
        form.onCharacter('x');
        form.onEnter();
        assertEquals(123, app.getRowValue(tableId, 2, 0));
    }

    @Test // Use Case 4.9 – Valid INTEGER input is accepted
    void testValidIntegerInputAccepted() {
        form.onClick(190, 110); // integer field
        form.onCharacter('4');
        form.onCharacter('2');
        form.onEnter();
        assertEquals(42, app.getRowValue(tableId, 2, 0));
    }

    @Test // Use Case 4.9 – Toggle BOOLEAN value
    void testToggleBoolean() {
        Object before = app.getRowValue(tableId, 3, 0);
        form.onClick(200, 150);
        Object after = app.getRowValue(tableId, 3, 0);

        assertNotEquals(before, after);
        assertTrue(after == null || after instanceof Boolean);
    }

    @Test // Use Case 4.8 – Navigate down (PageDown)
    void testPageDownNavigatesDown() {
        form.onPageDown();
        form.onClick(190, 90);
        form.onCharacter('A');
        form.onEnter();
        assertEquals("A", app.getRowValue(tableId, 1, 1));
    }

    @Test // Use Case 4.8 – Navigate up (PageUp)
    void testPageUpNavigatesUp() {
        form.onPageDown(); // now at row 2
        form.onPageUp();   // back to row 1
        form.onClick(190, 90);
        form.onCharacter('B');
        form.onEnter();
        assertEquals("B", app.getRowValue(tableId, 1, 0));
    }

    @Test // Use Case 4.9 – Add row via Ctrl+N
    void testAddRow() {
        int before = app.getTable(tableId).getRowCount();
        form.onControlN();
        int after = app.getTable(tableId).getRowCount();
        assertEquals(before + 1, after);
    }

    @Test // Use Case 4.9 – Delete row via Ctrl+D
    void testDeleteRow() {
        int before = app.getTable(tableId).getRowCount();
        form.onControlD();
        int after = app.getTable(tableId).getRowCount();
        assertEquals(before - 1, after);
    }

    @Test // Use Case 4.9 – Backspace removes character
    void testBackspace() {
        form.onClick(190, 90);
        form.onCharacter('A');
        form.onCharacter('B');
        form.onBackspace();
        form.onEnter();
        assertEquals("A", app.getRowValue(tableId, 1, 0));
    }

    @Test // Use Case 4.7 – Form opens and does not crash
    void testFormSubwindowOpensSafely() {
        assertNotNull(form);
        assertEquals(tableId, form.getTableId());
    }
}

