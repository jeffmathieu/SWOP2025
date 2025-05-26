package com.tablr.undoRedo;

import com.tablr.controller.TableController;

/**
 * Command to set a value to a cell in a table.
 */
public class SetCellValueCommand implements Command {
    private final TableController tableController;
    private final int tableId;
    private final int columnId;
    private final int rowIndex;
    private final Object oldValue;
    private final Object newValue;

    /**
     * Constructs new SetCellValueCommand.
     *
     * @param tableController | Controller that manages tables.
     * @param tableId | ID of table.
     * @param columnId | ID of column.
     * @param rowIndex | ID of row.
     * @param oldValue | Old value of cell.
     * @param newValue | New value of cell.
     */
    public SetCellValueCommand(
            TableController tableController,
            int tableId,
            int columnId,
            int rowIndex,
            Object oldValue,
            Object newValue
    ) {
        this.tableController = tableController;
        this.tableId = tableId;
        this.columnId = columnId;
        this.rowIndex = rowIndex;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    @Override
    public void execute() {
        tableController.setRowValue(tableId, columnId, rowIndex, newValue);
    }

    @Override
    public void undo() {
        tableController.setRowValue(tableId, columnId, rowIndex, oldValue);
    }

}