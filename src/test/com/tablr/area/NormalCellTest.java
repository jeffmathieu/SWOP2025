package com.tablr.area;

import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

public class NormalCellTest {

    @Test
    void testSetAndGetValue() {
        NormalCell cell = new NormalCell("Hello", new Rectangle());
        assertEquals("Hello", cell.getValue());

        cell.setValue("World");
        assertEquals("World", cell.getValue());
    }

    @Test
    void testSetEditAndEditBufferManipulation() {
        NormalCell cell = new NormalCell("Init", new Rectangle());
        cell.setEdit();
        assertEquals("Init", cell.getEdit());
    }

    @Test
    void testAppendEditAndRemoveCharEdit() {
        NormalCell cell = new NormalCell("A", new Rectangle());
        cell.setEdit();

        cell.appendEdit('B');
        cell.appendEdit('C');
        assertEquals("A" + "BC", cell.getEdit());

        cell.removeCharEdit();
        assertEquals("AB", cell.getEdit());
    }

    @Test
    void testResetEditClearsEditAndValidation() {
        NormalCell cell = new NormalCell("Text", new Rectangle());
        cell.setEdit();
        cell.appendEdit('Z');
        cell.setValid(false);

        cell.resetEdit();
        assertEquals("", cell.getEdit());
        assertTrue(cell.isValid());
    }

    @Test
    void testValidityFlag() {
        NormalCell cell = new NormalCell("x", new Rectangle());
        assertTrue(cell.isValid());

        cell.setValid(false);
        assertFalse(cell.isValid());
    }
}

