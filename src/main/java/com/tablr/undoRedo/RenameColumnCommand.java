package com.tablr.undoRedo;

import com.tablr.controller.TableController;

/**
 * Command that renames a column.
 */
public class RenameColumnCommand implements Command {
    private final TableController tableController;
    private final int tableId;
    private final int columnId;
    private final String oldName;
    private final String newName;

    /**
     * Constructs new RenameColumnCommand.
     *
     * @param tableController | controller that manages tables.
     * @param tableId | ID of table.
     * @param columnId | ID of column to be renamed.
     * @param oldName | Old name of column.
     * @param newName | New name of column.
     */
    public RenameColumnCommand(TableController tableController, int tableId, int columnId, String oldName, String newName) {
        this.tableController = tableController;
        this.tableId = tableId;
        this.columnId = columnId;
        this.oldName = oldName;
        this.newName = newName;
    }

    @Override
    public void execute() {
        tableController.renameColumn(tableId, columnId, newName);
    }

    @Override
    public void undo() {
        tableController.renameColumn(tableId, columnId, oldName);
    }

}