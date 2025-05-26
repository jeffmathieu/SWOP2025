package com.tablr.controller;

import com.tablr.model.Column;
import com.tablr.undoRedo.*;
import com.tablr.model.ColumnType;
import com.tablr.model.Table;
import com.tablr.subwindow.SubwindowController;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * AppController is the central coordinator of the application.
 * It acts as a mediator between the UIController, TableController, and the SubwindowController (MainWindow).
 */
public class AppController {
    private final TableController tableController;
    private final SubwindowController subwindowController;
    private final UIController uiController;
    private final CommandManager commandManager = new CommandManager();


    /**
     * Constructs an AppController and initializes all core controllers.
     */
    public AppController() {
        this.tableController = new TableController();
        initializeTables();
        this.subwindowController = new SubwindowController(this);
        this.uiController = new UIController(this);
    }

    /**
     * Initializes the UI and launches the application.
     */
    public void initializeApp() {
        uiController.show();
    }

    public void initializeTestApp() {
        tableController.clearTables();
        tableController.addTableDirectly(new Table("TestTable1", 1));
        tableController.addTableDirectly(new Table("TestTable2", 2));
    }


    /**
     * Initializes a default set of tables.
     */
    private void initializeTables() {
        for (int i = 0; i < 5; i++) {
            tableController.createTable();
        }
    }


    // ─────────────────────────────────────────────────────────────
    // TABLE MANAGEMENT
    // ─────────────────────────────────────────────────────────────

    /**
     * Creates a new table and adds it to the list of tables in tableController.
     */
    public void createTable() {
        commandManager.executeCommand(new CreateTableCommand(tableController));
    }

    /**
     * Deletes a table by its ID.
     *
     * @param tableId The ID of the table to delete.
     */
    public void deleteTable(int tableId) {
        subwindowController.closeAllSubwindowsForTable(tableId);
        commandManager.executeCommand(new DeleteTableCommand(tableController, tableId));
    }

    /**
     * Renames a table by its ID.
     *
     * @param tableId The ID of the table to rename.
     * @param newName The new name for the table.
     */
    public void renameTable(int tableId, String newName) {
        String oldName = tableController.getTableName(tableId);
        Command cmd = new RenameTableCommand(tableController, tableId, oldName, newName);
        commandManager.executeCommand(cmd);
    }

    /**
     * Gets the list of table IDs.
     *
     * @return A list of table IDs.
     */
    public List<Integer> getTableIds() {
        return tableController.getTableIds();
    }

    /**
     * Retrieve list of column types of given table
     * @param tableId | ID of table
     * @return list of column types
     */
    public List<ColumnType> getColumnTypesOfTable(int tableId) {
        return tableController.getColumnTypesOfTable(tableId);
    }

    /**
     * Gets a deep clone of the list of tables.
     *
     * @return A deep clone of the list of tables.
     */
    public List<Table> getTables() {
        List<Table> tables = tableController.getTables();
        List<Table> newTables = new ArrayList<>();
        for (Table table : tables) {
            newTables.add(table.deepClone());
        }
        return newTables;
    }

    /**
     * Gets a table by its ID.
     *
     * @param tableId The ID of the table.
     * @return A deep clone of the table object.
     */
    public Table getTable(int tableId) {
        return tableController.cloneTable(tableId);
    }

    /**
     * Retrieve list of column names of given table.
     * @param tableId | ID of table
     * @return List of column names
     */
    public List<String> getTableColumnNames(int tableId) {
        return tableController.getTableColumnNames(tableId);
    }

    /**
     * Retrieve list of column IDs of given table.
     * @param tableId | ID of table
     * @return List of column IDs
     */
    public List<Integer> getTableColumnIds(int tableId) {
        return tableController.getColumnIdsOfTable(tableId);
    }

    /**
     * Retrieve list of row values of given row in given table.
     * @param tableId | ID of table
     * @param rowIndex | Index of row
     * @return List of row values
     */
    public List<Object> getTableRowValues(int tableId, int rowIndex) {
        return tableController.getTableRowValues(tableId, rowIndex);
    }

    /**
     * Checks if a table exists by its name.
     *
     * @param tableName The name of the table.
     * @return True if the table exists, false otherwise.
     */
    public boolean tableExists(String tableName) {
        return tableController.hasTable(tableName);
    }

    /**
     * Attempts to change the default value of a column in a table.
     *
     * @param tableId The ID of the table.
     * @param columnId The ID of the column.
     * @param value The new default value.
     * @return True if the operation was successful, false otherwise.
     */
    public boolean tryChangeDefaultValue(int tableId, int columnId, String value) {
        try {
            String oldValue = tableController.getColumn(tableId, columnId).getDefaultValueAsString();
            commandManager.executeCommand(new ChangeDefaultValueCommand(tableController, tableId, columnId, oldValue, value));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Toggles the default value of a column in a table.
     *
     * @param tableId The ID of the table.
     * @param columnId The ID of the column.
     */
    public void toggleDefaultValue(int tableId, int columnId) {
        Column<?> column = tableController.getColumn(tableId, columnId);
        String oldValue = column.getDefaultValue() == null ? "" : column.getDefaultValue().toString();

        Boolean newValueBool = tableController.toggleDefaultValue(tableId, columnId);
        String newValue = newValueBool == null ? "" : newValueBool.toString();

        commandManager.executeCommand(new ChangeDefaultValueCommand(tableController, tableId, columnId, oldValue, newValue));
    }

    /**
     * Checks if a table name is valid.
     *
     * @param tableName The name of the table.
     * @return True if the table name is valid, false otherwise.
     */
    public boolean isValidTableName(String tableName) {
        return tableController.isValidTableName(tableName);
    }

    /**
     * Checks if a column name is valid.
     *
     * @param tableId The ID of the table.
     * @param columnName The name of the column.
     * @return True if the column name is valid, false otherwise.
     */
    public boolean isValidColumnName(int tableId, String columnName) {
        return tableController.isValidColumnName(tableId, columnName);
    }

    /**
     * Checks if a column value is valid.
     *
     * @param tableId The ID of the table.
     * @param columnId The ID of the column.
     * @param value The value to validate.
     * @return True if the column value is valid, false otherwise.
     */
    public boolean isValidColumnValue(int tableId, int columnId, Object value) {
        return tableController.isValidColumnValue(tableId, columnId, value);
    }

    /**
     * Adds a column to a specified table.
     *
     * @param tableId The ID of the table.
     */
    public void addColumnToTable(int tableId) {
        commandManager.executeCommand(new AddColumnCommand(tableController, tableId));
    }

    /**
     * Deletes a column from a specified table.
     *
     * @param tableId The ID of the table.
     * @param columnId The ID of the column to delete.
     */
    public void deleteColumn(int tableId, int columnId) {
        commandManager.executeCommand(new DeleteColumnCommand(tableController, tableId, columnId));

        if (tableController.getColumnCount(tableId) == 0) {
            subwindowController.closeRowsSubwindowIfOpen(tableId);
        }
    }

    /**
     * Gets the column ID at a specific row index in a table.
     *
     * @param tableId The ID of the table.
     * @param rowIndex The row index.
     * @return The column ID at the specified row index.
     */
    public int getColumnIdAt(int tableId, int rowIndex) {
        return tableController.getColumnIdAt(tableId, rowIndex);
    }

    /**
     * Renames a column in a table.
     *
     * @param tableId The ID of the table.
     * @param columnId The ID of the column to rename.
     * @param newName The new name for the column.
     */
    public void renameColumn(int tableId, int columnId, String newName) {
        String oldName = tableController.getColumnName(tableId, columnId);
        commandManager.executeCommand(new RenameColumnCommand(tableController, tableId, columnId, oldName, newName));
    }


    /**
     * Adds a row to a specified table.
     *
     * @param tableId The ID of the table.
     */
    public void addRowToTable(int tableId) {
        commandManager.executeCommand(new AddRowCommand(tableController, tableId));
    }

    /**
     * Removes a row from a specified table.
     *
     * @param tableId The ID of the table.
     * @param rowIndex The index of the row to remove.
     */
    public void removeRowFromTable(int tableId, int rowIndex) {
        if(rowIndex >= 0 && rowIndex < tableController.getRowCount(tableId)) {
            commandManager.executeCommand(new DeleteRowCommand(tableController, tableId, rowIndex));
        }

    }

    /**
     * Sets the value of a specific cell in a table.
     *
     * @param tableId The ID of the table.
     * @param columnId The ID of the column.
     * @param rowIndex The index of the row.
     * @param value The value to set.
     */
    public void setRowValue(int tableId, int columnId, int rowIndex, Object value) {
        Object oldValue = tableController.getRowValue(tableId, columnId, rowIndex);
        Command cmd = new SetCellValueCommand(tableController, tableId, columnId, rowIndex, oldValue, value);
        commandManager.executeCommand(cmd);
    }

    /**
     * Gets the value of a specific cell in a table.
     *
     * @param tableId The ID of the table.
     * @param columnId The ID of the column.
     * @param rowIndex The index of the row.
     * @return The value of the cell.
     */
    public Object getRowValue(int tableId, int columnId, int rowIndex) {
        return tableController.getRowValue(tableId, columnId, rowIndex);
    }

    /**
     * Attempts to toggle the "allows blank" setting for a column in a table.
     *
     * @param tableId The ID of the table.
     * @param columnId The ID of the column.
     * @param newAllowsBlank The new "allows blank" setting.
     * @return True if the operation was successful, false otherwise.
     */
    public boolean tryToggleAllowsBlank(int tableId, int columnId, boolean newAllowsBlank) {
        if (tableController.tryToggleAllowsBlank(tableId, columnId, newAllowsBlank)) {
            commandManager.executeCommand(new ToggleAllowsBlankCommand(tableController, tableId, columnId, newAllowsBlank));
            return true;
        }
        return false;
    }

    /**
     * Checks if a column type conversion is valid.
     *
     * @param tableId The ID of the table.
     * @param columnId The ID of the column.
     * @param type The new column type.
     * @return True if the type conversion is valid, false otherwise.
     */
    public boolean isValidColumnTypeConversion(int tableId, int columnId, ColumnType type) {
        return tableController.isValidTypeConversion(tableId, columnId, type);
    }

    /**
     * Cycles the column type for a specific column in a table.
     *
     * @param tableId The ID of the table.
     * @param columnId The ID of the column.
     * @param newType The new column type.
     */
    public void cycleColumnType(int tableId, int columnId, ColumnType newType) {
        commandManager.executeCommand(new ChangeColumnTypeCommand(tableController, tableId, columnId, newType));
    }

    // Event handling methods for user interactions with the UI.

    /**
     * Handles a single mouse click event.
     *
     * @param x The x-coordinate of the click.
     * @param y The y-coordinate of the click.
     */
    public void onClick(int x, int y) {
        subwindowController.onClick(x, y);
    }

    /**
     * Handles a double mouse click event.
     *
     * @param x The x-coordinate of the double click.
     * @param y The y-coordinate of the double click.
     */
    public void onDoubleClick(int x, int y) {
        subwindowController.onDoubleClick(x, y);
    }

    /**
     * Handles the "Control + Enter" key combination.
     */
    public void onControlEnter() {
        subwindowController.onControlEnter();
    }

    /**
     * Handles the "Enter" key press.
     */
    public void onEnter() {
        subwindowController.onEnter();
    }

    /**
     * Handles the "Escape" key press.
     */
    public void onEscape() {
        subwindowController.onEscape();
    }

    /**
     * Handles a character input event.
     *
     * @param keychar The character input.
     */
    public void onCharacter(char keychar) {
        subwindowController.onCharacter(keychar);
    }

    /**
     * Handles the "Backspace" key press.
     */
    public void onBackspace() {
        subwindowController.onBackspace();
    }

    /**
     * Handles the "Delete" key press.
     */
    public void onDelete() {
        subwindowController.onDelete();
    }

    /**
     * Handles a mouse press event.
     *
     * @param x The x-coordinate of the mouse press.
     * @param y The y-coordinate of the mouse press.
     */
    public void onMousePressed(int x, int y) {
        subwindowController.onMousePressed(x, y);
    }

    /**
     * Handles a mouse drag event.
     *
     * @param x The x-coordinate of the mouse drag.
     * @param y The y-coordinate of the mouse drag.
     */
    public void onMouseDragged(int x, int y) {
        subwindowController.onMouseDragged(x, y);
    }

    /**
     * Handles a mouse release event.
     */
    public void onMouseReleased() {
        subwindowController.onMouseReleased();
    }

    /**
     * Paints the subwindows using the provided Graphics object.
     *
     * @param g The Graphics object used for painting.
     */
    public void paintWindows(Graphics g) {
        subwindowController.paint(g);
    }

    /**
     * Handles the "Control + T" key combination.
     */
    public void onCtrlT() {
        subwindowController.onCtrlT();
    }

    public void onCtrlF() { subwindowController.onCtrlF();}
    public void onControlN() { subwindowController.onCtrlN();}

    public void onPageUp() { subwindowController.onPageUp();}
    public void onPageDown() { subwindowController.onPageDown();}
    public void onControlD() { subwindowController.onControlD();}

    private int getTableIdIfUndoingCreate() {
        Command top = commandManager.peekUndo();
        if (top instanceof CreateTableCommand command) {
            return command.getCreatedTableId();
        }
        return -1;
    }

    private int getTableIdIfRedoingDelete() {
        Command top = commandManager.peekRedo();
        if (top instanceof DeleteTableCommand command) {
            return command.getDeletedTableId();
        }
        return -1;
    }

    /**
     * Undo an action.
     */
    public void undo() {
        int deletedTableId = getTableIdIfUndoingCreate();

        commandManager.undo();

        if (deletedTableId != -1) {
            subwindowController.closeAllSubwindowsForTable(deletedTableId);
        }

        for (Table table : tableController.getTables()) {
            closeRowsSubwindowIfNoColumns(table.getId());
        }
        subwindowController.updateTableAreas(-1);
    }

    /**
     * Redo an undone action.
     */
    public void redo() {
        int deletedTableId = getTableIdIfRedoingDelete();

        commandManager.redo();

        if (deletedTableId != -1) {
            subwindowController.closeAllSubwindowsForTable(deletedTableId);
        }

        subwindowController.updateTableAreas(-1);
    }

    /**
     * Retrieves the width of the canvas.
     * @return width of canvas
     */
    public int getCanvasWidth() {
        return uiController.getCanvasWidth();
    }

    /**
     * Retrieves the height of the canvas.
     * @return height of canvas
     */
    public int getCanvasHeight() {
        return uiController.getCanvasHeight();
    }

    /**
     * Checks for all tables with no columns if they have RowsSubwindow open.
     *
     * @param tableId | ID of table we need checked.
     */
    private void closeRowsSubwindowIfNoColumns(int tableId) {
        if (tableController.getColumnCount(tableId) == 0) {
            subwindowController.closeRowsSubwindowIfOpen(tableId);
        }
    }

    /**
     * Retrieve name of table
     * @param tableId | ID of table
     * @return name of table
     */
    public String getTableName(int tableId) {
        return tableController.getTableName(tableId);
    }

    /**
     * Retrieve given column of given table
     * @param tableId | ID of table
     * @param columnId | ID of column
     * @return Column
     */
    public Column<?> getColumn(int tableId, int columnId) {
        return tableController.getColumn(tableId, columnId);
    }

    /**
     * Retrieve amount of columns of a table
     * @param tableId | ID of table
     * @return amount of columns
     */
    public int getColumnCount(int tableId) {
        return tableController.getColumnCount(tableId);
    }

    /**
     * retrieve if given column of given table allows blanks.
     * @param tableId | ID of table
     * @param columnId | ID of column
     * @return True if allows blank, False otherwise
     */
    public boolean getAllowsBlank(int tableId, int columnId) {
        return tableController.getAllowsBlank(tableId, columnId);
    }
}