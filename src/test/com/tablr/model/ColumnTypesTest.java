package com.tablr.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ColumnTypesTest {

    @Test
    void testEmailColumnValidAndInvalid() {
        EmailColumn emailCol = new EmailColumn("Email", false, "test@example.com", 1);
        emailCol.addDefaultValue();

        assertDoesNotThrow(() -> emailCol.setValue(0, "user@domain.com"));
        assertEquals("user@domain.com", emailCol.getRowValue(0));

        assertThrows(IllegalArgumentException.class, () -> emailCol.setValue(0, "nodomain"));
        assertThrows(IllegalArgumentException.class, () -> emailCol.setValue(0, "@multiple@@at.com"));
        assertThrows(IllegalArgumentException.class, () -> emailCol.setValue(0, ""));
    }

    @Test
    void testIntegerColumnValidAndInvalid() {
        IntegerColumn intCol = new IntegerColumn("Age", false, 25, 1);
        intCol.addDefaultValue();

        assertDoesNotThrow(() -> intCol.setValue(0, 42));
        assertEquals(42, intCol.getRowValue(0));

        assertThrows(IllegalArgumentException.class, () -> intCol.setValue(0, null));
    }

    @Test
    void testBooleanColumnAllowsBlankAndCycles() {
        BooleanColumn col = new BooleanColumn("Flag", true, null, 1);

        assertNull(col.getDefaultValue());
        Boolean next = col.toggleDefaultValue();
        col.changeDefaultValue(next);
        assertEquals(true, next);

        next = col.toggleDefaultValue();
        col.changeDefaultValue(next);
        assertEquals(false, next);

        next = col.toggleDefaultValue();
        col.changeDefaultValue(next);
        assertNull(next);
    }

    @Test
    void testBooleanColumnDisallowsBlank() {
        BooleanColumn col = new BooleanColumn("Flag", false, true, 1);

        Boolean next = col.toggleDefaultValue();
        col.changeDefaultValue(next);
        assertEquals(false, next);

        next = col.toggleDefaultValue();
        col.changeDefaultValue(next);
        assertEquals(true, next);
    }

    @Test
    void testBooleanColumnSetInvalidWhenBlankNotAllowed() {
        BooleanColumn col = new BooleanColumn("Flag", false, true, 1);
        col.addDefaultValue();

        assertThrows(IllegalArgumentException.class, () -> col.setValue(0, null));
    }
}
