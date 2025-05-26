package com.tablr.undoRedo;

import com.tablr.controller.TableController;
import com.tablr.model.Column;
import com.tablr.model.Table;

import java.util.HashMap;
import java.util.Map;

/**
 * Command that deletes a row in a table.
 */
public class DeleteRowCommand implements Command {
    private final TableController tableController;
    private final int tableId;
    private final int rowIndex;
    private final Map<Integer, Object> backupValues = new HashMap<>();

    /**
     * Constructs new DeleteRowCommand.
     *
     * @param tableController | controller that manages tables.
     * @param tableId | ID of table.
     * @param rowIndex | Row that needs to be removed.
     */
    public DeleteRowCommand(TableController tableController, int tableId, int rowIndex) {
        this.tableController = tableController;
        this.tableId = tableId;
        this.rowIndex = rowIndex;
    }

    @Override
    public void execute() {
        Table table = tableController.getTable(tableId);
        for (Column<?> col : table.getColumns()) {
            backupValues.put(col.getId(), col.getValue(rowIndex));
        }
        tableController.removeRowFromTable(tableId, rowIndex);
    }

    @Override
    public void undo() {
        tableController.insertRowAt(tableId, rowIndex);

        Table table = tableController.getTable(tableId);
        for (Column<?> col : table.getColumns()) {
            Object value = backupValues.get(col.getId());

            // "unsafe" but safe because of copy of safe column!
            @SuppressWarnings("unchecked")
            Column<Object> typedCol = (Column<Object>) col;

            typedCol.setValue(rowIndex, value);
        }
    }

}