package com.tablr.model;
import java.util.Objects;
/**
 * Represents a column that stores Integer values.
 * This class extends the abstract Column class and provides specific behavior for Integer values.
 */
public final class IntegerColumn extends Column<Integer> {

    /**
     * Constructs a new IntegerColumn with the specified attributes.
     *
     * @param name The name of the column.
     * @param allowsBlank Whether blank values are allowed in the column.
     * @param defaultValue The default value for the column.
     * @param id The unique identifier for the column.
     */
    public IntegerColumn(String name, boolean allowsBlank, Integer defaultValue, int id) {
        super(name, allowsBlank, defaultValue, id);
    }

    /**
     * Validates whether the provided value is valid for this column.
     *
     * @param value The value to validate.
     * @return True if the value is valid, false otherwise.
     */

    @Override
    public boolean isValidValue(Integer value) {
        if (value == null) {
            return allowsBlank;
        }
        return true;
    }
    @Override
    public ColumnType getColumnType() {
        return ColumnType.INTEGER;
    }

    @Override
    public ColumnType getTypeName() {
        return ColumnType.INTEGER;
    }

    /**
     * Changes the type of the column to a StringColumn.
     *
     * @return A new StringColumn with the same name, allowsBlank, defaultValue, and id.
     */


    @Override
    public Column<?> changeType() {
        return new StringColumn(name, allowsBlank, "", id);
    }

    @Override
    public void changeDefaultValueFromString(String value) {
        if ((value == null || value.isEmpty()) && allowsBlank) {
            changeDefaultValue(null);
            return;
        } else if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Blanks not allowed");
        }

        try {
            int parsed = Integer.parseInt(value);
            if (!Integer.toString(parsed).equals(value)) {
                // leidt getallen met voorloopnullen af
                throw new IllegalArgumentException("Invalid integer format");
            }
            changeDefaultValue(parsed);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid integer: " + value);
        }
    }

    @Override
    public Integer parseValue(String s) {
        return ColumnFactory.myStringToInt(s);
    }

    @Override
    public boolean canAcceptAllValuesFrom(Column<?> source) {
        try {
            if (source.getDefaultValue() != null) {
                parseValue(source.getDefaultValue().toString());
            }

            for (int i = 0; i < source.size(); i++) {
                Object value = source.getRowValue(i);
                if (value != null) {
                    parseValue(value.toString());
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
            case EMAIL,BOOLEAN -> {
                return (this.values.isEmpty() || allBlank) && this.defaultValue == null;
            }
            default -> {return true;}
        }
    }
}
