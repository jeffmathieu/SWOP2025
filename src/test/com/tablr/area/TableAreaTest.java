package com.tablr.area;

import com.tablr.model.Table;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TableAreaTest {

    private List<Table> tables;
    private Table table;

    @BeforeEach
    void setUp() {
        tables = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            tables.add(new Table("T" + i, i));
        }
        table = tables.getFirst();
    }

    @Test
    void testConstructorIllegalArguments() {
        assertThrows(IllegalArgumentException.class, () -> new TableArea(-1, 5));
        assertThrows(IllegalArgumentException.class, () -> new TableArea(2, -5));
    }

    @Test
    void testSetIdListAndGetIdFromX_Y() {
        table.createColumn();
        for (int i = 0; i < 5; i++) {
            table.createRow();
        }

        TableArea area = new TableArea(1, 5);
        List<Integer> ids = List.of(42, 43, 44, 45, 46);
        area.setIdList(ids);
        area.setColumn(table.getColumn(1), 0);
        area.setAreaTitle(List.of("Title"));
        area.SetBounds(0, 0);

        int cellY = area.getTableCells()[0][0].getRegion().y + 1;
        int cellX = area.getTitles()[0].getRegion().x + 1;

        assertEquals(42, area.getIdFromY(cellY));
        assertEquals(42, area.getIdFromX(cellX));

        assertThrows(IllegalArgumentException.class, () -> area.getIdFromX(0));
        assertThrows(IllegalArgumentException.class, () -> area.getIdFromY(0));
    }

    @Test
    void testSelectRow() {
        table.createColumn();
        table.createRow();

        TableArea area = new TableArea(1, 1);
        area.setColumn(table.getColumn(1), 0);
        area.setAreaTitle(List.of("Title"));
        area.SetBounds(0, 0);

        int x = area.getTitles()[0].getRegion().x - 10;
        int y = area.getTableCells()[0][0].getRegion().y + 1;

        area.selectRow(x, y);
        assertEquals(0, area.getSelectedRow());

        // Select same row again should deselect
        area.selectRow(x, y);
        assertEquals(-1, area.getSelectedRow());
    }

    @Test
    void testLeftMarginClicked() {
        table.createColumn();
        table.createRow();

        TableArea area = new TableArea(1, 1);
        area.setColumn(table.getColumn(1), 0);
        area.setAreaTitle(List.of("Title"));
        area.SetBounds(0, 0);

        int x = area.getTitles()[0].getRegion().x - 10;
        int y = area.getTableCells()[0][0].getRegion().y + 1;

        assertTrue(area.leftMarginClicked(x, y));
        assertFalse(new TableArea(1, 0).leftMarginClicked(x, y));
    }

    @Test
    void testGetSelectedRowRectangle() {
        table.createColumn();
        table.createRow();

        TableArea area = new TableArea(1, 1);
        area.setColumn(table.getColumn(1), 0);
        area.setAreaTitle(List.of("Title"));
        area.SetBounds(0, 0);

        assertNull(area.getSelectedRowRectangle());

        int x = area.getTitles()[0].getRegion().x - 10;
        int y = area.getTableCells()[0][0].getRegion().y + 1;
        area.selectRow(x, y);
        assertNotNull(area.getSelectedRowRectangle());
    }

    @Test
    void testGetRowFromYTopMiddleBottom() {
        table.createColumn();
        for (int i = 0; i < 3; i++) {
            table.createRow();
        }

        TableArea area = new TableArea(1, 3);
        area.setColumn(table.getColumn(1), 0);
        area.setAreaTitle(List.of("Header"));
        area.SetBounds(0, 0); // ensures cell regions are created

        int topY = area.getTableCells()[0][0].getRegion().y;
        int middleY = area.getTableCells()[0][1].getRegion().y + 10;

        assertEquals(0, area.getRowfromY(topY));
        assertEquals(1, area.getRowfromY(middleY));
    }


    @Test
    void testGetRowFromYThrowsForOutOfBounds() {
        table.createColumn();
        table.createRow();

        TableArea area = new TableArea(1, 1);
        area.setColumn(table.getColumn(1), 0);
        area.setAreaTitle(List.of("Header"));
        area.SetBounds(0, 0);

        int tooHigh = area.getTableCells()[0][0].getRegion().y - 1;
        int tooLow = area.getTableCells()[0][0].getRegion().y + 25; // more than 20 px height

        assertThrows(IllegalArgumentException.class, () -> area.getRowfromY(tooHigh));
        assertThrows(IllegalArgumentException.class, () -> area.getRowfromY(tooLow));
    }

    @Test
    void testSetAreaTitleInvalid() {
        TableArea area = new TableArea(1, 1);
        assertThrows(IllegalArgumentException.class, () -> area.setAreaTitle(null));
        assertThrows(IllegalArgumentException.class, () -> area.setAreaTitle(List.of("Too", "Many")));
    }

    @Test
    void testSetRowAndSetColumnByCell() {
        TableArea area = new TableArea(4, 1);
        table.createColumn();

        area.setRow(table.getColumn(1), 0);

        Cell[] cells = new Cell[1];
        cells[0] = new com.tablr.area.NormalCell("x", new Rectangle(0, 0, 100, 20));
        area.setColumn(cells, 0);

        assertThrows(IndexOutOfBoundsException.class, () -> area.setColumn(cells, -1));
        assertThrows(IllegalArgumentException.class, () -> area.setColumn(new Cell[5], 0));
    }

    @Test
    void testGetLowestY() {
        TableArea area = new TableArea(1, 0);  // No rows, test fallback
        area.setAreaTitle(List.of("T"));
        area.SetBounds(0, 0);
        assertTrue(area.getLowestY() > 0);

        table.createColumn();
        table.createRow();
        TableArea area2 = new TableArea(1, 1);
        area2.setColumn(table.getColumn(1), 0);
        area2.setAreaTitle(List.of("T"));
        area2.SetBounds(0, 0);
        assertTrue(area2.getLowestY() > 0);
    }

    @Test
    void testGetTableCellFromTableId() {
        table.createColumn();
        table.createRow();

        TableArea area = new TableArea(1, 1);
        area.setColumn(table.getColumn(1), 0);
        area.setIdList(List.of(5));
        assertNotNull(area.getTableCellFromTableId(5));
        assertNull(area.getTableCellFromTableId(999));
    }
}
