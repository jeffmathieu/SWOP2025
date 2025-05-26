package com.tablr.area;

import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

public class BooleanCellTest {

    @Test
    void testInitialGrayedOutState() {
        Rectangle rect = new Rectangle(0, 0, 10, 10);
        BooleanCell cell = new BooleanCell(true, false, rect);

        assertFalse(cell.isGrayedOut());
        assertTrue(cell.isCellSelected());
        assertEquals(rect, cell.getRegion());
    }

    @Test
    void testGrayOutTogglesState() {
        BooleanCell cell = new BooleanCell(false, false, new Rectangle());
        assertFalse(cell.isGrayedOut());

        cell.grayOut();
        assertTrue(cell.isGrayedOut());

        cell.grayOut();
        assertFalse(cell.isGrayedOut());
    }

    @Test
    void testIsCheckBoxClickedTrueWhenInside() {
        Rectangle cellRegion = new Rectangle(100, 100, 100, 20);
        BooleanCell cell = new BooleanCell(true, false, cellRegion);

        // Center of the checkbox area
        int clickX = cellRegion.x + 50;
        int clickY = cellRegion.y + 10;

        assertTrue(cell.isCheckBoxClicked(clickX, clickY));
    }

    @Test
    void testIsCheckBoxClickedFalseWhenOutside() {
        Rectangle cellRegion = new Rectangle(100, 100, 50, 30);
        BooleanCell cell = new BooleanCell(true, false, cellRegion);

        // Outside the checkbox
        int clickX = cellRegion.x + cellRegion.width + 10;
        int clickY = cellRegion.y + cellRegion.height + 10;

        assertFalse(cell.isCheckBoxClicked(clickX, clickY));
    }

    @Test
    void testSetSelectValueUpdatesSelection() {
        BooleanCell cell = new BooleanCell(false, false, new Rectangle());

        assertFalse(cell.isCellSelected());

        cell.setSelectValue(true);
        assertTrue(cell.isCellSelected());

        cell.setSelectValue(false);
        assertFalse(cell.isCellSelected());
    }
}
