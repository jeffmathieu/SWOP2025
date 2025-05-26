package com.tablr.subwindow;

import com.tablr.area.Cell;
import com.tablr.area.NormalCell;
import com.tablr.area.TableArea;
import com.tablr.area.TableAreaGenerator;
import com.tablr.model.Table;

import java.util.List;

/**
 * Represents a subwindow for managing and displaying tables.
 * This class extends the Subwindow class and provides specific behavior for table-related operations.
 */
public class TablesSubwindow extends Subwindow {

    private boolean editing = false; // Indicates if a cell is currently being edited
    private boolean validState = true; // Indicates if the current edit state is valid
    private NormalCell currentEditCell = null; // The cell currently being edited

    /**
     * Constructs a TablesSubwindow instance.
     *
     * @param tableIds     The list of table IDs to display.
     * @param x            The x-coordinate of the subwindow.
     * @param y            The y-coordinate of the subwindow.
     * @param parentWindow The parent controller managing this subwindow.
     */
    public TablesSubwindow(List<Integer> tableIds, int x, int y, SubwindowController parentWindow) {
        super("Tables", x, y, 300, 300, parentWindow);
        this.tableArea = TableAreaGenerator.GenerateTableArea(parentWindow.getTables());
        updateTableAreaPositions();
    }

    /**
     * Updates the table area positions based on the current window size and position.
     */
    @Override
    public void updateTableArea() {
        TableArea oldArea = tableArea;
        this.tableArea = TableAreaGenerator.GenerateTableArea(parentWindow.getTables());
        updateTableAreaPositions();
        editing = false;
        validState = true;
        currentEditCell = null;
    }

    /**
     * Returns the ID of the table this subwindow is associated with.
     * If it is a tables subwindow, this will return -1.
     *
     * @return The appropriate table ID or -1 if this is a tables subwindow.
     */
    @Override
    public int getTableId() {
        return -1;
    }

    /**
     * Gets the cell from the TableArea using the x and y coordinates.
     *
     * @param x The x-coordinate of the click.
     * @param y The y-coordinate of the click.
     * @return The cell that contains the given x and y coordinates.
     */
    private NormalCell getNormalCellFromXY(int x, int y) {
        for (Cell[] column : tableArea.getTableCells()) {
            for (Cell cell : column) {
                if (cell.getRegion().contains(x, y)) {
                    switch (cell) {
                        case NormalCell normalCell:
                            return normalCell;
                        default:
                            throw new RuntimeException("Cell in TablesSubwindow is not a NormalCell. This should never happen.");
                    }
                }
            }
        }
        return null;
    }

    /**
     * Moves the subwindow and its contents when the mouse is dragged.
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
     * Handles a click on the body of the subwindow.
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
                NormalCell cell = getNormalCellFromXY(adjustedX, adjustedY);
                if (cell != null) {
                    startEditing(cell);
                }
            }

        } else if (!currentEditCell.getRegion().contains(adjustedX, adjustedY)) {
            if (!validState) {
                // Invalid name
                return;
            }
            setEdit();
            stopEditing();
        }
    }

    /**
     * Starts editing mode on the given cell.
     *
     * @param cell The cell to edit.
     */
    private void startEditing(NormalCell cell) {
        editing = true;
        cell.selectCell();
        currentEditCell = cell;
        currentEditCell.setEdit();
    }

    /**
     * Updates the edit state of the current cell.
     */
    private void onEdit() {
        validState = parentWindow.isValidTableName(currentEditCell.getEdit()) || currentEditCell.getValue().equals(currentEditCell.getEdit());
        currentEditCell.setValid(validState);
    }

    /**
     * Sets the edit value of the current cell and updates the table area.
     */
    private void setEdit() {
        if (validState) {
            if (!currentEditCell.getValue().equals(currentEditCell.getEdit())) {
                parentWindow.renameTable(tableArea.getIdFromY(currentEditCell.getRegion().y), currentEditCell.getEdit());
            }
            parentWindow.updateTableAreas(getTableId());
        }
    }

    /**
     * Stops editing mode and resets the edit of the cell being edited.
     */
    private void stopEditing() {
        if (currentEditCell != null) {
            currentEditCell.selectCell();
            currentEditCell.resetEdit();
        }
        editing = false;
        currentEditCell = null;
    }

    /**
     * Handles a double click on the subwindow.
     *
     * @param x The x-coordinate of the click.
     * @param y The y-coordinate of the click.
     */
    @Override
    public void onDoubleClick(int x, int y) {
        x += scrollX;
        y += scrollY;

        if (validState) {
            if (y > tableArea.getLowestY()) {
                parentWindow.createTable();
                parentWindow.updateTableAreas(this.getTableId());
            } else {
                for (Cell[] column : tableArea.getTableCells()) {
                    for (Cell cell : column) {
                        if (cell.getRegion().contains(x, y)) {
                            int id = tableArea.getIdFromY(y);
                            Table table = parentWindow.getMediator().getTable(id);
                            if (parentWindow.getColumnCount(id) > 0) {
                                parentWindow.addSubWindow(new RowsSubwindow(table.getId(), 0, 0, parentWindow));
                            } else {
                                parentWindow.addSubWindow(new DesignSubwindow(table.getId(), 0, 0, parentWindow));
                            }
                            return;
                        }
                    }
                }
            }
        }
    }

    /**
     * Handles the Control+Enter key event.
     */
    @Override
    public void onControlEnter() {
    }

    @Override
    public void onControlF() {
        if (!editing && tableArea.getSelectedRow() != -1) {
            parentWindow.addSubWindow(new FormSubwindow(tableArea.getIdFromSelectedRowIndex(),30,30,0,parentWindow));
            //parentWindow.updateTableAreas(this.getTableId());
        }
    }

    /**
     * Handles the Enter key event.
     */
    @Override
    public void onEnter() {
        if (editing && validState) {
            setEdit();
            stopEditing();
        }
    }

    /**
     * Handles the Escape key event.
     */
    @Override
    public void onEscape() {
        if (editing) {
            stopEditing();
        }
    }

    /**
     * Handles the Backspace key event.
     */
    @Override
    public void onBackspace() {
        if (editing) {
            currentEditCell.removeCharEdit();
            onEdit();
        }
    }

    /**
     * Handles the Delete key event.
     */
    @Override
    public void onDelete() {
        if (!editing && tableArea.getSelectedRow() != -1) {
            parentWindow.deleteTable(tableArea.getIdFromSelectedRowIndex());
            parentWindow.updateTableAreas(this.getTableId());
        }
    }

    /**
     * Handles character input.
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
}
