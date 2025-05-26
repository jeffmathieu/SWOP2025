package com.tablr.model;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Represents a table in the application, contains multiple columns and rows.
 * A table can be created, renamed, modified by adding/removing columns, and
 * it supports operations to manipulate row data.
 */
public class Table {
    private String name;
    private final int id;
    private List<Column<?>> columns;

    /**
     * Constructs a new table with the given name.
     *
     * @param name | The name of the table.
     * @throws IllegalArgumentException if the name is null or empty.
     */
    public Table(String name, int id) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Table name cannot be empty.");
        }
        this.name = name;
        this.id = id;
        this.columns = new ArrayList<>();
    }

    /**
     * Get a cloned version of a table
     * @return cloned table
     */
    public Table deepClone() {
        Table clonedTable = new Table(this.name, this.id);
        for (Column column : this.columns) {
            clonedTable.addColumn(column.clone());
        }
        return clonedTable;
    }
    /**
     * Gets the name of the table.
     *
     * @return The table's name.
     */
    public String getName() { return name; }

    /**
     * Sets a new name for the table.
     *
     * @param name The new name of the table.
     * @throws IllegalArgumentException if the name is null or empty.
     */
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Table name cannot be empty.");
        }
            this.name = name;
    }

    /**
     * Gets the ID of the table.
     *
     * @return The table's ID.
     */
    public int getId() { return id; }

    // unmodifiable list
    /**
     * Returns unmodifiable list of table's columns.
     *
     * @return The list of columns in the table.
     */
    public List<Column<?>> getColumns () {
        return Collections.unmodifiableList(columns);
    }

    /**
     * Get list of column IDs of table
     * @return list of column IDs
     */
    public List<Integer> getColumnIds() {return columns.stream().map(Column::getId).collect(Collectors.toList());}

    /**
     * Get list of column names of table
     * @return list of column names
     */
    public List<String> getColumnNames() { return columns.stream().map(Column::getName).collect(Collectors.toList()); }

    /**
     * Get list of al row values of a given row from a table
     * @param index | Index of row
     * @return List of row values for this row
     */
    public List<Object> getRowValues(int index) {
        if(index < 0 || index >= getRowCount()) { return null;}
        return columns.stream().map(c->c.getValue(index)).collect(Collectors.toList());
    }

    /**
     * Creates and adds new column to the table with default configuration.
     * Column is named "ColumnN" where N is a unique identifier.
     */
    public void createColumn() {
        int columnCount = IntStream.iterate(1, n -> n + 1)
                .filter(n -> columns.stream().noneMatch(c -> c.getName().equals("Column" + n)))
                .findFirst()
                .orElseThrow(); // Should never happen
        int columnId = IntStream.iterate(1, n -> n + 1)
                .filter(n -> columns.stream().noneMatch(c -> c.getId() == n))
                .findFirst()
                .orElseThrow(); // Should never happen
        Column<?> column = new StringColumn("Column"+columnCount, true, "", columnId);
        addColumn(column);
    }

    /**
     * Checks if given value is valid for column with given id
     * @param columnId | ID of column
     * @param value | value to be checked
     * @return True if value is valid, False otherwise.
     */
    public boolean isValidColumnValue(int columnId, Object value) {
        Column<?> c = getColumn(columnId);
        if(c!= null){
            return switch (c) {
                case IntegerColumn i -> i.isValidValue((Integer) value);
                case StringColumn s -> s.isValidValue((String) value);
                case EmailColumn e -> e.isValidValue((String) value);
                case BooleanColumn b -> b.isValidValue((Boolean) value);
            };
        }
        throw new IllegalArgumentException("Column " + columnId + " is not valid.");
    }

    /**
     * Creates new row with default values in table.
     */
    public void createRow(){
        for(Column<?> column : columns){
            column.addDefaultValue();
        }
    }

    /**
     * Adds a new column to the table.
     *
     * @param column The column to be added.
     * @throws IllegalArgumentException if a column with the same name already exists.
     */
    private void addColumn (Column<?> column) {
        if (hasColumn(column.getName())) {
            throw new IllegalArgumentException("Column with name '" + column.getName() + "' already exists.");
        }
        this.columns.add(column);
        int rowCount = getRowCount();
        while (column.getValues().size() < rowCount) {
            column.addDefaultValue();
        }
    }

    /**
     * Removes a column from the table by id.
     *
     * @param columnId The id of the column to remove.
     */
    public void removeColumn(int columnId) {
        columns.removeIf(c -> c.getId() == columnId);
    }

    /**
     * Retrieves a column by id.
     *
     * @param columnId The id of the column.
     * @return The column object.
     * @throws NoSuchElementException if the column does not exist.
     */
    public Column<?> getColumn (int columnId){
        return columns.stream()
                .filter(c -> c.getId() == columnId)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Column not found: " + columnId));
    }

    /**
     * Gets the column at the given index.
     * Used internally to avoid direct access to columns list.
     *
     * @param index | index of the column (0-based)
     * @return Column at the given index
     * @throws IndexOutOfBoundsException if index is invalid
     */
    public Column<?> getColumnByIndex(int index) {
        if (index < 0 || index >= columns.size()) {
            throw new IndexOutOfBoundsException("Invalid column index: " + index);
        }
        return columns.get(index);
    }

    /**
     * Set a value to a cell given the column and row of this cell.
     * @param columnId | ID of column
     * @param rowIndex | index of row
     * @param value | new value of cell
     */
    public void setValue(int columnId, int rowIndex, Object value) {
        Column column = getColumn(columnId);
        try {
            column.setValue(rowIndex, value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid value for column " + column.getName() + ": " + value, e);
        } catch (IndexOutOfBoundsException e) {
            throw new IndexOutOfBoundsException("Invalid row index: " + rowIndex);
        }
    }

    /**
     * Sets list of columns in table
     * @param newColumns | columns
     */
    public void setColumns(List<Column<?>> newColumns) {
        this.columns = new ArrayList<>(newColumns);
    }

    /**
     * Replaces a column by ID with a new column of a different type.
     *
     * @param columnId | ID of the column to replace
     * @param newColumn | New column to place at same index
     * @throws NoSuchElementException if the original column doesn't exist
     */
    public void replaceColumnById(int columnId, Column<?> newColumn) {
        for (int i = 0; i < columns.size(); i++) {
            if (columns.get(i).getId() == columnId) {
                columns.set(i, newColumn);
                return;
            }
        }
        throw new NoSuchElementException("Column not found with ID: " + columnId);
    }

    /**
     * Checks if a column with the specified name exists.
     *
     * @param columnName The name of the column.
     * @return True if the column exists, otherwise false.
     */
    public boolean hasColumn (String columnName){
        return columns.stream().anyMatch(c -> c.getName().equals(columnName));
    }

    public boolean hasColumnId (int columnId) {
        return columns.stream().anyMatch(c -> c.getId() == columnId);
    }
    /**
     * Removes a row at the specified index.
     *
     * @param index The index of the row to remove.
     * @throws IndexOutOfBoundsException if the index is invalid.
     */
    public void removeRow (int index){
        if (index < 0 || index >= getRowCount()) {
            throw new IndexOutOfBoundsException("Invalid row index: " + index);
        }
        for (Column column : columns) {
            column.removeValue(index);
        }
    }

    /**
     * Gets the number of rows in the table.
     *
     * @return The number of rows.
     */
    public int getRowCount() {
        return columns.isEmpty() ? 0 : columns.stream().mapToInt(col -> col.getValues().size()).max().orElse(0);
    }

    /**
     * Gets the number of columns in the table.
     *
     * @return number of columns in the table.
     */
    public int getColumnCount() {
        return columns.size();
    }

    /**
     * Get list of all column types of a table
     * @return list of column types
     */
    public List<ColumnType> getColumnTypesList(){
        return columns.stream().map(Column::getColumnType).collect(Collectors.toList());
    }
}
