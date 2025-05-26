package com.tablr.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IntegerColumnTest {
    IntegerColumn col;

    @BeforeEach
    void setUp() {
        col = new IntegerColumn("Int", true, null, 1);
    }

    @Test
    void testValidValues() {
        assertTrue(col.isValidValue(10));
        assertTrue(col.isValidValue(null));
    }

    @Test
    void testParseValue() {
        assertEquals(123, col.parseValue("123"));
    }

    @Test
    void testChangeDefaultValueFromString() {
        col.changeDefaultValueFromString("42");
        assertEquals(42, col.getDefaultValue());
        assertThrows(IllegalArgumentException.class, () -> col.changeDefaultValueFromString("01"));
    }

    @Test
    void testChangeType() {
        assertEquals(ColumnType.STRING, col.changeType().getColumnType());
    }

    @Test
    void testCanAcceptAllValuesFrom() {
        IntegerColumn source = new IntegerColumn("Source", true, 123, 2);
        source.addDefaultValue();
        col.addDefaultValue();
        assertTrue(col.canAcceptAllValuesFrom(source));
    }

    @Test
    void testCanChangeToType() {
        assertTrue(col.canChangeToType(ColumnType.STRING));
        col = new IntegerColumn("WithDefault", false, 99, 3);
        assertFalse(col.canChangeToType(ColumnType.EMAIL));
    }
}
