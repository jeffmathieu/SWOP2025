package com.tablr.controller;

import com.tablr.model.Column;
import com.tablr.model.ColumnFactory;
import com.tablr.model.ColumnType;
import com.tablr.model.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Manages tables by allowing creation, deletion, retrieval, and renaming.
 */
public class TableController  {
    private List<Table> tables;

    /**
     * Constructs a new TableController with an empty list of tables.
     */
    public TableController() {

        this.tables = new ArrayList<>();
    }

    /**
     * Creates a new table with a unique name and id and adds it to the list.
     */
    public void createTable() {
        int tableNumber = IntStream.iterate(1, n -> n + 1)
                .filter(n -> tables.stream().noneMatch(t -> t.getName().equals("Table" + n)))
                .findFirst()
                .orElseThrow(); // Zou nooit mogen gebeuren
        int tableId = IntStream.iterate(1, n -> n+1)
                .filter(n -> tables.stream().noneMatch(t -> t.getId() == n))
                .findFirst()
                .orElseThrow(); // Zou nooit mogen gebeuren
        String newTableName = "Table" + tableNumber;
        Table table = new Table(newTableName, tableId);
        tables.add(table);
    }

    /**
     * Add table directly to list (for testing purposes)
     * @param table | table to add
     */
    public void addTableDirectly(Table table) {
        tables.add(table);
    }

    /**
     * Inserts a table in the list of tables in the place of the index
     * @param table | Table to add
     * @param index | Index of where table should be added
     */
    public void insertTableAt(Table table, int index) {
        List<Table> copy = new ArrayList<>(tables);
        copy.add(index, table);
        this.tables = copy;
    }

    /**
     * Checks if table exists by id.
     * @param tableId | ID of table to check.
     * @return True if table exists, otherwise False.
     */
    public boolean hasTable (int tableId){
        return tables.stream().anyMatch(c -> c.getId() == tableId);
    }

    /**
     * Checks if table name is valid.
     *
     * @param tableName | Name of table to check.
     * @return True if table name is valid, otherwise False.
     */
    public boolean isValidTableName(String tableName) {
        boolean notnull = tableName != null;
        boolean notEmpty = !tableName.trim().isEmpty();
        boolean unique = tables.stream().noneMatch(c -> c.getName().equals(tableName));
        return notnull && notEmpty && unique;
    }

    /**
     * Checks if column name is valid.
     *
     * @param tableId | ID of table to check.
     * @param columnName | Name of column to check.
     * @return True if column name is valid, otherwise False.
     */
    public boolean isValidColumnName(int tableId, String columnName) {
        boolean notnull = columnName != null;
        boolean notEmpty = !columnName.trim().isEmpty();
        boolean unique = getTable(tableId).getColumns().stream()
                .noneMatch(col -> col.getName().equals(columnName));

        return notnull && notEmpty && unique;
    }

    /**
     * Checks if given value is valid for column with given id in table with given id
     * @param tableId
     * @param columnId
     * @param value
     * @return
     */
    public boolean isValidColumnValue(int tableId, int columnId, Object value) {
        return getTable(tableId).isValidColumnValue(columnId, value);
    }

    /**
     * Checks if table exists.
     * @param tableName | Name of table to check.
     * @return True if table exists, otherwise False.
     */
    public boolean hasTable (String tableName){
        return tables.stream().anyMatch(c -> c.getName().equals(tableName));
    }

    /**
     * Deletes a table by its id.
     *
     * @param tableId | The id of the table to delete.
     * @throws IllegalArgumentException if the table is not found.
     */
    public void deleteTable(int tableId) {
        boolean removed = tables.removeIf(table-> table.getId() == tableId);
        if (!removed) {
            throw new IllegalArgumentException("Table not found with id: " + tableId);
        }
    }

    /**
     * Changes default value of a given table and column (normal cell)
     * @param tableId | ID of table
     * @param columnId | ID of column
     * @param value | new default value
     */
    public void changeDefaultValue(int tableId, int columnId, String value) {
        Column<?> column = getTable(tableId).getColumn(columnId);
        column.changeDefaultValueFromString(value);
    }

    /**
     * Toggles default value of a given table and column (boolean cell)
     * @param tableId | ID of table
     * @param columnId | ID of column
     * @return New default value
     */
    public Boolean toggleDefaultValue(int tableId, int columnId) {
        Column<?> column = getTable(tableId).getColumn(columnId);
        return column.toggleDefaultValue();
    }

    /**
     * Retrieves a list of all tables.
     *
     * @return A list of tables.
     */
    public List<Table> getTables() {
        // encapsulation
        return new ArrayList<>(tables);
    }

    /**
     * Retrieves a table by its id.
     *
     * @param id of the table.
     * @return Table object.
     * @throws IllegalArgumentException if table is not found.
     */
    public Table getTable(int id) {
        return tables.stream()
                .filter(t -> t.getId() == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Table not found with Id: " + id));
    }

    /**
     * Adds a column to a specified table.
     *
     * @param tableId | The ID of the table.
     */
    public void addColumnToTable(int tableId) {
        Table table = getTable(tableId);
        table.createColumn();
    }

    /**
     * Inserts column in table at given index
     * @param tableId | ID of table
     * @param column | column to be added
     * @param index | index of where in list of columns, column should be added
     */
    public void insertColumnAt(int tableId, Column<?> column, int index) {
        Table table = getTable(tableId);
        List<Column<?>> mutable = new ArrayList<>(table.getColumns());
        mutable.add(index, column);
        table.setColumns(mutable);
    }

    /**
     * Set row value of given row, column and table
     * @param tableId | ID of table
     * @param columnId | ID of column
     * @param rowIndex | index of row
     * @param value | new value
     */
    public void setRowValue(int tableId, int columnId, int rowIndex, Object value) {
        getTable(tableId).setValue(columnId, rowIndex, value);
    }

    /**
     * Get list of column names of given table.
     * @param tableId | ID of table
     * @return list of column names
     */
    public List<String> getTableColumnNames(int tableId) {
        return getTable(tableId).getColumnNames();
    }

    /**
     * Get list of all the values of a given rows of a table/
     * @param tableId | ID of table.
     * @param rowIndex | index of row
     * @return list of all row values
     */
    public List<Object> getTableRowValues(int tableId,int rowIndex) {
        return getTable(tableId).getRowValues(rowIndex);
    }

    /**
     * Adds row to a specified table.
     *
     * @param tableId | ID of the table.
     */
    public void addRowToTable(int tableId) {
        getTable(tableId).createRow();
    }

    /**
     * Insert a row at a specific index in a table.
     * @param tableId | ID of table
     * @param rowIndex | index of the row
     */
    public void insertRowAt(int tableId, int rowIndex) {
        for (Column<?> col : getTable(tableId).getColumns()) {
            col.addDefaultValueAt(rowIndex); // this method must exist in your Column class
        }
    }

    /**
     * Removes row from specified table.
     *
     * @param tableId | ID of the table.
     * @param rowIndex | Index of row to remove.
     */
    public void removeRowFromTable(int tableId, int rowIndex) {
        getTable(tableId).removeRow(rowIndex);
    }

    /**
     * Renames a table with the given id, only if valid.
     *
     * @param tableId The id of the table to rename.
     * @param userInput The new name for the table.
     * @throws IndexOutOfBoundsException if the index is invalid.
     * @throws IllegalArgumentException if the name is empty or already exists.
     */
    public void renameTable(int tableId, String userInput) {
        Objects.requireNonNull(userInput, "Table name cannot be null");
        if (userInput.trim().isEmpty()) {
            throw new IllegalArgumentException("Table name cannot be empty.");
        }
        if (tables.stream().anyMatch(table -> table.getName().equals(userInput))) {
            throw new IllegalArgumentException("Table name '" + userInput + "' already exists.");
        }
        getTable(tableId).setName(userInput);
    }

    /**
     * Rename a column of a given table
     * @param tableId | ID of table
     * @param columnId | ID of column
     * @param newName | New name for the column
     */
    public void renameColumn(int tableId, int columnId, String newName) {
        if (!isValidColumnName(tableId, newName)) {
            throw new IllegalArgumentException("Invalid column name.");
        }
        getTable(tableId).getColumn(columnId).setName(newName);
    }

    /**
     * Delete a column from a table
     * @param tableId | ID of table
     * @param columnId | ID of column
     */
    public void deleteColumn(int tableId, int columnId) {
        getTable(tableId).removeColumn(columnId);
    }

    /**
     * Tries to update the allowsBlank property of a column in the specified table.
     *
     * @param tableId | The ID of the table containing the column.
     * @param columnId | The ID of the column to be updated.
     * @param newAllowsBlank | The new value for the allowsBlank property.
     * @return true if the update was successful; false if it was blocked due to invalid blank values.
     */
    public boolean tryToggleAllowsBlank(int tableId, int columnId, boolean newAllowsBlank) {
        Table table = getTable(tableId);
        Column<?> column = table.getColumn(columnId);
        return column.setAllowsBlank(newAllowsBlank);
    }

    /**
     * Check if a type conversion is valid.
     * @param tableId | ID of table
     * @param columnId | ID of column
     * @param type | new type of column
     * @return true if valid conversion, false otherwise
     */
    public boolean isValidTypeConversion(int tableId, int columnId,ColumnType type) {
        Column<?> col = getTable(tableId).getColumn(columnId);
        return col.canChangeToType(type);
    }

    /**
     * Cycles types of a column
     * @param tableId | ID of table
     * @param columnId | ID of column
     * @param newType | New type of the column
     */
    public void cycleColumnType(int tableId, int columnId, ColumnType newType) {
        Table table = getTable(tableId);
        Column<?> oldCol = table.getColumn(columnId);
        Column<?> newCol = ColumnFactory.createColumn(newType, oldCol);

        for (int r = 0; r < table.getRowCount(); r++) {
            newCol.addDefaultValue();
        }
        for (int r = 0; r < table.getRowCount(); r++) {
            newCol.setValueFromString(r,objectToString(oldCol.getValue(r)));
        }

        table.replaceColumnById(columnId, newCol);
    }

    /**
     * Replaces a column with another column
     * @param tableId | ID of table
     * @param columnId | ID of column
     * @param newColumn | new column
     */
    public void replaceColumn(int tableId, int columnId, Column<?> newColumn) {
        getTable(tableId).replaceColumnById(columnId, newColumn);
    }

    private String objectToString(Object object) {
        if(object == null) return "";
        else return object.toString();
    }

    /**
     * Retrieves column id of a given table and row
     * @param tableId | ID of table
     * @param rowIndex | index of row
     * @return ID of column
     */
    public int getColumnIdAt(int tableId, int rowIndex) {
        return getTable(tableId).getColumnByIndex(rowIndex).getId();
    }

    /**
     * Get a list of all column types of a given table
     * @param tableId | ID of table
     * @return List of column types
     */
    public List<ColumnType> getColumnTypesOfTable(int tableId) {
        return getTable(tableId).getColumnTypesList();
    }

    /**
     * Get a list of all column IDs of a table
     * @param tableId | ID of table
     * @return list of Column ID
     */
    public List<Integer> getColumnIdsOfTable(int tableId) {
        return getTable(tableId).getColumnIds();
    }

    /**
     * Get row value of a given row, column and table
     * @param tableId | ID of table
     * @param columnId | ID of column
     * @param rowIndex | Index of row
     * @return value of given row and column
     */
    public Object getRowValue(int tableId, int columnId, int rowIndex) {
        return getTable(tableId).getColumn(columnId).getValue(rowIndex);
    }

    /**
     * Retrieves the number of columns of a given table.
     * @param tableId | ID of table
     * @return number of columns
     */
    public int getColumnCount(int tableId) {
        return getTable(tableId).getColumnCount();
    }

    /**
     * Retrieves the number of rows of a given table.
     * @param tableId | ID of table
     * @return number of rows
     */
    public int getRowCount(int tableId) {
        return getTable(tableId).getRowCount();
    }

    /**
     * Retrieves list of all columns of a table
     * @param tableId | ID of table
     * @return list of all columns of this table
     */
    public List<Column<?>> getColumns(int tableId) {
        return getTable(tableId).getColumns();
    }

    /**
     * Retrieve a given column of a given table
     * @param tableId | ID of table
     * @param columnId | ID of column
     * @return Column
     */
    public Column<?> getColumn(int tableId, int columnId) {
        return getTable(tableId).getColumn(columnId);
    }

    /**
     * Retrieves table name.
     * @param tableId | ID of table
     * @return name of given table.
     */
    public String getTableName(int tableId) {
        return getTable(tableId).getName();
    }

    /**
     * Get name of a given column from a given table.
     * @param tableId | ID of table
     * @param columnId | ID of column
     * @return Name of column
     */
    public String getColumnName(int tableId, int columnId) {
        return getTable(tableId).getColumn(columnId).getName();
    }

    /**
     * Clone a table.
     * @param tableId | ID of table to clone.
     * @return cloned table
     */
    public Table cloneTable(int tableId) {
        return getTable(tableId).deepClone();
    }

    /**
     * Clears list of tables
     */
    public void clearTables() {
        getTables().clear();
    }

    /**
     * Get list of IDs of all tables.
     * @return List of tableIds
     */
    public List<Integer> getTableIds() {
        return getTables().stream().map(Table::getId).collect(Collectors.toList());
    }

    /**
     * retrieve if given column of given table allows blanks.
     * @param tableId | ID of table
     * @param columnId | ID of column
     * @return True if allows blank, False otherwise
     */
    public boolean getAllowsBlank(int tableId, int columnId) {
        return getTable(tableId).getColumn(columnId).allowsBlank();
    }
}
