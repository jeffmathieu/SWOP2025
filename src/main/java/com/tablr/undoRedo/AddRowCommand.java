package com.tablr.undoRedo;

import com.tablr.controller.TableController;
import com.tablr.model.Table;

/**
 * Command that adds row to a table.
 */
public class AddRowCommand implements Command {
    private final TableController tableController;
    private final int tableId;
    private int addedRowIndex;

    /**
     * Constructs new AddRowCommand.
     *
     * @param tableController | controller that manages tables.
     * @param tableId | ID of table.
     */
    public AddRowCommand(TableController tableController, int tableId) {
        this.tableController = tableController;
        this.tableId = tableId;
    }

    @Override
    public void execute() {
        Table table = tableController.getTable(tableId);
        addedRowIndex = table.getRowCount();
        tableController.addRowToTable(tableId);
    }

    @Override
    public void undo() {
        tableController.removeRowFromTable(tableId, addedRowIndex);
    }
}