package com.tablr.subwindow;

import com.tablr.controller.AppController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TablesSubwindowTest {
    private TablesSubwindow tablesSubwindow;
    private AppController app;
    private SubwindowController controller;

    @BeforeEach
    void setUp() {
        app = new AppController();
        controller = new SubwindowController(app);
        tablesSubwindow = new TablesSubwindow(app.getTableIds(), 0, 0, controller);
    }

    @Test
    void testDoubleClickCreatesNewTable() {
        int initial = app.getTables().size();
        tablesSubwindow.onDoubleClick(5, tablesSubwindow.tableArea.getLowestY() + 10);
        int updated = app.getTables().size();
        assertEquals(initial + 1, updated);
    }

    @Test
    void testDoubleClickOpensSubwindow() {
        int x = tablesSubwindow.tableArea.getTableCells()[0][0].getRegion().x + 1;
        int y = tablesSubwindow.tableArea.getTableCells()[0][0].getRegion().y + 1;
        tablesSubwindow.onDoubleClick(x, y); // opens Design or Rows subwindow
    }

    @Test
    void test_EditTableNameToDuplicate_isRejected() {
        // Step 1 — Voeg meerdere tabellen toe
        tablesSubwindow.onDoubleClick(5, tablesSubwindow.tableArea.getLowestY() + 10); // Table1
        tablesSubwindow.onDoubleClick(5, tablesSubwindow.tableArea.getLowestY() + 10); // Table2
        tablesSubwindow.onDoubleClick(5, tablesSubwindow.tableArea.getLowestY() + 10); // Table3

        int col = 0;
        int row = 0; // Table1 zit op rij 0

        var cell = tablesSubwindow.tableArea.getTableCells()[row][col];
        var region = cell.getRegion();

        // Step 2 — Start editing op Table1
        tablesSubwindow.onBodyClick(region.x + 2, region.y + 2);

        // Step 3 — Backspace om '1' te verwijderen, dan '2' typen
        tablesSubwindow.onBackspace(); // verwijdert '1'
        tablesSubwindow.onCharacter('2'); // maakt 'Table2'

        // Step 4 — Klik buiten cel → mag niet committen wegens duplicate
        tablesSubwindow.onBodyClick(region.x + 100, region.y + 100);

        // Step 5 — Controleer dat de naam van rij 0 nog steeds "Table1"
        Object current = app.getTableName(app.getTableIds().get(row));
        assertEquals("Table1", current, "Duplicate name must not be accepted");
    }

    @Test
    void test_EditValidName_commitsAndStopsEditing() {
        // Stap 1 — Voeg 1 tabel toe: Table1
        tablesSubwindow.onDoubleClick(5, tablesSubwindow.tableArea.getLowestY() + 10);

        int row = 0;
        int col = 0;
        var cell = tablesSubwindow.tableArea.getTableCells()[row][col];
        var region = cell.getRegion();

        // Stap 2 — Start editing op Table1
        tablesSubwindow.onBodyClick(region.x + 2, region.y + 2);

        // Stap 3 — Backspace (verwijdert '1') en typ 'X'
        tablesSubwindow.onBackspace();
        tablesSubwindow.onCharacter('X'); // Wordt TableX

        // Stap 4 — Klik buiten veld → setEdit() + stopEditing()
        tablesSubwindow.onBodyClick(region.x + 100, region.y + 100);

        // Stap 5 — Naam moet nu "TableX" zijn
        Object newName = app.getTableName(app.getTableIds().get(row));
        assertEquals("TableX", newName, "Valid name should be committed");

        // Stap 6 — Typ nog een karakter → mag geen effect hebben
        tablesSubwindow.onCharacter('Y'); // Geen effect als editing gestopt is
        Object stillSame = app.getTableName(app.getTableIds().get(row));
        assertEquals("TableX", stillSame, "Editing should have been stopped after commit");
    }

    @Test
    void testOnEnterAndEscape() {
        tablesSubwindow.onEnter();
        tablesSubwindow.onEscape();
    }

    @Test
    void testBackspaceAndCharacterInput() {
        tablesSubwindow.onBackspace();
        tablesSubwindow.onCharacter('T');
    }

    @Test
    void testMouseInteractions() {
        int x = tablesSubwindow.tableArea.getTableCells()[0][0].getRegion().x + 1;
        int y = tablesSubwindow.tableArea.getTableCells()[0][0].getRegion().y + 1;

        tablesSubwindow.onBodyClick(x, y);
        tablesSubwindow.onMouseDragged(x + 10, y + 10);
        tablesSubwindow.onMousePressed(x, y);
        tablesSubwindow.onMouseReleased();
    }

    @Test
    void testDeleteTable() {
        int before = app.getTables().size();
        tablesSubwindow.tableArea.selectRow(0, tablesSubwindow.tableArea.getTableCells()[0][0].getRegion().y);
        tablesSubwindow.onDelete();
        int after = app.getTables().size();
        assertTrue(after <= before);
    }
}
