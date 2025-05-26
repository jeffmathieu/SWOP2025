package com.tablr.subwindow;

import com.tablr.area.BooleanCell;
import com.tablr.area.TableAreaGenerator;
import com.tablr.model.ColumnType;
import com.tablr.area.Cell;
import com.tablr.area.NormalCell;

import java.awt.*;
import java.util.Objects;

/**
 * Represents a subwindow for designing a table structure.
 * Provides functionality for editing column names, types, and default values.
 */
public class DesignSubwindow extends Subwindow {

    private final int tableId; // The ID of the table being designed
    public final SubwindowController parentWindow; // The parent subwindow controller

    private boolean editing = false; // Indicates if a cell is currently being edited
    private boolean validState = true; // Indicates if the current edit is valid
    private NormalCell currentEditCell = null; // The cell currently being edited
    private Rectangle currentEditRectangle = null; // The region of the cell being edited
    private BooleanCell currentBooleanCell = null; // The boolean cell currently being edited

    /**
     * Checks if the currently edited cell is the name cell.
     *
     * @return True if the current cell is the name cell, false otherwise.
     */
    private boolean isEditingNameCell() {
        return 0 == tableArea.getColumnFromX(currentEditCell.getRegion().x);
    }

    /**
     * Constructs a DesignSubwindow for a specific table.
     *
     * @param tableId       The ID of the table.
     * @param x             The x-coordinate of the subwindow.
     * @param y             The y-coordinate of the subwindow.
     * @param parentWindow  The parent subwindow controller.
     */
    public DesignSubwindow(int tableId, int x, int y, SubwindowController parentWindow) {
        super("Design: " + parentWindow.getTableName(tableId), x, y, 440, 300, parentWindow);
        this.tableId = tableId;
        this.parentWindow = parentWindow;
        this.tableArea = TableAreaGenerator.GenerateDesignArea(parentWindow.getTable(tableId));
        updateTableAreaPositions();
    }

    /**
     * Updates the table area by regenerating it and resetting the editing state.
     */
    @Override
    public void updateTableArea() {
        this.tableArea = TableAreaGenerator.GenerateDesignArea(parentWindow.getTable(tableId));
        this.title = "Design: " + parentWindow.getTableName(tableId);
        this.validState = true;
        this.editing = false;
        this.currentEditCell = null;
        this.currentEditRectangle = null;
        this.currentBooleanCell = null;
        updateTableAreaPositions();
    }

    @Override
    public int getTableId() {
        return this.tableId;
    }

    /**
     * Handles a mouse click on the body of the subwindow.
     * Determines the clicked cell and initiates editing or selection as needed.
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
            } else if (tableArea.isTableAreaClicked(adjustedX, adjustedY)) {
                int row = tableArea.getRowfromY(adjustedY);
                int column = tableArea.getColumnFromX(adjustedX);
                Cell cell = tableArea.getTableCells()[column][row];
                currentEditRectangle = cell.getRegion();
                switch (cell) {
                    case NormalCell c -> {
                        currentEditCell = c;
                        if (column == 0) {
                            handleNameClick(row, adjustedX, adjustedY);
                        } else if (column == 1) {
                            handleTypeClick(row, adjustedY);
                        } else {
                            handleDefaultClick(row, adjustedX, adjustedY);
                        }
                    }
                    case BooleanCell b -> {
                        if (b.isCheckBoxClicked(adjustedX, adjustedY)) {
                            currentBooleanCell = b;
                            if (column == 3) {
                                handleDefaultClick(row, adjustedX, adjustedY);
                            } else {
                                handleBlanksClick(adjustedY);
                            }
                        }
                    }
                    default -> {
                        assert false : "Unsupported Cell subtype: " + cell.getClass();
                    }
                }
            }
        } else if (currentEditRectangle.contains(x, y)) {
            if (currentBooleanCell != null && currentBooleanCell.isCheckBoxClicked(adjustedX, adjustedY)) {
                handleBlanksClick(adjustedY);
            } else if (currentEditCell != null && !validState) {
                int row = tableArea.getRowfromY(adjustedY);
                handleTypeClick(row, adjustedY);
            }
        } else if (!currentEditRectangle.contains(adjustedX, adjustedY)) {
            if (validState) {
                int column = tableArea.getColumnFromX(currentEditRectangle.x);
                if (column == 0) {
                    setEdit();
                } else {
                    setDefaultEdit();
                }
                stopEditing();
            }
        }
    }

    /**
     * Handles a click on the default value cell.
     *
     * @param row The row index of the clicked cell.
     * @param x   The x-coordinate of the click.
     * @param y   The y-coordinate of the click.
     */
    private void handleDefaultClick(int row, int x, int y) {
        Cell defaultCell = tableArea.getTableCells()[3][row];
        int columnId = tableArea.getIdFromY(y);

        if (defaultCell.isToggleableDefaultCell()) {
            parentWindow.toggleDefaultValue(tableId, columnId);
            parentWindow.updateTableAreas(tableId);
            return;
        }

        if (!editing && defaultCell.isEditableDefaultCell()) {
            switch (defaultCell) {
                case NormalCell c -> {
                    editing = true;
                    validState = true;
                    currentEditCell = c;
                    c.selectCell();
                    c.setEdit();
                }
                default -> {
                    assert false : "Expected NormalCell in editable default cell, but got: " + defaultCell.getClass();
                }
            }
        } else if (editing && !currentEditCell.getRegion().contains(x, y)) {
            if (!validState) return;
            setDefaultEdit();
            stopEditing();
        }
    }

    /**
     * Handles a click on the name cell.
     *
     * @param row The row index of the clicked cell.
     * @param x   The x-coordinate of the click.
     * @param y   The y-coordinate of the click.
     */
    private void handleNameClick(int row, int x, int y) {
        Cell nameCell = tableArea.getTableCells()[0][row];
        if (!editing && nameCell instanceof NormalCell c) {
            editing = true;
            currentEditCell = c;
            c.selectCell();
            c.setEdit();
        } else if (editing && !currentEditCell.getRegion().contains(x, y)) {
            if (!validState) return;
            setEdit();
            stopEditing();
        }
    }

    /**
     * Handles a click on the type cell.
     *
     * @param row The row index of the clicked cell.
     * @param y   The y-coordinate of the click.
     */
    private void handleTypeClick(int row, int y) {
        int columnId = tableArea.getIdFromY(y);
        ColumnType next = ColumnType.valueOf(currentEditCell.getValue()).next();
        boolean isValid = parentWindow.isValidColumnTypeConversion(tableId, columnId, next);
        if (isValid && !Objects.equals(currentEditCell.getEdit(), next.toString())) {
            parentWindow.cycleColumnType(tableId, columnId, next);
            currentEditCell = null;
            validState = true;
            parentWindow.updateTableAreas(tableId);
            editing = false;
        } else if (isValid && Objects.equals(currentEditCell.getEdit(), currentEditCell.getValue())) {
            validState = true;
            currentEditCell.resetEdit();
            editing = false;
            currentEditCell = null;
        } else {
            editing = true;
            currentEditCell.setEdit();
            currentEditCell.setValue(next.toString());
            currentEditCell.setValid(false);
            validState = false;
        }
    }

    /**
     * Handles a click on the "allows blanks" checkbox.
     *
     * @param y The y-coordinate of the click.
     */
    private void handleBlanksClick(int y) {
        boolean nextBlank = !currentBooleanCell.isCellSelected();
        boolean success = parentWindow.tryToggleAllowsBlank(tableId, tableArea.getIdFromY(y), nextBlank);

        if (!success) {
            currentBooleanCell.setSelectValue(nextBlank);
            currentBooleanCell.setValid(false);
            this.editing = true;
            this.validState = false;
        } else {
            this.editing = false;
            this.validState = true;
            currentBooleanCell = null;
            currentEditRectangle = null;
            parentWindow.updateTableAreas(tableId);
        }
    }

    @Override
    public void onMouseDragged(int mx, int my) {
        super.onMouseDragged(mx, my);
        updateTableAreaPositions();
    }

    /**
     * Validates the current edit and updates the cell's state.
     */
    private void onEdit() {
        if(Objects.equals(tableArea.getTypeFromY(tableArea.getRowfromY(currentEditCell.getRegion().y)), "INTEGER")) {
            if (isIntString(currentEditCell.getEdit())) {
                validState = parentWindow.isValidColumnValue(tableId, tableArea.getIdFromY(currentEditCell.getRegion().y), myStringToInt(currentEditCell.getEdit()))
                        || currentEditCell.getValue().equals(currentEditCell.getEdit());
            } else {
                validState = false;
                currentEditCell.setValid(false);
            }
        }else if (isEditingNameCell()) {

            validState = parentWindow.isValidColumnName(tableId, currentEditCell.getEdit())
                    || currentEditCell.getValue().equals(currentEditCell.getEdit());
        } else {
            int columnId = tableArea.getIdFromY(currentEditCell.getRegion().y);
            validState = parentWindow.isValidColumnValue(tableId, columnId, currentEditCell.getEdit())
                    || currentEditCell.getValue().equals(currentEditCell.getEdit());
        }
        currentEditCell.setValid(validState);
    }

    /**
     * Commits the edit for the name cell.
     */
    private void setEdit() {
        if (validState && !currentEditCell.getValue().equals(currentEditCell.getEdit())) {
            int row = tableArea.getRowfromY(currentEditCell.getRegion().y);
            int columnId = parentWindow.getColumnIdAt(tableId, row);
            parentWindow.renameColumn(tableId, columnId, currentEditCell.getEdit());
            parentWindow.updateTableAreas(tableId);
        }
    }

    /**
     * Commits the edit for the default value cell.
     */
    private void setDefaultEdit() {
        int row = tableArea.getRowfromY(currentEditCell.getRegion().y);
        int columnId = parentWindow.getColumnIdAt(tableId, row);

        String newValue = currentEditCell.getEdit();
        boolean success = parentWindow.tryChangeDefaultValue(tableId, columnId, newValue);
        currentEditCell.setValid(success);

        if (success) {
            parentWindow.updateTableAreas(tableId);
        }
    }

    /**
     * Stops the current editing session and resets the editing state.
     */
    private void stopEditing() {
        if (currentEditCell != null) {
            currentEditCell.selectCell();
            currentEditCell.resetEdit();
        }

        editing = false;
        currentEditCell = null;
        currentEditRectangle = null;
    }

    @Override
    public void onDoubleClick(int x, int y) {
        y += scrollY;

        if (!editing && validState && y > tableArea.getLowestY()) {
            parentWindow.addColumnToTable(tableId);
            parentWindow.updateTableAreas(tableId);
        }
    }

    @Override
    public void onControlEnter() {
        if (parentWindow.getColumnCount(tableId) == 0) {
            System.err.println("no columns yet, can't open rows mode");
        } else {
            Subwindow newWindow = new RowsSubwindow(tableId, x + 40, y + 40, parentWindow);
            parentWindow.addSubWindow(newWindow);
        }
    }

    @Override
    public void onControlF() {

    }

    @Override
    public void onEnter() {
        if (editing && validState) {
            int row = tableArea.getRowfromY(currentEditCell.getRegion().y);

            if (tableArea.getTableCells()[0][row] == currentEditCell) {
                setEdit();
            } else if (tableArea.getTableCells()[3][row] == currentEditCell) {
                setDefaultEdit();
            }

            stopEditing();
        }
    }

    @Override
    public void onEscape() {
        if (editing) {
            stopEditing();
        }
    }

    @Override
    public void onBackspace() {
        if (editing) {
            currentEditCell.removeCharEdit();
            onEdit();
        }
    }

    @Override
    public void onDelete() {
        if (!editing && tableArea.getSelectedRow() != -1) {
            int columnId = tableArea.getIdFromSelectedRowIndex();
            parentWindow.deleteColumn(tableId, columnId);
            parentWindow.updateTableAreas(tableId);
        }
    }

    @Override
    public void onCharacter(char keyChar) {
        if (editing) {
            currentEditCell.appendEdit(keyChar);
            onEdit();
        }
    }
}