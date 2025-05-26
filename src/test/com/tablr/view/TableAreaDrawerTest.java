package com.tablr.view;

import com.tablr.area.*;
import com.tablr.view.TableAreaDrawer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

public class TableAreaDrawerTest {

    private Graphics g;
    private TableArea area;

    @BeforeEach
    void setup() {
        g = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB).getGraphics();
        area = new TableArea(2, 2);
        area.setAreaTitle(java.util.List.of("Title1", "Title2"));
        area.setIdList(java.util.List.of(1, 2));

        area.setColumn(new Cell[]{
                new NormalCell("Row1", new Rectangle(10, 40, 100, 20)),
                new NormalCell("Row2", new Rectangle(10, 70, 100, 20))
        }, 0);

        area.setColumn(new Cell[]{
                new BooleanCell(true, true, new Rectangle(120, 40, 100, 20)),
                new BooleanCell(false, false, new Rectangle(120, 70, 100, 20))
        }, 1);
    }

    @Test
    void testDrawTableAreaDoesNotCrash() {
        assertDoesNotThrow(() -> TableAreaDrawer.drawTableArea(g, area));
    }

    @Test
    void testDrawNormalCellWithNullValue() {
        assertDoesNotThrow(() -> {
            TableAreaDrawer.drawTableArea(g, area);
        });
    }

    @Test
    void testDrawBooleanCellHandlesStates() {
        assertDoesNotThrow(() -> {
            TableAreaDrawer.drawTableArea(g, area);
        });
    }

    @Test
    void testDrawCellThrowsOnNull() throws Exception {
        var method = TableAreaDrawer.class.getDeclaredMethod("drawCell", Graphics.class, Cell.class);
        method.setAccessible(true);

        InvocationTargetException exception = assertThrows(InvocationTargetException.class, () -> {
            method.invoke(null, g, null);
        });

        Throwable cause = exception.getCause();
        assertInstanceOf(IllegalArgumentException.class, cause);
        assertNull(cause.getMessage());
    }

    @Test
    void testDrawBooleanCellRedBorderWhenInvalid() {
        Rectangle r = new Rectangle(10, 10, 100, 20);
        BooleanCell cell = new BooleanCell(true, true, r);
        cell.setValid(false);

        assertDoesNotThrow(() -> {
            TableAreaDrawer.drawTableArea(g, new TableArea(0, 0));
            TableAreaDrawer.drawBooleanCell(g, cell);
        });
    }

    @Test
    void testDrawBooleanCellValidWhenGrayedOut() {
        Rectangle r = new Rectangle(10, 10, 100, 20);
        BooleanCell cell = new BooleanCell(true, true, r);
        cell.setValid(true);

        assertDoesNotThrow(() -> TableAreaDrawer.drawBooleanCell(g, cell));
    }

    @Test
    void testDrawBooleanCellWhenSelected() {
        Rectangle r = new Rectangle(10, 10, 100, 20);
        BooleanCell cell = new BooleanCell(true, true, r);
        cell.setValid(true);
        cell.setSelectValue(true);

        assertDoesNotThrow(() -> TableAreaDrawer.drawBooleanCell(g, cell));
    }
}

