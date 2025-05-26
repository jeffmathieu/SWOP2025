package com.tablr.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import static org.junit.Assert.assertEquals;

class UIControllerTest {
    private UIController ui;

    @BeforeEach
    void setUp() {
        ui = new UIController(new AppController());
    }

    @Test
    void testMouseClickSingle() {
        ui.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 10, 10, 1);
    }

    @Test
    void testMouseClickDouble() {
        ui.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 10, 10, 2);
    }

    @Test
    void testMousePressedAndDraggedAndReleased() {
        ui.handleMouseEvent(MouseEvent.MOUSE_PRESSED, 20, 20, 1);
        ui.handleMouseEvent(MouseEvent.MOUSE_DRAGGED, 30, 30, 1);
        ui.handleMouseEvent(MouseEvent.MOUSE_RELEASED, 30, 30, 1);
    }

    @Test
    void testKeyTypedValidCharacter() {
        ui.handleKeyEvent(KeyEvent.KEY_TYPED, 0, 'x', 0); // regular char
        ui.handleKeyEvent(KeyEvent.KEY_TYPED, 0, '@', 0); // special char
    }

    @Test
    void testKeyPressedStandardKeys() {
        ui.handleKeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.VK_BACK_SPACE, '\b', 0);
        ui.handleKeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.VK_ESCAPE, (char) 0, 0);
        ui.handleKeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.VK_DELETE, (char) 0, 0);
    }

    @Test
    void testEnterAndControlEnter() {
        ui.handleKeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.VK_ENTER, '\n', 0);
        ui.handleKeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.VK_ENTER, '\n', KeyEvent.CTRL_DOWN_MASK);
    }

    @Test
    void testCtrlT() {
        ui.handleKeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.VK_T, 'T', KeyEvent.CTRL_DOWN_MASK);
    }

    @Test
    void testFormKeys(){
        ui.handleKeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.VK_F, 'F', KeyEvent.CTRL_DOWN_MASK);
        ui.handleKeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.VK_PAGE_DOWN, (char) 0, 0);
        ui.handleKeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.VK_PAGE_UP, (char) 0, 0);
        ui.handleKeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.VK_D, 'D', KeyEvent.CTRL_DOWN_MASK);
        ui.handleKeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.VK_N, 'N', KeyEvent.CTRL_DOWN_MASK);
    }

    @Test
    void testUndoRedoKeys() {
        ui.handleKeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.VK_Z, 'Z', KeyEvent.CTRL_DOWN_MASK);
        int redoModifiers = KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK;
        ui.handleKeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.VK_Z, 'Z', redoModifiers);
    }
}
