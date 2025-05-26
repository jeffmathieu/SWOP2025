package com.tablr.undoRedo;

import com.tablr.controller.TableController;
import com.tablr.model.Column;
import com.tablr.model.Table;
import java.util.List;

/**
 * Command that deletes a column.
 */
public class DeleteColumnCommand implements Command {
    private final TableController tableController;
    private final int tableId;
    private final int columnId;
    private Column<?> backupColumn;
    private int originalIndex;

    /**
     * Constructs new DeleteColumnCommand.
     *
     * @param tableController | controller that manages tables.
     * @param tableId | ID of table.
     * @param columnId | ID of column to be removed.
     */
    public DeleteColumnCommand(TableController tableController, int tableId, int columnId) {
        this.tableController = tableController;
        this.tableId = tableId;
        this.columnId = columnId;
    }

    @Override
    public void execute() {
        List<Column<?>> columns = tableController.getColumns(tableId);
        for (int i = 0; i < columns.size(); i++) {
            if (columns.get(i).getId() == columnId) {
                originalIndex = i;
                break;
            }
        }
        Table table = tableController.getTable(tableId);
        backupColumn = table.getColumn(columnId).clone();
        tableController.deleteColumn(tableId, columnId);
    }

    @Override
    public void undo() {
        tableController.insertColumnAt(tableId, backupColumn, originalIndex);
    }

}