package com.tablr.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BooleanColumnTest {
    BooleanColumn col;

    @BeforeEach
    void setUp() {
        col = new BooleanColumn("BoolCol", true, null, 1);
    }

    @Test
    void testToggleDefaultValue() {
        Boolean next = col.toggleDefaultValue(); // null -> true
        assertEquals(true, next);
        col.changeDefaultValue(next);
        next = col.toggleDefaultValue(); // true -> false
        assertEquals(false, next);
        col.changeDefaultValue(next);
        next = col.toggleDefaultValue(); // false -> null
        assertNull(next);
    }

    @Test
    void testSetAndGet() {
        col.addDefaultValue(); // adds null
        col.setValue(0, true);
        assertTrue(col.getValue(0));
    }

    @Test
    void testParsing() {
        assertEquals(true, col.parseValue("true"));
        assertEquals(false, col.parseValue("false"));
        assertNull(col.parseValue(""));
        assertThrows(IllegalArgumentException.class, () -> col.parseValue("invalid"));
    }

    @Test
    void testChangeType() {
        assertThrows(IllegalArgumentException.class,
                () -> new BooleanColumn("B", false, true, 2).changeType());

        BooleanColumn b = new BooleanColumn("B", true, null, 2);
        assertEquals(ColumnType.INTEGER, b.changeType().getColumnType());
    }

    @Test
    void testCanAcceptValuesFromSelf() {
        BooleanColumn other = new BooleanColumn("Other", true, null, 2);
        other.addDefaultValue();
        assertTrue(col.canAcceptAllValuesFrom(other));
    }

    @Test
    void testCanChangeToType() {
        col.addDefaultValue();
        col.setValue(0, null);
        assertTrue(col.canChangeToType(ColumnType.STRING));
    }
}

