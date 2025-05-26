package com.tablr.undoRedo;

import com.tablr.controller.TableController;
import com.tablr.model.Table;

import java.util.List;

/**
 * Command that deletes a table.
 */
public class DeleteTableCommand implements Command {
    private final TableController tableController;
    private final int tableId;
    private Table backupTable;
    private int originalIndex;

    /**
     * Constructs new DeleteTableCommand.
     *
     * @param tableController | controller that manages tables.
     * @param tableId | ID of table to be removed.
     */
    public DeleteTableCommand(TableController tableController, int tableId) {
        this.tableController = tableController;
        this.tableId = tableId;
    }

    /**
     * Retrieves the tableId of this deleted table.
     * @return table ID
     */
    public int getDeletedTableId() {
        return tableId;
    }

    @Override
    public void execute() {
        List<Table> tables = tableController.getTables();
        for (int i = 0; i < tables.size(); i++) {
            if (tables.get(i).getId() == tableId) {
                originalIndex = i;
                break;
            }
        }
        backupTable = tableController.cloneTable(tableId);
        tableController.deleteTable(tableId);
    }

    @Override
    public void undo() {
        if (backupTable != null) {
            tableController.insertTableAt(backupTable, originalIndex);
        }
    }

}