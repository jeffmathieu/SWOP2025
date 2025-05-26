package com.tablr.model;

/**
 * Factory class for creating different types of columns based on the specified ColumnType.
 */
public class ColumnFactory {

    /**
     * Creates a new column of the specified type, using the base column for initialization.
     *
     * @param type The type of column to create (e.g., STRING, EMAIL, BOOLEAN, INTEGER).
     * @param base The base column to use for initialization.
     * @return A new column of the specified type.
     */
    public static Column<?> createColumn(ColumnType type, Column<?> base) {
        Object defaultValue = base.getDefaultValue();

        return switch (type) {
            case STRING -> new StringColumn(base.getName(), base.allowsBlank(), objectToString(defaultValue), base.getId());
            case EMAIL -> new EmailColumn(base.getName(), base.allowsBlank(), objectToString(defaultValue), base.getId());
            case BOOLEAN -> new BooleanColumn(base.getName(), base.allowsBlank(), stringToBoolean(objectToString(defaultValue)), base.getId());
            case INTEGER -> new IntegerColumn(base.getName(), base.allowsBlank(), myStringToInt(objectToString(defaultValue)), base.getId());
        };
    }

    /**
     * Converts a string to a Boolean value.
     *
     * @param string The string to convert.
     * @return True if the string is "true", false otherwise. Returns null if the string is null.
     */
    private static Boolean stringToBoolean(String string) {
        switch (string) {
            case null -> {
                return null;
            }
            case "true" -> {
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    /**
     * Converts an object to its string representation.
     *
     * @param object The object to convert.
     * @return The string representation of the object, or null if the object is null.
     */
    private static String objectToString(Object object) {
        if (object == null) {
            return null;
        } else {
            return object.toString();
        }
    }

    /**
     * Converts a string to an Integer value.
     *
     * @param string The string to convert.
     * @return The Integer value of the string, or null if the string is null, empty, or "null".
     * @throws NumberFormatException If the string cannot be parsed as an integer.
     */
    public static Integer myStringToInt(String string) {
        if (string == null || string.isEmpty()) {
            return null;
        } else {
            return Integer.parseInt(string);
        }
    }
}