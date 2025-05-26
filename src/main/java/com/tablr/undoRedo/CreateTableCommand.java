package com.tablr.undoRedo;

import com.tablr.controller.TableController;
import com.tablr.model.Table;
import java.util.List;

/**
 * Command that creates a new table.
 */
public class CreateTableCommand implements Command {
    private final TableController tableController;
    private int createdTableId = -1;

    /**
     * Constructs new CreateTableCommand.
     *
     * @param tableController | controller that manages tables.
     */
    public CreateTableCommand(TableController tableController) {
        this.tableController = tableController;
    }

    /**
     * Retrieves ID of created table.
     * @return table ID
     */
    public int getCreatedTableId() {
        return createdTableId;
    }

    @Override
    public void execute() {
        tableController.createTable();
        List<Table> tables = tableController.getTables();
        createdTableId = tables.getLast().getId();
    }

    @Override
    public void undo() {
        if (createdTableId != -1 && tableController.hasTable(createdTableId)) {
            tableController.deleteTable(createdTableId);
        }
    }

}