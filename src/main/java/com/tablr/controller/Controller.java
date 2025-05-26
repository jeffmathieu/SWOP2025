package com.tablr.controller;

import com.tablr.model.Table;

/**
 * Abstract base class for controllers that manage tables and their components.
 * Provides common functionality for managing a current table.
 */
public abstract class Controller {
    public Table table;

    /**
     * Constructs a controller with the specified table.
     *
     * @param table | The table managed by this controller.
     */
    protected Controller(Table table) {
        this.table = table;
    }

    /**
     * Sets the current table for this controller.
     *
     * @param table | The new table to be managed.
     */
    public void setCurrentTable(Table table) {
        this.table = table;
    }
}
