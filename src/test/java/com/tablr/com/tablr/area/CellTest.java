package com.tablr.area;

import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

public class CellTest {

    static class DummyCell extends Cell {
        public DummyCell(Rectangle r, boolean selected) {
            super(r, selected);
        }

        @Override
        public void setValid(boolean valid) {

        }
    }

    @Test
    void testSelectCellTogglesState() {
        DummyCell cell = new DummyCell(new Rectangle(10, 10, 50, 20), false);
        assertFalse(cell.isCellSelected());

        cell.selectCell();
        assertTrue(cell.isCellSelected());

        cell.selectCell();
        assertFalse(cell.isCellSelected());
    }

    @Test
    void testIsCellSelectedReturnsCorrectState() {
        DummyCell cell = new DummyCell(new Rectangle(), true);
        assertTrue(cell.isCellSelected());
    }

    @Test
    void testRegionIsStoredCorrectly() {
        Rectangle r = new Rectangle(5, 5, 20, 20);
        DummyCell cell = new DummyCell(r, true);
        assertEquals(r, cell.getRegion());
    }
}

