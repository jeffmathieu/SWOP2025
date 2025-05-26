package com.tablr.subwindow;

import com.tablr.controller.AppController;
import com.tablr.model.Column;
import com.tablr.model.ColumnType;
import com.tablr.model.Table;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Manages the lifecycle and behavior of subwindows in the application.
 * Provides functionality for adding, removing, focusing, and rendering subwindows.
 */
public class SubwindowController {

    private final List<Subwindow> subWindows = new ArrayList<>();
    private final List<Subwindow> freshSubWindows = new ArrayList<>();
    private Subwindow focusedWindow;
    private final AppController mediator;

    /**
     * Constructs a SubwindowController instance.
     *
     * @param appController The application controller that mediates interactions.
     */
    public SubwindowController(AppController appController) {
        this.mediator = appController;
        openTablesSubWindow();
    }

    /**
     * Opens a new TablesSubwindow and adds it to the controller.
     */
    private void openTablesSubWindow() {
        TablesSubwindow sub = new TablesSubwindow(mediator.getTableIds(), 100, 100, this);
        addSubWindow(sub);
    }

    /**
     * Adds a subwindow to the controller and sets it as focused.
     *
     * @param sub The subwindow to add.
     */
    public void addSubWindow(Subwindow sub) {
        subWindows.add(sub);
        setFocused(sub);
    }

    /**
     * Updates the table areas of subwindows with the specified table ID.
     *
     * @param id The ID of the table to update.
     */
    public void updateTableAreas(int id) {
        for (Subwindow subWindow : freshSubWindows) {
            if (id == -1 || subWindow.getTableId() == id) {
                subWindow.updateTableArea();
            }
        }
    }

    /**
     * Removes a subwindow from the controller.
     * If the removed subwindow was focused, the next freshest subwindow is focused.
     *
     * @param subwindow The subwindow to remove.
     */
    public void removeSubWindow(Subwindow subwindow) {
        subWindows.remove(subwindow);
        freshSubWindows.remove(subwindow);
        if (focusedWindow == subwindow) {
            focusedWindow = getFreshestSubwindow();
            if (focusedWindow != null) {
                focusedWindow.setFocused(true);
            }
        }
    }

    /**
     * Closes all subwindows that are currently associated with the specified table ID.
     *
     * @param tableId the ID of the table for which all associated subwindows should be closed
     */
    public void closeAllSubwindowsForTable(int tableId) {
        for (Subwindow w : new ArrayList<>(subWindows)) {
            if (w.getTableId() == tableId) {
                removeSubWindow(w);
            }
        }
    }

    /**
     * Checks if rowssubwindows are open for a certain table and closes them.
     *
     * @param tableId | ID of table we want to check for.
     */
    public void closeRowsSubwindowIfOpen(int tableId) {
        List<Subwindow> toClose = new ArrayList<>();

        for (Subwindow sw : freshSubWindows) {
            if (sw instanceof RowsSubwindow rsw && rsw.getTableId() == tableId) {
                toClose.add(rsw);
            }
        }

        for (Subwindow sw : toClose) {
            removeSubWindow(sw);
        }
    }

    /**
     * Sets the specified subwindow as focused.
     *
     * @param sub The subwindow to focus.
     */
    private void setFocused(Subwindow sub) {
        if (focusedWindow != null) {
            focusedWindow.setFocused(false);
        }
        focusedWindow = sub;
        focusedWindow.setFocused(true);
        freshSubWindows.remove(focusedWindow);
        freshSubWindows.add(focusedWindow);
    }

    /**
     * Retrieves the freshest subwindow.
     *
     * @return The freshest subwindow, or null if none exist.
     */
    private Subwindow getFreshestSubwindow() {
        if (freshSubWindows.isEmpty()) {
            return null;
        }
        return freshSubWindows.getLast();
    }

    /**
     * Retrieves the list of subwindows ordered from freshest to least fresh.
     *
     * @return A list of subwindows ordered by freshness.
     */
    private List<Subwindow> getFreshSubWindowsOrderedFreshest() {
        return freshSubWindows.reversed();
    }

    /**
     * Retrieves the list of subwindows ordered from least fresh to freshest.
     *
     * @return A list of subwindows ordered by freshness.
     */
    private List<Subwindow> getFreshSubWindowsOrderedLeastFresh() {
        return freshSubWindows;
    }

    /**
     * Retrieves the application controller mediator.
     *
     * @return The application controller.
     */
    public AppController getMediator() {
        return mediator;
    }

    /**
     * Paints all subwindows managed by the controller.
     *
     * @param g The Graphics object used for rendering.
     */
    public void paint(Graphics g) {
        g.setColor(new Color(220, 220, 220));
        g.fillRect(0, 0, 850, 650);

        for (Subwindow win : getFreshSubWindowsOrderedLeastFresh()) {
            win.draw(g);
        }
    }

    /**
     * Handles the Control+Enter key event for the focused subwindow.
     */
    public void onControlEnter() {
        if (focusedWindow != null) {
            focusedWindow.onControlEnter();
        }
    }

    /**
     * Handles the Enter key event for the focused subwindow.
     */
    public void onEnter() {
        if (focusedWindow != null) {
            focusedWindow.onEnter();
        }
    }

    /**
     * Handles the Escape key event for the focused subwindow.
     */
    public void onEscape() {
        if (focusedWindow != null) {
            focusedWindow.onEscape();
        }
    }

    /**
     * Handles character input events for the focused subwindow.
     *
     * @param keychar The character input.
     */
    public void onCharacter(char keychar) {
        if (focusedWindow != null) {
            focusedWindow.onCharacter(keychar);
        }
    }

    /**
     * Handles the Backspace key event for the focused subwindow.
     */
    public void onBackspace() {
        if (focusedWindow != null) {
            focusedWindow.onBackspace();
        }
    }

    /**
     * Handles the Delete key event for the focused subwindow.
     */
    public void onDelete() {
        if (focusedWindow != null) {
            focusedWindow.onDelete();
        }
    }

    /**
     * Handles mouse events to activate a subwindow and perform an action.
     *
     * @param x The x-coordinate of the mouse event.
     * @param y The y-coordinate of the mouse event.
     * @param event The action to perform on the subwindow.
     */
    public void windowActivationMouseEvent(int x, int y, BiConsumer<Subwindow, int[]> event) {
        for (Subwindow sub : getFreshSubWindowsOrderedFreshest()) {
            if (sub.isInside(x, y)) {
                setFocused(sub);
                event.accept(sub, new int[]{x, y});
                return;
            }
        }
    }

    /**
     * Handles a single mouse click event.
     *
     * @param x The x-coordinate of the click.
     * @param y The y-coordinate of the click.
     */
    public void onClick(int x, int y) {
        windowActivationMouseEvent(x, y, (Subwindow sub, int[] coords) -> sub.onClick(coords[0], coords[1]));
    }

    /**
     * Handles a double-click mouse event.
     *
     * @param x The x-coordinate of the double click.
     * @param y The y-coordinate of the double click.
     */
    public void onDoubleClick(int x, int y) {
        windowActivationMouseEvent(x, y, (Subwindow sub, int[] coords) -> sub.onDoubleClick(coords[0], coords[1]));
    }

    /**
     * Handles a mouse press event.
     *
     * @param x The x-coordinate of the mouse press.
     * @param y The y-coordinate of the mouse press.
     */
    public void onMousePressed(int x, int y) {
        windowActivationMouseEvent(x, y, (Subwindow sub, int[] coords) -> sub.onMousePressed(coords[0], coords[1]));
    }

    /**
     * Handles a mouse drag event for the focused subwindow.
     *
     * @param x The x-coordinate of the mouse drag.
     * @param y The y-coordinate of the mouse drag.
     */
    public void onMouseDragged(int x, int y) {
        if (focusedWindow != null) {
            focusedWindow.onMouseDragged(x, y);
        }
    }

    /**
     * Handles a mouse release event for the focused subwindow.
     */
    public void onMouseReleased() {
        if (focusedWindow != null) {
            focusedWindow.onMouseReleased();
        }
    }

    /**
     * Opens a new TablesSubwindow when the Ctrl+T key combination is pressed.
     */
    public void onCtrlT() {
        openTablesSubWindow();
    }

    /**
     * Opens a new FormSubwindow when the Ctrl+F key combination is pressed and a given table is selected.
     */
    public void onCtrlF() {
        if(focusedWindow != null) {
            focusedWindow.onControlF();
        }
    }

    /**
     * In a FormSubwindow pressing PageUp makes the row change
     */
    public void onPageUp() {
        if(focusedWindow != null) {
            focusedWindow.onPageUp();
        }
    }

    /**
     * In a FormSubwindow pressing PageDown makes the row change
     */
    public void onPageDown() {
        if(focusedWindow != null) {
            focusedWindow.onPageDown();
        }
    }

    /**
     * In a FormSubwindow pressing Control+D deletes a row
     */
    public void onControlD() {
        if (focusedWindow != null) {
            focusedWindow.onControlD();
        }
    }

    /**
     * In a FormSubwindow pressing Control+N creates a new row
     */
    public void onCtrlN() {
        if (focusedWindow != null) {
            focusedWindow.onControlN();
        }
    }

    /**
     * Renames a table by its ID.
     *
     * @param tableId The ID of the table to rename.
     * @param newName The new name for the table.
     */
    public void renameTable(int tableId,String newName) {
        mediator.renameTable(tableId, newName);
    }

    /**
     * Creates a new table and adds it to the list of tables in tableController.
     */
    public void createTable() {
        mediator.createTable();
    }

    /**
     * Gets a table by its ID.
     *
     * @param tableId The ID of the table.
     * @return A deep clone of the table object.
     */
    public Table getTable(int tableId) {
        return mediator.getTable(tableId);
    }

    /**
     * Deletes a table by its ID.
     *
     * @param tableId The ID of the table to delete.
     */
    public void deleteTable(int tableId) {
        mediator.deleteTable(tableId);
    }

    /**
     * Deletes a column from a specified table.
     *
     * @param tableId The ID of the table.
     * @param columnId The ID of the column to delete.
     */
    public void deleteColumn(int tableId, int columnId) {
        mediator.deleteColumn(tableId, columnId);
    }

    /**
     * Retrieve name of table
     * @param tableId | ID of table
     * @return name of table
     */
    public String getTableName(int tableId) {
        return mediator.getTableName(tableId);
    }

    /**
     * Gets a deep clone of the list of tables.
     *
     * @return A deep clone of the list of tables.
     */
    public List<Table> getTables() {
        return mediator.getTables();
    }

    /**
     * Retrieves the width of the canvas.
     * @return width of canvas
     */
    public int getCanvasWidth() {
        return mediator.getCanvasWidth();
    }

    /**
     * Retrieves the height of the canvas.
     * @return height of canvas
     */
    public int getCanvasHeight() {
        return mediator.getCanvasHeight();
    }

    /**
     * Checks if a table name is valid.
     *
     * @param tableName The name of the table.
     * @return True if the table name is valid, false otherwise.
     */
    public boolean isValidTableName(String tableName) {
        return mediator.isValidTableName(tableName);
    }

    /**
     * Checks if a column name is valid.
     *
     * @param tableId The ID of the table.
     * @param columnName The name of the column.
     * @return True if the column name is valid, false otherwise.
     */
    public boolean isValidColumnName(int tableId, String columnName) {
        return mediator.isValidColumnName(tableId, columnName);
    }

    /**
     * Checks if a column value is valid.
     *
     * @param tableId The ID of the table.
     * @param columnId The ID of the column.
     * @param edit The value to validate.
     * @return True if the column value is valid, false otherwise.
     */
    public boolean isValidColumnValue(int tableId, int columnId, Object edit){
        return mediator.isValidColumnValue(tableId, columnId, edit);
    }

    /**
     * Removes a row from a specified table.
     *
     * @param tableId The ID of the table.
     * @param rowIndex The index of the row to remove.
     */
    public void removeRowFromTable(int tableId, int rowIndex) {
        mediator.removeRowFromTable(tableId, rowIndex);
    }

    /**
     * Adds a row to a specified table.
     *
     * @param tableId The ID of the table.
     */
    public void addRowToTable(int tableId){
        mediator.addRowToTable(tableId);
    }

    /**
     * Retrieve given column of given table
     * @param tableId | ID of table
     * @param columnId | ID of column
     * @return Column
     */
    public Column<?> getColumn(int tableId, int columnId){
        return mediator.getColumn(tableId, columnId);
    }

    /**
     * Sets the value of a specific cell in a table.
     *
     * @param tableId The ID of the table.
     * @param columnId The ID of the column.
     * @param rowId The index of the row.
     * @param value The value to set.
     */
    public void setRowValue(int tableId, int columnId, int rowId, Object value) {
        mediator.setRowValue(tableId, columnId, rowId, value);
    }

    /**
     * Toggles the default value of a column in a table.
     *
     * @param tableId The ID of the table.
     * @param columnId The ID of the column.
     */
    public void toggleDefaultValue(int tableId, int columnId) {
        mediator.toggleDefaultValue(tableId, columnId);
    }

    /**
     * Checks if a column type conversion is valid.
     *
     * @param tableId The ID of the table.
     * @param columnId The ID of the column.
     * @param next The new column type.
     * @return True if the type conversion is valid, false otherwise.
     */
    public boolean isValidColumnTypeConversion(int tableId, int columnId, ColumnType next) {
        return mediator.isValidColumnTypeConversion(tableId, columnId, next);
    }

    /**
     * Cycles the column type for a specific column in a table.
     *
     * @param tableId The ID of the table.
     * @param columnId The ID of the column.
     * @param next The new column type.
     */
    public void cycleColumnType(int tableId, int columnId, ColumnType next) {
        mediator.cycleColumnType(tableId, columnId, next);
    }

    /**
     * Attempts to toggle the "allows blank" setting for a column in a table.
     *
     * @param tableId The ID of the table.
     * @param columnId The ID of the column.
     * @param nextBlank The new "allows blank" setting.
     * @return True if the operation was successful, false otherwise.
     */
    public boolean tryToggleAllowsBlank(int tableId, int columnId, boolean nextBlank) {
        return mediator.tryToggleAllowsBlank(tableId, columnId, nextBlank);
    }

    /**
     * Attempts to change the default value of a column in a table.
     *
     * @param tableId The ID of the table.
     * @param columnId The ID of the column.
     * @param next The new default value.
     * @return True if the operation was successful, false otherwise.
     */
    public boolean tryChangeDefaultValue(int tableId, int columnId, String next) {
        return mediator.tryChangeDefaultValue(tableId, columnId, next);
    }

    /**
     * Gets the column ID at a specific row index in a table.
     *
     * @param tableId The ID of the table.
     * @param row The row index.
     * @return The column ID at the specified row index.
     */
    public int getColumnIdAt(int tableId, int row) {
        return mediator.getColumnIdAt(tableId, row);
    }

    /**
     * Renames a column in a table.
     *
     * @param tableId The ID of the table.
     * @param columnId The ID of the column to rename.
     * @param newName The new name for the column.
     */
    public void renameColumn(int tableId, int columnId, String newName) {
        mediator.renameColumn(tableId, columnId, newName);
    }

    /**
     * Adds a column to a specified table.
     *
     * @param tableId The ID of the table.
     */
    public void addColumnToTable(int tableId) {
        mediator.addColumnToTable(tableId);
    }

    /**
     * Retrieve amount of columns of a table
     * @param tableId | ID of table
     * @return amount of columns
     */
    public int getColumnCount(int tableId) {
        return mediator.getColumnCount(tableId);
    }

    /**
     * Retrieve list of row values of given row in given table.
     * @param tableId | ID of table
     * @param row | Index of row
     * @return List of row values
     */
    public List<Object> getTableRowValues(int tableId, int row) {
        return mediator.getTableRowValues(tableId, row);
    }

    /**
     * Retrieve list of column names of given table.
     * @param tableId | ID of table
     * @return List of column names
     */
    public List<String> getTableColumnNames(int tableId) {
        return mediator.getTableColumnNames(tableId);
    }

    /**
     * Retrieve list of column types of given table
     * @param tableId | ID of table
     * @return list of column types
     */
    public List<ColumnType> getColumnTypesOfTable(int tableId){
        return mediator.getColumnTypesOfTable(tableId);
    }

    /**
     * Retrieve list of column IDs of given table.
     * @param tableId | ID of table
     * @return List of column IDs
     */
    public List<Integer> getTableColumnIds(int tableId) {
        return mediator.getTableColumnIds(tableId);
    }

    /**
     * retrieve if given column of given table allows blanks.
     * @param tableId | ID of table
     * @param columnId | ID of column
     * @return True if allows blank, False otherwise
     */
    public boolean getAllowsBlank(int tableId, int columnId) {
        return mediator.getAllowsBlank(tableId, columnId);
    }
}