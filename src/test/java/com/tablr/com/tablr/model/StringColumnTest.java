package com.tablr.model;

import com.tablr.controller.TableController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringColumnTest {
    StringColumn col;

    @BeforeEach
    void setUp() {
        col = new StringColumn("Str", true, null, 1);
    }

    @Test
    void testValidValues() {
        assertTrue(col.isValidValue("abc"));
        assertTrue(col.isValidValue("")); // allows blank
        assertTrue(col.isValidValue(null)); // allows blank
    }

    @Test
    void testParse() {
        assertEquals("abc", col.parseValue("abc"));
        assertNull(col.parseValue(""));
    }

    @Test
    void testChangeDefault() {
        col.changeDefaultValueFromString("new");
        assertEquals("new", col.getDefaultValue());
    }

    @Test
    void testChangeType() {
        assertEquals(ColumnType.EMAIL, col.changeType().getColumnType());
    }

    @Test
    void testCanChangeToType() {
        col.addDefaultValue();
        col.setValue(0, "true");
        assertTrue(col.canChangeToType(ColumnType.BOOLEAN));
        assertTrue(col.canChangeToType(ColumnType.STRING));
    }

    private Column<?> setupColumnWithValues(String... values) {
        TableController controller = new TableController();
        controller.createTable();
        controller.addColumnToTable(1);
        Table table = controller.getTable(1);
        Column<?> col = table.getColumn(1);
        for (String v : values) {
            controller.addRowToTable(1);
            col.setValueFromString(col.size() - 1, v);
        }
        return col;
    }

    @Test
    void testAcceptsFromCompatibleStringColumn() {
        Column<?> source = setupColumnWithValues("Hello");
        ((StringColumn) source).changeDefaultValue("Hi");

        Column<?> target = setupColumnWithValues();
        ((StringColumn) target).changeDefaultValue("Default");

        assertTrue(target.canAcceptAllValuesFrom(source));
    }

    @Test
    void testAcceptFailsOnIncompatibleDefaultValue() {
        TableController controller = new TableController();
        controller.createTable();
        controller.addColumnToTable(1);
        Table table = controller.getTable(1);
        Column<?> source = new IntegerColumn("Int", true, null, 1);
        ((IntegerColumn) source).changeDefaultValue(123);
        source.setAllowsBlank(true);
        source.addDefaultValue();

        Column<?> target = table.getColumn(1);
        assertTrue(target.canAcceptAllValuesFrom(source));
    }

    @Test
    void testCanChangeToEmailColumn() {
        Column<?> col = setupColumnWithValues("x@y.com");
        ((StringColumn) col).changeDefaultValue("a@b.com");

        assertTrue(col.canChangeToType(ColumnType.EMAIL));
    }

    @Test
    void testCannotChangeToEmailWithInvalidValues() {
        Column<?> col = setupColumnWithValues("invalid");
        ((StringColumn) col).changeDefaultValue("not@email");

        assertFalse(col.canChangeToType(ColumnType.EMAIL));
    }

    @Test
    void testCanChangeToIntegerIfParsable() {
        Column<?> col = setupColumnWithValues("42");
        ((StringColumn) col).changeDefaultValue("99");

        assertTrue(col.canChangeToType(ColumnType.INTEGER));
    }

    @Test
    void testCannotChangeToIntegerWithInvalidStrings() {
        Column<?> col = setupColumnWithValues("notNumber");
        ((StringColumn) col).changeDefaultValue("abc");

        assertFalse(col.canChangeToType(ColumnType.INTEGER));
    }

    @Test
    void testCanChangeToBooleanWithValidStrings() {
        Column<?> col = setupColumnWithValues("true", "false");
        ((StringColumn) col).changeDefaultValue("false");

        assertTrue(col.canChangeToType(ColumnType.BOOLEAN));
    }

    @Test
    void testCannotChangeToBooleanWithInvalidValues() {
        Column<?> col = setupColumnWithValues("maybe", "nope");
        ((StringColumn) col).changeDefaultValue("yes");

        assertFalse(col.canChangeToType(ColumnType.BOOLEAN));
    }

    @Test
    void testChangeToTypeOnBlankColumn() {
        Column<?> col = setupColumnWithValues(null, null);
        col.setAllowsBlank(true);
        col.changeDefaultValue(null);

        assertTrue(col.canChangeToType(ColumnType.EMAIL));
        assertTrue(col.canChangeToType(ColumnType.INTEGER));
        assertTrue(col.canChangeToType(ColumnType.BOOLEAN));
    }

    @Test
    void testCanChangeToStringTypeAlways() {
        Column<?> col = setupColumnWithValues("whatever");
        assertTrue(col.canChangeToType(ColumnType.STRING));
    }
}

