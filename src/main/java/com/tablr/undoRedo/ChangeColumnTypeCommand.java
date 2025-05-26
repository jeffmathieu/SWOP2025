package com.tablr.undoRedo;

import com.tablr.controller.TableController;
import com.tablr.model.Column;
import com.tablr.model.ColumnFactory;
import com.tablr.model.ColumnType;

/**
 * Command that changes type of column.
 */
public class ChangeColumnTypeCommand implements Command {
    private final TableController tableController;
    private final int tableId;
    private final int columnId;
    private final ColumnType oldType;
    private final ColumnType newType;
    private Column<?> oldColumn;
    private Column<?> newColumn;

    /**
     * Constructs new ChangeColumnTypeCommand.
     *
     * @param tableController | controller that manages tables.
     * @param tableId | ID of table.
     */
    public ChangeColumnTypeCommand(TableController tableController, int tableId, int columnId, ColumnType newType) {
        this.tableController = tableController;
        this.tableId = tableId;
        this.columnId = columnId;
        this.newType = newType;

        this.oldColumn = tableController.getColumn(tableId, columnId);
        this.oldType = oldColumn.getColumnType();

        this.newColumn = ColumnFactory.createColumn(newType, oldColumn);

        for (int r = 0; r < tableController.getRowCount(tableId); r++) {
            newColumn.addDefaultValue();
        }
        for (int r = 0; r < tableController.getRowCount(tableId); r++) {
            Object val = oldColumn.getValue(r);
            newColumn.setValueFromString(r, val != null ? val.toString() : "");
        }
    }

    @Override
    public void execute() {
        tableController.replaceColumn(tableId, columnId, newColumn);
    }

    @Override
    public void undo() {
        tableController.replaceColumn(tableId, columnId, oldColumn);
    }
}