package com.tablr.controller;

import com.tablr.view.CanvasWindow;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * The UIController class is responsible for handling user input and rendering the UI.
 * It delegates subwindow management to the {@link AppController}, which acts as the central mediator.
 * This controller does not directly manage or store subwindows itself.
 */
public class UIController extends CanvasWindow {

    private final AppController appController;

    /**
     * Constructs a new UIController that delegates system coordination to the AppController.
     *
     * @param appController The central application controller (mediator) responsible for all coordination.
     */
    public UIController(AppController appController) {
        super("Tablr UI");
        this.appController = appController;
    }

    /**
     * Paints all current subwindows on the canvas by retrieving them via the AppController.
     *
     * @param g The Graphics context to draw with.
     */
    @Override
    public void paint(Graphics g) {
        g.setColor(new Color(221, 221, 221));
        Rectangle clip = g.getClipBounds();
        g.fillRect(clip.x, clip.y, clip.width, clip.height);
        appController.paintWindows(g);
    }

    /**
     * Handles mouse click events and delegates them to the AppController.
     *
     * @param id         Mouse event type (only MOUSE_CLICKED is handled here)
     * @param x          X-coordinate of the click
     * @param y          Y-coordinate of the click
     * @param clickCount Number of clicks (1 for single click, 2 for double click)
     */
    @Override
    public void handleMouseEvent(int id, int x, int y, int clickCount) {
        switch (id) {
            case MouseEvent.MOUSE_CLICKED -> {
                if (clickCount == 2) {
                    // Double-click: e.g. create table, add row, open table, etc.
                    appController.onDoubleClick(x, y);
                } else {
                    // Single-click: e.g. start editing, select table/column/row, etc.
                    appController.onClick(x, y);
                }
            }
            case MouseEvent.MOUSE_PRESSED -> appController.onMousePressed(x, y);
            case MouseEvent.MOUSE_DRAGGED -> appController.onMouseDragged(x, y);
            case MouseEvent.MOUSE_RELEASED -> appController.onMouseReleased();
        }
        repaint();
    }

    /**
     * Handles key events and delegates the appropriate user actions to the AppController.
     * Supports both typed characters and special key presses.
     *
     * @param id        Type of key event (KEY_TYPED or KEY_PRESSED)
     * @param keyCode   Key code (only relevant for KEY_PRESSED)
     * @param keyChar   Character typed (only relevant for KEY_TYPED)
     * @param modifiers Modifier flags (e.g. CTRL)
     */
    @Override
    public void handleKeyEvent(int id, int keyCode, char keyChar, int modifiers) {
        if (id == KeyEvent.KEY_TYPED) {
            if (Character.isLetterOrDigit(keyChar) || Character.isSpaceChar(keyChar) || "~`!@#$%^&*()_+-={}[]|\\:;\"'<>,.?/".indexOf(keyChar) != -1) {
                // Letters, numbers and space are added to edit buffer
                appController.onCharacter(keyChar);
            }
        } else if (id == KeyEvent.KEY_PRESSED) {
            switch (keyCode) {
                case KeyEvent.VK_BACK_SPACE -> appController.onBackspace();
                case KeyEvent.VK_ENTER -> {
                    if ((modifiers & KeyEvent.CTRL_DOWN_MASK) != 0) {
                        appController.onControlEnter(); // Open related subwindow
                    } else {
                        appController.onEnter(); // Confirm edit
                    }
                }
                case KeyEvent.VK_ESCAPE -> appController.onEscape(); // Cancel edit
                case KeyEvent.VK_DELETE -> appController.onDelete(); // Delete selected item
                case KeyEvent.VK_PAGE_UP -> appController.onPageUp();
                case KeyEvent.VK_PAGE_DOWN -> appController.onPageDown();
                case KeyEvent.VK_T -> {
                    if ((modifiers & KeyEvent.CTRL_DOWN_MASK) != 0) {
                        appController.onCtrlT(); // Open new Tables subwindow
                    }
                }
                case KeyEvent.VK_F -> {
                    if ((modifiers & KeyEvent.CTRL_DOWN_MASK) != 0) {
                        appController.onCtrlF(); // Open new form subwindow
                    }
                }
                case KeyEvent.VK_D -> {
                    if ((modifiers & KeyEvent.CTRL_DOWN_MASK) != 0) {
                        appController.onControlD(); // Open new Tables subwindow
                    }
                }
                case KeyEvent.VK_N -> {
                    if ((modifiers & KeyEvent.CTRL_DOWN_MASK) != 0) {
                        appController.onControlN(); // Open new Tables subwindow
                    }
                }
                case KeyEvent.VK_Z -> {
                    if ((modifiers & KeyEvent.CTRL_DOWN_MASK) != 0 && (modifiers & KeyEvent.SHIFT_DOWN_MASK) != 0) {
                        appController.redo(); // Redo (ctrl+shift+Z)
                    } else if ((modifiers & KeyEvent.CTRL_DOWN_MASK) != 0) {
                        appController.undo(); // Undo (ctrl+Z)
                    }
                }
            }
        }
        repaint();
    }

    /**
     * Retrieve height of Canvas.
     * @return height of canvas.
     */
    public int getCanvasWidth() {
        return  getWidth();
    }

    /**
     * Retrieve height of Canvas.
     * @return height of canvas.
     */
    public int getCanvasHeight() {
        return getHeight();
    }
}
