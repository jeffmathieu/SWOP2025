package com.tablr.undoRedo;

import com.tablr.controller.TableController;
import com.tablr.model.Column;

import java.util.List;

/**
 * Command that adds column to a table.
 */
public class AddColumnCommand implements Command {
    private final TableController tableController;
    private final int tableId;
    private Column<?> addedColumn;

    /**
     * Constructs new AddColumnCommand.
     *
     * @param tableController | controller that manages tables.
     * @param tableId | ID of table.
     */
    public AddColumnCommand(TableController tableController, int tableId) {
        this.tableController = tableController;
        this.tableId = tableId;
    }

    @Override
    public void execute() {
        tableController.addColumnToTable(tableId);
        List<Column<?>> columns = tableController.getColumns(tableId);
        addedColumn = columns.getLast();
    }

    @Override
    public void undo() {
        tableController.deleteColumn(tableId, addedColumn.getId());
    }
}