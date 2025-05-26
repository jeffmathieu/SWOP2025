package com.tablr.model;
import java.util.Objects;
/**
 * Represents a column that stores email addresses.
 * This class extends the abstract Column class and provides specific behavior for email values.
 */
public final class EmailColumn extends Column<String> {

    /**
     * Constructs a new EmailColumn with the specified attributes.
     *
     * @param name The name of the column.
     * @param allowsBlank Whether blank values are allowed in the column.
     * @param defaultValue The default value for the column.
     * @param id The unique identifier for the column.
     */

    public EmailColumn(String name, boolean allowsBlank, String defaultValue, int id) {
        super(name, allowsBlank, defaultValue, id);
    }

    /**
     * Validates whether the provided value is valid for this column.
     * A valid email value must contain exactly one '@' character.
     * If the value is null or empty, it is valid only if blank values are allowed.
     *
     * @param value The value to validate.
     * @return True if the value is valid, false otherwise.
     */
    @Override
    public boolean isValidValue(String value) {
        if (value == null || value.isEmpty()) {
            return allowsBlank;
        }
        // Simple email validation: exactly one '@' character
        return value.chars().filter(ch -> ch == '@').count() == 1 && !value.contains(" ");
    }

    @Override
    public ColumnType getColumnType() {
        return ColumnType.EMAIL;
    }

    /**
     * Changes the type of the column to a BooleanColumn.
     *
     * @return A new BooleanColumn with the same name, allowsBlank, defaultValue, and id.
     */
    @Override
    public Column<?> changeType() {
        return new BooleanColumn(name, allowsBlank, null, id);
    }

    @Override
    public ColumnType getTypeName() {
        return ColumnType.EMAIL;
    }

    @Override
    public void changeDefaultValueFromString(String value) {
        if(isValidValue(value)) {
            changeDefaultValue(value);
        }
    }

    @Override
    public String parseValue(String s) {
        if (s == null || s.isEmpty()) {
            if (!allowsBlank) {
                throw new IllegalArgumentException("Blanks not allowed");
            }
            return null;
        }

        // eenvoudige email validatie: exact één @ en geen spaties
        if (!s.matches("^[^\\s@]+@[^\\s@]+$")) {
            throw new IllegalArgumentException("Invalid email format: " + s);
        }

        return s;
    }

    @Override
    public boolean canAcceptAllValuesFrom(Column<?> source) {
        try {
            if (source.getDefaultValue() != null) {
                parseValue(source.getDefaultValue().toString());
            } else if (!this.allowsBlank) {
                throw new IllegalArgumentException("Blank default not allowed");
            }

            for (int i = 0; i < source.size(); i++) {
                Object value = source.getRowValue(i);
                if (value != null) {
                    parseValue(value.toString());
                } else if (!this.allowsBlank) {
                    throw new IllegalArgumentException("Blank row value not allowed");
                }
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean canChangeToType(ColumnType type) {
        boolean allBlank = values.stream().allMatch(Objects::isNull);
        return switch (type) {
            case BOOLEAN, INTEGER -> (this.values.isEmpty() || allBlank) && this.defaultValue == null;
            default -> true;
        };
    }
}