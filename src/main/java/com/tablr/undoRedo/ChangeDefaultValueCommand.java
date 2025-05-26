package com.tablr.undoRedo;

import com.tablr.controller.TableController;
import com.tablr.model.Column;

/**
 * Command that changes default value of Column.
 */
public class ChangeDefaultValueCommand implements Command {
    private final TableController tableController;
    private final int tableId;
    private final int columnId;
    private final String oldValue;
    private final String newValue;
    private final boolean oldAllowsBlank;

    /**
     * Constructs new ChangeDefaultValueCommand.
     *
     * @param tableController | controller that manages tables.
     * @param tableId | ID of table.
     */
    public ChangeDefaultValueCommand(TableController tableController, int tableId, int columnId, String oldValue, String newValue) {
        this.tableController = tableController;
        this.tableId = tableId;
        this.columnId = columnId;
        this.oldValue = oldValue;
        this.newValue = newValue;

        Column<?> col = tableController.getColumn(tableId, columnId);
        this.oldAllowsBlank = col.allowsBlank();
    }

    @Override
    public void execute() {Column<?> col = tableController.getColumn(tableId, columnId);
        try {
            if ((newValue == null || newValue.isBlank())) {
                col.setAllowsBlank(true);
            }

            String toApply = newValue == null || newValue.isBlank() ? null : newValue;
            tableController.changeDefaultValue(tableId, columnId, toApply);
        } finally {
            col.setAllowsBlank(oldAllowsBlank);
        }
    }

    @Override
    public void undo() {
        Column<?> col = tableController.getColumn(tableId, columnId);

        try {
            if ((oldValue == null || oldValue.isBlank()) && !col.allowsBlank()) {
                col.setAllowsBlank(true);
            }

            String toApply = oldValue == null || oldValue.isBlank() ? null : oldValue;
            tableController.changeDefaultValue(tableId, columnId, toApply);
        } finally {
            col.setAllowsBlank(oldAllowsBlank);
        }
    }
}