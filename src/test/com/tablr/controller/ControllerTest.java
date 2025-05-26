package com.tablr.controller;

import com.tablr.model.Table;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ControllerTest {
    static class DummyController extends Controller {
        public DummyController(Table table) {
            super(table);
        }
    }

    @Test
    void testSetCurrentTable() {
        Table t1 = new Table("Test1", 1);
        Table t2 = new Table("Test2", 2);
        DummyController ctrl = new DummyController(t1);
        ctrl.setCurrentTable(t2);
        assertEquals("Test2", ctrl.table.getName());
    }
}

