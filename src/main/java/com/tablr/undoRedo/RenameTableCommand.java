package com.tablr.undoRedo;

import com.tablr.controller.TableController;

/**
 * Command that renames a table.
 */
public class RenameTableCommand implements Command {
    private final TableController tableController;
    private final int tableId;
    private final String oldName;
    private final String newName;

    /**
     * Constructs new RenameTableCommand.
     *
     * @param tableController | controller that manages tables.
     * @param tableId | ID of table.
     * @param oldName | Old name of table.
     * @param newName | New name of table.
     */
    public RenameTableCommand(TableController tableController, int tableId, String oldName, String newName) {
        this.tableController = tableController;
        this.tableId = tableId;
        this.oldName = oldName;
        this.newName = newName;
    }

    @Override
    public void execute() {
        tableController.renameTable(tableId, newName);
    }

    @Override
    public void undo() {
        tableController.renameTable(tableId, oldName);
    }
}