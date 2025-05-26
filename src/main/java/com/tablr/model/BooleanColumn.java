package com.tablr.model;

import java.util.Objects;

/**
 * Represents a column that stores Boolean values.
 * This class extends the abstract Column class and provides specific behavior for Boolean values.
 */
public final class BooleanColumn extends Column<Boolean> {

    /**
     * Constructs a new BooleanColumn with the specified attributes.
     *
     * @param name The name of the column.
     * @param allowsBlank Whether blank values are allowed in the column.
     * @param defaultValue The default value for the column.
     * @param id The unique identifier for the column.
     */
    public BooleanColumn(String name, boolean allowsBlank, Boolean defaultValue, int id) {
        super(name, allowsBlank, defaultValue, id);
    }

    @Override
    public ColumnType getColumnType() {
        return ColumnType.BOOLEAN;
    }

    /**
     * Validates whether the provided value is valid for this column.
     *
     * @param value The value to validate.
     * @return True if the value is valid, false otherwise.
     */
    @Override
    protected boolean isValidValue(Boolean value) {
        if (value == null) {
            return allowsBlank;
        }
        return true;
    }

    @Override
    public ColumnType getTypeName() {
        return ColumnType.BOOLEAN;
    }

    /**
     * Changes the default value for the column.
     *
     * If blank values are not allowed, the default value toggles between true and false.
     * If blank values are allowed, the default value cycles between null, true, and false.
     *
     */
    public Boolean toggleDefaultValue() {
        if(allowsBlank) {
            if (defaultValue == null) {
                return true;
            } else if (defaultValue) {
                return false;
            } else {
                return null;
            }
        }else {
            return !defaultValue;
        }

    }

    /**
     * Changes the default value for the column.
     *
     * If blank values are not allowed, the default value toggles between true and false.
     * If blank values are allowed, the default value cycles between null, true, and false.
     *
     * @param newDefaultValue The new default value to set.
     */
    @Override
    public void changeDefaultValue(Boolean newDefaultValue) {
        if (!allowsBlank) {
            if(newDefaultValue==null) {throw new IllegalArgumentException("newDefaultValue cannot be null if column does not allow blank");}
            else{
                this.defaultValue = newDefaultValue;
            }
        } else {
            this.defaultValue = newDefaultValue;
        }
    }

    @Override
    public void changeDefaultValueFromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            changeDefaultValue(null); // clear it
        } else if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
            changeDefaultValue(parseValue(value));
        } else {
            throw new IllegalArgumentException("Invalid boolean value: " + value);
        }
    }


    /**
     * Indicates whether this column is a Boolean column.
     *
     * @return True, as this is a Boolean column.
     */
    @Override
    public boolean isBooleanColumn() {
        return true;
    }

    /**
     * Changes the type of the column to an IntegerColumn.
     *
     * @return A new IntegerColumn with the same name, allowsBlank, defaultValue, and id.
     * @throws IllegalArgumentException If the default value is not null.
     */
    @Override
    public Column<?> changeType() {
        if (defaultValue != null) throw new IllegalArgumentException("Default value isn't null.");
        return new IntegerColumn(name, allowsBlank, null, id);
    }

    @Override
    public Boolean parseValue(String s) {
        if (s == null || s.isEmpty()) {
            if (!allowsBlank) {
                throw new IllegalArgumentException("Blanks not allowed");
            }
            return null;
        }

        String normalized = s.trim().toLowerCase();
        if ("true".equals(normalized)) return true;
        if ("false".equals(normalized)) return false;

        throw new IllegalArgumentException("Invalid boolean string: " + s);
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
        switch (type) {
            case INTEGER, EMAIL -> {
                return (this.values.isEmpty() || allBlank) && this.defaultValue == null;
            }
            default -> {return true;}
        }
    }
}
