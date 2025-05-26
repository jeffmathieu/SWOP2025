package com.tablr.subwindow;

import com.tablr.controller.AppController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class SubwindowControllerTest {
    private SubwindowController controller;
    private AppController app;

    @BeforeEach
    void setUp() {
        app = new AppController();
        controller = new SubwindowController(app);
    }

    @Test
    void testKeyEvents() {
        controller.onControlEnter();
        controller.onEnter();
        controller.onEscape();
        controller.onBackspace();
        controller.onDelete();
        controller.onCharacter('x');
    }

    @Test
    void testMouseEvents() {
        controller.onClick(150, 150);
        controller.onDoubleClick(150, 150);
        controller.onMousePressed(150, 150);
        controller.onMouseDragged(160, 160);
        controller.onMouseReleased();
    }

    @Test
    void testOpenTablesSubwindow() {
        controller.onCtrlT(); // should open another TablesSubwindow
    }

    private int initTableWithColumnRowAndSubwindows(AppController app, SubwindowController controller) {
        int tableId = app.getTableIds().getFirst();

        // Add column and row
        app.addColumnToTable(tableId);
        app.addRowToTable(tableId);

        // Create and register 3 RowsSubwindows
        for (int i = 0; i < 3; i++) {
            RowsSubwindow rowsSub = new RowsSubwindow(tableId, 30, 30, controller);
            controller.addSubWindow(rowsSub);
        }

        return tableId;
    }

    @Test
    void testCloseAllOpenRowsSubwindowsWhenNoColumnsAnymoreDelete() {
        app.initializeTestApp();

        int tableId = initTableWithColumnRowAndSubwindows(app, controller);

        assertEquals(1, app.getTable(tableId).getRowCount());
        assertEquals(1, app.getTable(tableId).getColumnCount());

        // Does not throw error
        assertDoesNotThrow(() -> app.deleteColumn(tableId, 1));
        assertEquals(0, app.getTable(tableId).getRowCount());
        assertEquals(0, app.getTable(tableId).getColumnCount());
    }

    @Test
    void testCloseAllOpenRowsSubwindowsWhenNoColumnsAnymoreUndo() {
        app.initializeTestApp();

        int tableId = initTableWithColumnRowAndSubwindows(app, controller);

        assertEquals(1, app.getTable(tableId).getRowCount());
        assertEquals(1, app.getTable(tableId).getColumnCount());

        app.undo();
        // Does not throw error
        assertDoesNotThrow(() -> app.undo());
        assertEquals(0, app.getTable(tableId).getRowCount());
        assertEquals(0, app.getTable(tableId).getColumnCount());
    }

    private TablesSubwindow initTablesSubwindowWithApp(AppController app, int tableCount, SubwindowController controller) {
        for (int i = 0; i < tableCount; i++) {
            app.createTable();
        }

        TablesSubwindow sub = new TablesSubwindow(app.getTableIds(), 100, 100, controller);
        controller.addSubWindow(sub);
        return sub;
    }

    private boolean getPrivateBoolean(Object obj, String fieldName) {
        Class<?> clazz = obj.getClass();
        while (clazz != null) {
            try {
                var field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.getBoolean(obj);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            } catch (Exception e) {
                throw new RuntimeException("Failed to access field: " + fieldName, e);
            }
        }
        throw new RuntimeException("Cannot access field: " + fieldName);
    }

    @Test
    void testDraggingTablesSubwindow() {
        AppController app = new AppController() {
            @Override public int getCanvasWidth() { return 800; }
            @Override public int getCanvasHeight() { return 600; }
        };

        SubwindowController controller = new SubwindowController(app);
        TablesSubwindow sub = initTablesSubwindowWithApp(app, 15, controller);

        int originalX = sub.getX();
        int originalY = sub.getY();

        sub.onMousePressed(originalX + 5, originalY + 5);
        sub.onMouseDragged(originalX + 30, originalY + 40);

        assertEquals(originalX + 25, sub.getX());
        assertEquals(originalY + 35, sub.getY());
    }

    @Test
    void testResizingTablesSubwindow() {
        AppController app = new AppController() {
            @Override public int getCanvasWidth() { return 800; }
            @Override public int getCanvasHeight() { return 600; }
        };

        SubwindowController controller = new SubwindowController(app);
        TablesSubwindow sub = initTablesSubwindowWithApp(app, 10, controller);

        int originalWidth = sub.getWidth();
        int originalHeight = sub.getHeight();

        sub.onMousePressed(sub.getX() + sub.getWidth() - 2, sub.getY() + sub.getHeight() - 2);
        sub.onMouseDragged(sub.getX() + sub.getWidth() + 50, sub.getY() + sub.getHeight() + 30);
        sub.onMouseReleased();

        assertTrue(sub.getWidth() > originalWidth);
        assertTrue(sub.getHeight() > originalHeight);
    }

    @Test
    void testScrollbarsAppearWhenContentExceedsView() {
        AppController app = new AppController() {
            @Override public int getCanvasWidth() { return 800; }
            @Override public int getCanvasHeight() { return 600; }
        };

        SubwindowController controller = new SubwindowController(app);

        for (int i = 0; i < 50; i++) {
            app.createTable();
        }

        TablesSubwindow sub = new TablesSubwindow(app.getTableIds(), 100, 100, controller);
        controller.addSubWindow(sub);

        sub.onMousePressed(sub.getX() + sub.getWidth() - 2, sub.getY() + sub.getHeight() - 2);
        sub.onMouseDragged(sub.getX() + 120, sub.getY() + 120);
        sub.onMouseReleased();

        sub.updateTableArea();

        assertTrue(getPrivateBoolean(sub, "showVerticalScrollbar"), "Expected vertical scrollbar");
        assertTrue(getPrivateBoolean(sub, "showHorizontalScrollbar"), "Expected horizontal scrollbar");
    }
}
