package com.tablr.area;

import com.tablr.model.Table;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TableAreaGeneratorTest {

    @Test
    void testGenerateTableArea() {
        Table t1 = new Table("T1", 1);
        Table t2 = new Table("T2", 2);
        TableArea area = TableAreaGenerator.GenerateTableArea(List.of(t1, t2));

        assertNotNull(area.getTableCells());
        assertEquals(2, area.getTableCells()[0].length);
        assertEquals("T1", ((com.tablr.area.NormalCell) area.getTableCells()[0][0]).getValue());
    }

    @Test
    void testGenerateDesignArea() {
        Table t = new Table("Design", 3);
        t.createColumn();
        TableArea area = TableAreaGenerator.GenerateDesignArea(t);

        assertEquals(4, area.getTableCells().length);
        assertEquals(t.getColumns().size(), area.getTableCells()[0].length);
    }

    @Test
    void testGenerateRowsArea() {
        Table t = new Table("Rows", 4);
        t.createColumn();
        t.createColumn();
        t.createRow();
        TableArea area = TableAreaGenerator.GenerateRowsArea(t);

        assertEquals(2, area.getTableCells().length);
        assertEquals(1, area.getTableCells()[0].length);
        assertNotNull(area.getTitles());
    }
}

