package com.tablr.subwindow;

import com.tablr.area.*;
import com.tablr.model.Column;
import com.tablr.model.IntegerColumn;

/**
 * Represents a subwindow for managing rows in a table.
 * Provides functionality for editing, adding, and removing rows.
 */
public class RowsSubwindow extends Subwindow {
    private final int tableId;
    private boolean editing = false;
    private boolean validState = true;
    private NormalCell currentEditCell = null;
    private BooleanCell currentBooleanCell = null;
    private Column<?> currentEditColumn = null;

    /**
     * Constructs a RowsSubwindow instance.
     *
     * @param tableId      The ID of the table associated with this subwindow.
     * @param x            The x-coordinate of the subwindow.
     * @param y            The y-coordinate of the subwindow.
     * @param parentWindow The parent subwindow controller.
     */
    public RowsSubwindow(int tableId, int x, int y, SubwindowController parentWindow) {
        super("Rows: " + parentWindow.getTableName(tableId), x, y, 500, 300, parentWindow);
        this.tableId = tableId;
        //this.parentWindow = parentWindow;
        this.tableArea = TableAreaGenerator.GenerateRowsArea(parentWindow.getTable(tableId));
        updateTableAreaPositions();
    }

    /**
     * Updates the table area by regenerating it and resetting editing states.
     */
    @Override
    public void updateTableArea() {
        this.tableArea = TableAreaGenerator.GenerateRowsArea(parentWindow.getTable(tableId));
        this.title = "Rows: " + parentWindow.getTableName(tableId);
        updateTableAreaPositions();
        editing = false;
        validState = true;
        currentEditCell = null;
        currentBooleanCell = null;
        currentEditColumn = null;
    }

    /**
     * Gets the ID of the table associated with this subwindow.
     *
     * @return The table ID.
     */
    @Override
    public int getTableId() {
        return tableId;
    }

    /**
     * Handles mouse drag events and updates table area positions.
     *
     * @param mx The x-coordinate of the mouse.
     * @param my The y-coordinate of the mouse.
     */
    @Override
    public void onMouseDragged(int mx, int my) {
        super.onMouseDragged(mx, my);
        updateTableAreaPositions();
    }

    /**
     * Handles mouse click events within the subwindow body.
     *
     * @param x The x-coordinate of the click.
     * @param y The y-coordinate of the click.
     */
    @Override
    public void onBodyClick(int x, int y) {
        int adjustedX = x + scrollX;
        int adjustedY = y + scrollY;

        if (!editing) {
            if (tableArea.leftMarginClicked(adjustedX, adjustedY)) {
                tableArea.selectRow(adjustedX, adjustedY);
            } else {
                for (Cell[] column : tableArea.getTableCells()) {
                    for (Cell cell : column) {
                        if (cell.getRegion().contains(adjustedX, adjustedY)) {
                            if (currentEditCell != null) {
                                throw new RuntimeException("cell in tablessubwindow is not a normalcell, this should never happen");
                            }
                            switch (cell) {
                                case NormalCell c -> startEditing(c);
                                case BooleanCell b -> {
                                    if (b.isCheckBoxClicked(adjustedX, adjustedY)) {
                                        currentBooleanCell = b;
                                        startBooleanEdit();
                                    }
                                }
                                default -> throw new IllegalStateException("Unexpected value: " + cell);
                            }
                        }
                    }
                }
            }
        } else if (currentBooleanCell != null && currentBooleanCell.isCheckBoxClicked(adjustedX, adjustedY)) {
            startBooleanEdit();
        } else if (currentEditCell != null && !currentEditCell.getRegion().contains(adjustedX, adjustedY)) {
            if (!validState) {
                return;
            }
            setEdit();
            stopEditing();
        }
    }

    /**
     * Starts editing a Boolean cell.
     */
    private void startBooleanEdit() {
        currentEditColumn = parentWindow.getColumn(tableId, tableArea.getIdFromX(currentBooleanCell.getRegion().x));
        Boolean next = booleanSwitch(currentBooleanCell.isCellSelected());
        validState = validateBoolean(next, tableArea.getIdFromX(currentBooleanCell.getRegion().x));
        editing = true;
        if (validState) {
            parentWindow.setRowValue(tableId, tableArea.getIdFromX(currentBooleanCell.getRegion().x), tableArea.getRowfromY(currentBooleanCell.getRegion().y), next);
            parentWindow.updateTableAreas(tableId);
        } else {
            currentBooleanCell.setSelectValue(next);
        }
    }

    /**
     * Validates a Boolean value for a specific column.
     *
     * @param b        The Boolean value to validate.
     * @param columnId The ID of the column.
     * @return True if the value is valid, false otherwise.
     */
    private boolean validateBoolean(Boolean b, int columnId) {
        return parentWindow.isValidColumnValue(tableId, columnId, b);
    }

    /**
     * Toggles the Boolean value, considering whether blank values are allowed.
     *
     * @param b The current Boolean value.
     * @return The toggled Boolean value.
     */
    private Boolean booleanSwitch(Boolean b) {
        if (currentEditColumn.allowsBlank()) {
            if (b == null) return true;
            if (b) return false;
            return null;
        } else {
            return !b;
        }
    }

    /**
     * Starts editing a normal cell.
     *
     * @param cell The NormalCell to edit.
     */
    private void startEditing(NormalCell cell) {
        editing = true;
        cell.selectCell();
        currentEditCell = cell;
        currentEditCell.setEdit();
        currentEditColumn = parentWindow.getColumn(tableId, tableArea.getIdFromX(currentEditCell.getRegion().x));
    }

    /**
     * Handles editing logic for the current cell.
     */
    private void onEdit() {
        switch (currentEditColumn) {
            case IntegerColumn c -> {
                if (isIntString(currentEditCell.getEdit())) {
                    validState = parentWindow.isValidColumnValue(tableId, tableArea.getIdFromX(currentEditCell.getRegion().x), myStringToInt(currentEditCell.getEdit()))
                            || currentEditCell.getValue().equals(currentEditCell.getEdit());
                    currentEditCell.setValid(validState);
                } else {
                    currentEditCell.setValid(false);
                }
            }
            default -> {
                validState = parentWindow.isValidColumnValue(tableId, tableArea.getIdFromX(currentEditCell.getRegion().x), currentEditCell.getEdit())
                        || currentEditCell.getValue().equals(currentEditCell.getEdit());
                currentEditCell.setValid(validState);
            }
        }
    }

    /**
     * Sets the edited value for the current cell.
     */
    private void setEdit() {
        if (validState) {
            switch (currentEditColumn) {
                case IntegerColumn c ->
                        parentWindow.setRowValue(tableId, tableArea.getIdFromX(currentEditCell.getRegion().x), tableArea.getRowfromY(currentEditCell.getRegion().y), myStringToInt(currentEditCell.getEdit()));
                default ->
                        parentWindow.setRowValue(tableId, tableArea.getIdFromX(currentEditCell.getRegion().x), tableArea.getRowfromY(currentEditCell.getRegion().y), currentEditCell.getEdit());
            }
            //parentWindow.updateTableAreas(getTableId());
        }
    }

    /**
     * Stops editing the current cell and resets editing states.
     */
    private void stopEditing() {
        currentEditCell.selectCell();
        currentEditCell.resetEdit();
        editing = false;
        currentEditCell = null;
        validState = true;
        currentEditColumn = null;
        parentWindow.updateTableAreas(tableId);
    }

    /**
     * Handles double-click events within the subwindow body.
     *
     * @param x The x-coordinate of the double click.
     * @param y The y-coordinate of the double click.
     */
    @Override
    public void onDoubleClick(int x, int y) {
        y += scrollY;

        if (!editing) {
            if (y > tableArea.getLowestY()) {
                parentWindow.addRowToTable(tableId);
                parentWindow.updateTableAreas(getTableId());
            }
        }
    }

    /**
     * Opens the design subwindow when Control+Enter is pressed.
     */
    @Override
    public void onControlEnter() {
        parentWindow.addSubWindow(new DesignSubwindow(tableId, 0, 0, parentWindow));
    }

    @Override
    public void onControlF() {

    }

    /**
     * Confirms editing when Enter is pressed.
     */
    @Override
    public void onEnter() {
        if (editing && validState && currentEditCell != null) {
            setEdit();
            stopEditing();
        }
    }

    /**
     * Cancels editing when Escape is pressed.
     */
    @Override
    public void onEscape() {
        if (editing && currentEditCell != null) {
            stopEditing();
        }
    }

    /**
     * Handles backspace key events during editing.
     */
    @Override
    public void onBackspace() {
        if (editing && currentEditCell != null) {
            currentEditCell.removeCharEdit();
            onEdit();
        }
    }

    /**
     * Handles delete key events to remove a selected row.
     */
    @Override
    public void onDelete() {
        if (!editing && tableArea.getSelectedRow() != -1) {
            parentWindow.removeRowFromTable(tableId, tableArea.getSelectedRow());
            parentWindow.updateTableAreas(tableId);
        }
    }

    /**
     * Handles character input during editing.
     *
     * @param keyChar The character input.
     */
    @Override
    public void onCharacter(char keyChar) {
        if (editing && currentEditCell != null) {
            currentEditCell.appendEdit(keyChar);
            onEdit();
        }
    }
}