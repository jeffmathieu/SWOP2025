package com.tablr.model;

/**
 * Enum representing the different types of columns that can exist in a table.
 */
public enum ColumnType {
    STRING, // Represents a column that stores string values.
    EMAIL,  // Represents a column that stores email addresses.
    BOOLEAN, // Represents a column that stores boolean values (true/false).
    INTEGER; // Represents a column that stores integer values.

    /**
     * Returns the next ColumnType in a cyclic order.
     *
     * @return The next ColumnType in the sequence: STRING -> EMAIL -> BOOLEAN -> INTEGER -> STRING.
     */
    public ColumnType next() {
        return switch (this) {
            case STRING -> EMAIL;
            case EMAIL -> BOOLEAN;
            case BOOLEAN -> INTEGER;
            case INTEGER -> STRING;
        };
    }
}