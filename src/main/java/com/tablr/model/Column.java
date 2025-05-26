package com.tablr.model;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Represents a column in a table.
 * Each column has a name, type, default value, and a list of stored values.
 */
public abstract sealed class Column<T> implements Cloneable permits StringColumn, BooleanColumn, EmailColumn, IntegerColumn {
    protected String name;
    protected final int id;
    protected boolean allowsBlank;
    protected T defaultValue;
    protected List<T> values;

    /**
     * Constructs a new column with the specified attributes.
     *
     * @param name The name of the column.
     * @param allowsBlank Whether blank values are allowed.
     * @param defaultValue The default value for new rows.
     * @param id The unique identifier of the column.
     * @throws IllegalArgumentException if the name is blank or the default value is invalid.
     */
    protected Column(String name, boolean allowsBlank, T defaultValue, int id) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Column name cannot be blank.");
        }
        this.name = name;
        this.allowsBlank = allowsBlank;

        if (!isValidValue(defaultValue)) throw new IllegalArgumentException("Default value is not valid.");
        else if (defaultValue == "") this.defaultValue = null;
        else this.defaultValue = defaultValue;
        this.values = new ArrayList<>();
        this.id = id;
    }

    /**
     * Validates the value for the column.
     * This method should be overridden by subclasses to provide specific validation logic.
     *
     * @param value The value to be validated.
     * @return True if the value is valid, false otherwise.
     */
    protected abstract boolean isValidValue(T value);

    /**
     * Checks if the column and its values are valid.
     *
     * @return True if the column and its values are valid, false otherwise.
     */
    public boolean isValidColumn() {
        return values.stream().allMatch(this::isValidValue) && isValidValue(defaultValue);
    }

    /**
     * Gets the name of the column.
     *
     * @return The name of the column.
     */
    public String getName() { return name; }

    /**
     * Gets the unique identifier of the column.
     *
     * @return The ID of the column.
     */
    public int getId() { return id; }

    /**
     * Sets the name of the column.
     *
     * @param name The new name of the column.
     * @throws IllegalArgumentException if the name is blank.
     */
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Column name cannot be empty");
        }
        this.name = name;
    }

    /**
     * Gets the type name of the column.
     *
     * @return The type name of the column.
     */
    public abstract ColumnType getTypeName();

    /**
     * Gets the column type.
     *
     * @return The column type.
     */
    public abstract ColumnType getColumnType();

    /**
     * Creates a clone of the column.
     *
     * @return A cloned instance of the column.
     */
@SuppressWarnings("unchecked")
    public Column<T> clone() {
        try {
            return (Column<T>) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Cloning not supported", e);
        }
    }

    /**
     * Checks if blank values are allowed in the column.
     *
     * @return True if blank values are allowed, false otherwise.
     */
    public boolean allowsBlank() { return allowsBlank; }

    /**
     * Tries to update the allowsBlank property.
     * Only succeeds if no values or default are blank.
     *
     * @param newAllowsBlank The desired new value for allowsBlank.
     * @return True if updated successfully, false otherwise.
     */
    public boolean setAllowsBlank(boolean newAllowsBlank) {
        if (newAllowsBlank == this.allowsBlank) return true;

        if (!newAllowsBlank) {
            boolean hasBlankDefault = (defaultValue == null || defaultValue.toString().isBlank());
            boolean hasBlankValues = values.stream()
                    .anyMatch(v -> v == null || v.toString().isBlank());

            if (hasBlankDefault || hasBlankValues) {
                return false; // blocked
            }
        }

        this.allowsBlank = newAllowsBlank;
        return true;
    }

    /**
     * Gets the default value for the column.
     *
     * @return The default value of the column.
     */
    public T getDefaultValue() { return defaultValue; }

    /**
     * Changes the default value of the column.
     *
     * @param value The new default value.
     */
    public void changeDefaultValue(T value) {
        this.defaultValue = value;
    }

    /**
     * Gets default value as a string.
     * @return default value as string.
     */
    public String getDefaultValueAsString() {
        return defaultValue == null ? "" : defaultValue.toString();
    }

    /**
     * Changes the default value of the column from a string.
     *
     * @param value The new default value as a string.
     * @throws UnsupportedOperationException if not implemented.
     */
    public void changeDefaultValueFromString(String value) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Toggles the default value of the column.
     *
     * @throws UnsupportedOperationException if not supported for the column type.
     */
    public Boolean toggleDefaultValue() {
        throw new UnsupportedOperationException("Only supported for BooleanColumn");
    }

    /**
     * Returns an unmodifiable list of values in the column.
     *
     * @return An unmodifiable list of column values.
     */
    public List<T> getValues() {
        return Collections.unmodifiableList(values);
    }

    /**
     * Adds the default value to the column.
     */
    public void addDefaultValue() {
        values.add(defaultValue);
    }

    public void addDefaultValueAt(int index) {
        values.add(index, defaultValue);
    }

    /**
     * Removes the value at the specified index in the column.
     *
     * @param index The index of the value to remove.
     * @throws IndexOutOfBoundsException if the index is out of bounds.
     */
    public void removeValue(int index) {
        if (index < 0 || index >= values.size()) {
            throw new IndexOutOfBoundsException("Invalid index: " + index);
        }
        values.remove(index);
    }

    /**
     * Gets the value at the specified index in the column.
     *
     * @param index The index of the value to retrieve.
     * @return The value at the specified index.
     */
    public T getValue(int index) {
        return values.get(index);
    }

    /**
     * Sets the value at the specified index in the column.
     *
     * @param index The index of the value to set.
     * @param value The new value to set.
     * @throws IllegalArgumentException if the value is invalid or blank values are not allowed.
     * @throws IndexOutOfBoundsException if the index is out of bounds.
     */
    public void setValue(int index, T value) {
        if (value == null || value == "") {
            if (allowsBlank) values.set(index, null);
            else throw new IllegalArgumentException("Value cannot be blank.");
        } else if (!isValidValue(value)) throw new IllegalArgumentException("Value is not valid:" + value);
        else if (index < 0 || index >= values.size()) throw new IndexOutOfBoundsException("Invalid index for column: " + index);
        else values.set(index, value);
    }

    /**
     * Gets the value of a specific row in the column.
     *
     * @param index The row index of the value to retrieve.
     * @return The value at the specified row index.
     * @throws IndexOutOfBoundsException if the index is out of bounds.
     */
    public T getRowValue(int index) {
        if (index < 0 || index >= values.size()) {
            throw new IndexOutOfBoundsException("Invalid row index: " + index);
        }
        return values.get(index);
    }

    /**
     * Gets the number of values in the column.
     *
     * @return The size of the column.
     */
    public int size() {
        return values.size();
    }

    /**
     * Returns a string representation of the column.
     *
     * @return A string representation of the column.
     */
    @Override
    public String toString() {
        return String.format("Column{name='%s', values=%s}", name, values);
    }

    /**
     * Checks whether this column can accept all values and the default value from another column.
     * Used to determine if conversion is possible.
     *
     * @param source The column whose values are to be checked for compatibility.
     * @return True if all values and the default value can be accepted, false otherwise.
     */
    public abstract boolean canAcceptAllValuesFrom(Column<?> source);

    /**
     * Checks if the column type can be changed to the specified type.
     *
     * @param type The target column type.
     * @return True if the type change is valid, false otherwise.
     */
    public abstract boolean canChangeToType(ColumnType type);

    /**
     * Parses a string into the correct type for this column.
     * Used internally for conversion validation.
     *
     * @param s The string to parse.
     * @return The parsed value.
     */
    protected abstract T parseValue(String s);

    /**
     * Checks if the column is a Boolean column.
     *
     * @return True if the column is a Boolean column, false otherwise.
     */
    public boolean isBooleanColumn() {
        return false;
    }

    /**
     * Changes the type of the column to the next type in the cycle.
     *
     * @return A new column of the next type in the cycle.
     * @deprecated This method is deprecated and may be removed in future versions.
     */
    @Deprecated
    public abstract Column<?> changeType();

    /**
     * Sets the value at the specified index in the column.
     *
     * @param index The index of the value to set.
     * @param value The new value to set, not parsed yet.
     * @throws IndexOutOfBoundsException if the index is out of bounds.
     */
    public void setValueFromString(int index,String value){
        if (index < 0 || index >= values.size()) throw new IndexOutOfBoundsException("Invalid index for column: " + index);
        else setValue(index,parseValue(value));
    }
}