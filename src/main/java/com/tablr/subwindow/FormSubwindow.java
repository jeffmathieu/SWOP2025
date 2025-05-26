package com.tablr.subwindow;

import com.tablr.area.BooleanCell;
import com.tablr.area.Cell;
import com.tablr.area.NormalCell;
import com.tablr.area.TableAreaGenerator;
import com.tablr.model.ColumnType;
import com.tablr.model.IntegerColumn;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Represents a subwindow for editing a specific row in a table.
 * Provides functionality for rendering, editing, and navigating table rows.
 */
public class FormSubwindow extends Subwindow {

    private final int tableId; // The ID of the table being edited
    private int row; // The current row being edited
    private boolean editing = false; // Indicates if a cell is currently being edited
    private boolean validState = true; // Indicates if the current edit is valid
    private NormalCell currentEditCell = null; // The cell currently being edited
    private Rectangle currentEditRectangle = null; // The region of the cell being edited
    private int currentColumnIndex = -1;
    /**
     * Constructs a FormSubwindow for a specific table and row.
     *
     * @param tableId       The ID of the table.
     * @param x             The x-coordinate of the subwindow.
     * @param y             The y-coordinate of the subwindow.
     * @param row           The row being edited.
     * @param parentWindow  The parent subwindow controller.
     */
    public FormSubwindow(int tableId, int x, int y, int row, SubwindowController parentWindow) {
        super("Form: " + parentWindow.getTableName(tableId) + " Row: " + row,
                x, y, 400, 400, parentWindow);
        this.tableId = tableId;
        this.row = row;
        initTableArea();
    }

    /**
     * Initializes the table area by updating the form.
     */
    private void initTableArea() {
        try {
            updateForm();
        } catch (Exception e) {
            System.err.println("FormSubwindow – kon TableArea niet opbouwen: " + e.getMessage());
        }
    }

    /**
     * Updates the form by generating a new table area and setting its position.
     */
    private void updateForm() {
        List<Object> rowValues = parentWindow.getTableRowValues(tableId, row);
        List<String> columnNames = parentWindow.getTableColumnNames(tableId);
        List<ColumnType> columnTypes = parentWindow.getColumnTypesOfTable(tableId);
        List<Integer> columnIds = parentWindow.getTableColumnIds(tableId);

        this.tableArea = TableAreaGenerator.GenerateFormArea(columnNames, rowValues, columnTypes, columnIds);
        updateTableAreaPositions();
        this.title = "Form: " + parentWindow.getTableName(tableId) + " Row: " + row;
    }

    /**
     * Updates the table area. Overrides the parent method.
     */
    @Override
    public void updateTableArea() {
        updateForm();
    }

    @Override
    public int getTableId() {
        return tableId;
    }

    @Override
    public void onControlEnter() {
        // No operation
    }

    @Override
    public void onControlF() {
        // No operation
    }

    @Override
    public void onDelete() {
        // Not used
    }
    @Override
    public void onDoubleClick(int x, int y) {
        // No operation
    }

    /**
     * Handles mouse drag events and updates the table area positions.
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
     * Handles the Page Up key event to navigate to the previous row.
     */
    @Override
    public void onPageUp() {
        if(!editing && validState && row >0) {
            row--;
            updateForm();
        }
    }

    /**
     * Handles the Page Down key event to navigate to the next row.
     */
    @Override
    public void onPageDown() {
        if(!editing && validState) {
            row++;
            updateForm();
        }
    }

    /**
     * Handles the Control+D key event to delete the current row.
     */
    @Override
    public void onControlD() {
        parentWindow.removeRowFromTable(tableId, row);
        parentWindow.updateTableAreas(tableId);
    }

    /**
     * Handles the Control+N key event to add a new row to the table.
     */
    @Override
    public void onControlN() {
        parentWindow.addRowToTable(tableId);
        parentWindow.updateTableAreas(tableId);
    }

    /**
     * Handles character input for editing a cell.
     *
     * @param keyChar The character input.
     */
    @Override
    public void onCharacter(char keyChar) {
        if (editing) {
            currentEditCell.appendEdit(keyChar);
            onEdit();
        }
    }

    /**
     * Handles the Backspace key event for editing a cell.
     */
    @Override
    public void onBackspace() {
        if (editing) {
            currentEditCell.removeCharEdit();
            onEdit();
        }
    }

    /**
     * Handles the Enter key event to commit and stop editing.
     */
    @Override
    public void onEnter() {
        if (editing && validState) {
            commitEdit();
            stopEditing();
        }
    }

    /**
     * Handles the Escape key event to stop editing.
     */
    @Override
    public void onEscape() {
        if (editing) stopEditing();
    }

    /**
     * Handles a mouse click on the body of the table area.
     *
     * @param x The x-coordinate of the click.
     * @param y The y-coordinate of the click.
     */
    @Override
    public void onBodyClick(int x, int y) {
        Cell[] rowValues = this.tableArea.getTableCells()[1];

        if (!editing) {
            if (Arrays.stream(rowValues).noneMatch(Objects::isNull)) {
                for (int i =0;i<rowValues.length;i++) {
                    Cell cell = rowValues[i];
                    if (!cell.getRegion().contains(x, y)) continue;

                    switch (cell) {
                        case BooleanCell b -> {
                            if (b.isCheckBoxClicked(x, y)) {
                                currentColumnIndex = i;
                                startBooleanEdit(b.isCellSelected());
                            }
                        }
                        case NormalCell c -> {
                            if (!editing) {
                                editing = true;
                                validState = true;
                                currentEditCell = c;
                                currentEditRectangle = c.getRegion();

                                c.selectCell();
                                c.setEdit();
                                currentColumnIndex = i;

                            } else if (currentEditRectangle != null && !currentEditRectangle.contains(x, y)) {
                                if (validState) {
                                    commitEdit();
                                    stopEditing();
                                }
                            }
                        }
                        default -> throw new IllegalStateException("FormSubwindow – onbekend celtype: " + cell);
                    }
                }
            }
        } else if (validState) {
            commitEdit();
            stopEditing();
        }
    }

    /**
     * Starts editing a boolean cell.
     *
     * @param currentValue The current value of the cell.
     */
    private void startBooleanEdit(Boolean currentValue) {
        int columnId = tableArea.getId(currentColumnIndex);
        boolean allowsBlank = parentWindow.getAllowsBlank(tableId, columnId);

        Boolean next = booleanSwitch(currentValue, allowsBlank);
        boolean valid = parentWindow.isValidColumnValue(tableId, columnId, next);

        if (valid) {
            parentWindow.setRowValue(tableId, columnId, row, next);
            parentWindow.updateTableAreas(tableId);
        } else {
            System.err.println("FormSubwindow – boolean‑switch resulteert in ongeldige waarde");
        }
    }

    /**
     * Toggles a boolean value, considering if blank values are allowed.
     *
     * @param current     The current boolean value.
     * @param allowsBlank Whether blank values are allowed.
     * @return The toggled boolean value.
     */
    private static Boolean booleanSwitch(Boolean current, boolean allowsBlank) {
        if (allowsBlank) {
            if (current == null) return Boolean.TRUE;
            if (current) return Boolean.FALSE;
            return null;
        }
        return current == null ? Boolean.TRUE : !current;
    }

    /**
     * Handles the editing logic for a cell.
     */
    private void onEdit() {
        int columnId = tableArea.getId(currentColumnIndex);
        String newText = currentEditCell.getEdit();

        var columnObj = parentWindow.getColumn(tableId, columnId);

        switch (columnObj) {
            case IntegerColumn ignored -> {
                if (!isIntString(newText)) { // Early reject for invalid integer strings
                    validState = false;
                } else {
                    Integer candidate = newText.isBlank() ? null : myStringToInt(newText);
                    boolean sameAsOld = Objects.equals(newText, currentEditCell.getValue());
                    validState = parentWindow.isValidColumnValue(tableId, columnId, candidate) || sameAsOld;
                }
            }
            default -> { // Handles strings, email, etc.
                boolean sameAsOld = Objects.equals(newText, currentEditCell.getValue());
                validState = parentWindow.isValidColumnValue(tableId, columnId, newText) || sameAsOld;
            }
        }

        currentEditCell.setValid(validState);
    }

    /**
     * Commits the current edit to the mediator and updates the table area.
     */
    private void commitEdit() {
        int columnId = tableArea.getId(currentColumnIndex);
        String newText = currentEditCell.getEdit();

        var columnObj = parentWindow.getColumn(tableId, columnId);

        Object valueForModel;
        boolean success;

        if (columnObj instanceof IntegerColumn) {
            valueForModel = newText.isBlank() ? null : myStringToInt(newText);
            success = parentWindow.isValidColumnValue(tableId, columnId, valueForModel);
        } else {
            valueForModel = newText;
            success = parentWindow.isValidColumnValue(tableId, columnId, newText);
        }

        currentEditCell.setValid(success);
        validState = success;

        if (success) {
            parentWindow.setRowValue(tableId, columnId, row, valueForModel);
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
        currentColumnIndex = -1;
    }
}