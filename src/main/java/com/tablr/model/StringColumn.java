package com.tablr.model;

import java.util.Objects;

/**
 * Represents a column that stores String values.
 * This class extends the abstract Column class and provides specific behavior for String values.
 */
public final class StringColumn extends Column<String> {

    /**
     * Constructs a new StringColumn with the specified attributes.
     *
     * @param name The name of the column.
     * @param allowsBlank Whether blank values are allowed in the column.
     * @param defaultValue The default value for the column.
     * @param id The unique identifier for the column.
     */
    public StringColumn(String name, boolean allowsBlank, String defaultValue, int id) {
        super(name, allowsBlank, defaultValue, id);
    }

    @Override
    public ColumnType getColumnType() {
        return ColumnType.STRING;
    }

    /**
     * Validates whether the provided value is valid for this column.
     *
     * A valid String value must not be null or empty unless blank values are allowed.
     *
     * @param value The value to validate.
     * @return True if the value is valid, false otherwise.
     */
    @Override
    public boolean isValidValue(String value) {
        if (value == null || value.isEmpty()) {
            return allowsBlank;
        }
        return true;
    }

    @Override
    public ColumnType getTypeName() {
        return ColumnType.STRING;
    }

    /**
     * Changes the type of the column to an EmailColumn.
     *
     * @return A new EmailColumn with the same name, allowsBlank, defaultValue, and id.
     */
    @Override
    public Column<?> changeType() {
        return new EmailColumn(name, allowsBlank, "", id);
    }

    @Override
    public void changeDefaultValueFromString(String value) {
        if ((value == null || value.isEmpty()) && allowsBlank) {
            changeDefaultValue(null);
        } else if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Blanks not allowed for this column");
        } else {
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
        return s;
    }

    @Override
    public boolean canAcceptAllValuesFrom(Column<?> source) {
        try {
            if (source.getDefaultValue() != null) {
                parseValue(source.getDefaultValue().toString());
            } else if (!this.allowsBlank) {
                // default is null maar we staan geen blanks toe
                throw new IllegalArgumentException("Blank default not allowed in StringColumn");
            }

            for (int i = 0; i < source.size(); i++) {
                Object value = source.getRowValue(i);
                if (value != null) {
                    parseValue(value.toString());
                } else if (!this.allowsBlank) {
                    throw new IllegalArgumentException("Blank value not allowed in StringColumn");
                }
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isIntString(Object str) {
        if(str == null) return true;
        try{
            Integer.parseInt(String.valueOf(str));
        }catch(NumberFormatException e){
            return false;
        }
        return true;
    }

    @Override
    public boolean canChangeToType(ColumnType type) {
        boolean allBlank = values.stream().allMatch(Objects::isNull);

        switch (type) {
            // TODO: implement proper email validation, nakijken null
            case EMAIL -> {
                boolean allEmail = values.stream().allMatch(v-> v != null && v.contains("@"));
                return (this.values.isEmpty() || allBlank || allEmail)
                        && (this.defaultValue == null || this.defaultValue.contains("@"));
            }
            case INTEGER -> {
                boolean allInt = values.stream().allMatch(this::isIntString);
                boolean defVal = true;
                try{
                    Integer.parseInt(String.valueOf(defaultValue));
                } catch (NumberFormatException e) {
                    defVal = false;
                }
                //defVal = true;
                return (allInt || allBlank) && (defVal || defaultValue == null);
            }
            case BOOLEAN -> {
                boolean allBoolean = values.stream().allMatch(v -> isBooleanString(String.valueOf(v)));
                boolean defVal = isBooleanString(String.valueOf(defaultValue));
                return (allBoolean || allBlank) && (defVal || defaultValue == null);
            }
            default -> {return true;}
        }
    }

    public static boolean isBooleanString(String s) {
        if (s == null) return true; // blanks toegestaan
        String normalized = s.trim().toLowerCase();
        return normalized.equals("true") || normalized.equals("false");
    }

}
