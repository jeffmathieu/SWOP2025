package com.tablr.model;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmailColumnTest {
    EmailColumn col;

    @BeforeEach
    void setUp() {
        col = new EmailColumn("Email", true, null, 1);
    }

    @Test
    void testValidEmails() {
        assertTrue(col.isValidValue("test@example.com"));
        assertFalse(col.isValidValue("test@@example.com"));
        assertFalse(col.isValidValue("invalid email"));
        assertTrue(col.isValidValue("")); // allowsBlank = true
    }

    @Test
    void testParseValue() {
        assertEquals("x@y", col.parseValue("x@y"));
        assertThrows(IllegalArgumentException.class, () -> col.parseValue("bad email"));
        assertNull(col.parseValue(""));
    }

    @Test
    void testChangeDefaultValueFromString() {
        col.changeDefaultValueFromString("user@mail.com");
        assertEquals("user@mail.com", col.getDefaultValue());
    }

    @Test
    void testChangeType() {
        assertEquals(ColumnType.BOOLEAN, col.changeType().getColumnType());
    }

    @Test
    void testCanAcceptAllValuesFrom() {
        EmailColumn source = new EmailColumn("Source", true, null, 2);
        source.addDefaultValue();
        assertTrue(col.canAcceptAllValuesFrom(source));
    }

    @Test
    void testCanChangeToType() {
        assertTrue(col.canChangeToType(ColumnType.STRING));
        col.addDefaultValue();
        col.setValue(0, "a@b");
        assertFalse(col.canChangeToType(ColumnType.INTEGER));
    }
}

