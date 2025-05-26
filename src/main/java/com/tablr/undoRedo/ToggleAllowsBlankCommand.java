package com.tablr.undoRedo;

import com.tablr.controller.TableController;
import com.tablr.model.Column;

/**
 * Command that toggles allowsBlank attribute of column.
 */
public class ToggleAllowsBlankCommand implements Command {
    private final TableController tableController;
    private final int tableId;
    private final int columnId;
    private final boolean oldAllowsBlank;
    private final boolean newAllowsBlank;

    /**
     * Constructs new ToggleAllowsBlankCommand.
     *
     * @param tableController | Controller that manages tables.
     * @param tableId | ID of table.
     * @param columnId | ID of Column.
     * @param newAllowsBlank | New allowsBlank value.
     */
    public ToggleAllowsBlankCommand(TableController tableController, int tableId, int columnId, boolean newAllowsBlank) {
        this.tableController = tableController;
        this.tableId = tableId;
        this.columnId = columnId;

        Column<?> col = tableController.getColumn(tableId, columnId);
        this.oldAllowsBlank = !newAllowsBlank;
        this.newAllowsBlank = newAllowsBlank;
    }

    @Override
    public void execute() {
        tableController.tryToggleAllowsBlank(tableId, columnId, newAllowsBlank);
    }

    @Override
    public void undo() {
        tableController.tryToggleAllowsBlank(tableId, columnId, oldAllowsBlank);
    }

}