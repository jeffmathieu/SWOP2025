package com.tablr.subwindow;

import com.tablr.area.TableArea;
import com.tablr.view.TableAreaDrawer;

import java.awt.*;

/**
 * Represents an abstract subwindow in the application.
 * Provides common functionality for managing subwindow behavior, such as dragging, resizing, and rendering.
 */
public abstract class Subwindow {
    protected int x, y, width, height;
    protected boolean isFocused = false;
    protected final int titleBarHeight = 25;
    public final SubwindowController parentWindow;

    protected String title;
    protected boolean acceptsInput = true;

    private boolean dragging = false;
    private int dragOffsetX, dragOffsetY;

    private boolean resizing = false;
    private static final int RESIZE_MARGIN = 10;
    public TableArea tableArea;

    //Scrollbar
    protected int scrollX = 0;
    protected int scrollY = 0;
    private boolean showVerticalScrollbar = false;
    private boolean showHorizontalScrollbar = false;
    private boolean draggingVerticalScrollbar = false;
    private boolean draggingHorizontalScrollbar = false;
    private int lastMouseY, lastMouseX;


    /**
     * Constructs a Subwindow instance with the specified parameters.
     *
     * @param title        The title of the subwindow.
     * @param x            The x-coordinate of the subwindow.
     * @param y            The y-coordinate of the subwindow.
     * @param width        The width of the subwindow.
     * @param height       The height of the subwindow.
     * @param parentWindow The parent controller managing this subwindow.
     */
    public Subwindow(String title, int x, int y, int width, int height, SubwindowController parentWindow) {
        this.title = title;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.parentWindow = parentWindow;
    }

    /**
     * Retrieves x-coordinate of the subwindow.
     *
     * @return x-coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * Retrieves y-coordinate of the subwindow.
     *
     * @return y-coordinate
     */
    public int getY() {
        return y;
    }

    /**
     * Retrieves width of subwindow.
     *
     * @return width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Retrieves height of subwindow.
     *
     * @return height
     */
    public int getHeight() {
        return height;
    }

    /**
     * Updates the table area associated with the subwindow.
     * This method must be implemented by subclasses.
     */
    public abstract void updateTableArea();

    /**
     * Retrieves the ID of the table associated with the subwindow.
     *
     * @return The table ID.
     */
    public abstract int getTableId();
    /**
     * Updates the position of the table area within the subwindow.
     */
    protected void updateTableAreaPositions() {
        int yOffset = y;
        int xOffset = x;

        int contentWidth = tableArea.getTotalWidth();
        int contentHeight = tableArea.getTotalHeight();
        int viewWidth = width - (showVerticalScrollbar ? 10 : 0);
        int viewHeight = height - titleBarHeight - (showHorizontalScrollbar ? 10 : 0);
        int maxScrollX = Math.max(0, contentWidth - viewWidth);
        int maxScrollY = Math.max(0, contentHeight - viewHeight);
        scrollX = Math.min(scrollX, maxScrollX);
        scrollY = Math.min(scrollY, maxScrollY);

        showHorizontalScrollbar = contentWidth > viewWidth;
        showVerticalScrollbar = contentHeight > viewHeight;

        tableArea.SetBounds(xOffset, yOffset);
    }


    /**
     * Converts a string to an Integer.
     *
     * @param string The string to convert.
     * @return The Integer value, or null if the string is null or empty.
     */
    protected Integer myStringToInt(String string) {
        if (string == null || string.isEmpty()) {
            return null;
        } else {
            return Integer.parseInt(string);
        }
    }

    /**
     * Checks if a string represents a valid integer.
     *
     * @param str The string to check.
     * @return True if the string is a valid integer or empty, false otherwise.
     */
    protected boolean isIntString(String str) {
        if (str == null || str.isEmpty()) return true;
        try {
            Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    /**
     * Draws the subwindow, including its frame, title bar, content, resize handle and scrollbars.
     *
     * @param g The Graphics object used for rendering.
     */
    public void draw(Graphics g) {
        drawFrame(g);
        drawTitleBar(g);
        drawContent(g);
        drawScrollbars(g);
        drawResizeHandle(g);
    }

    /**
     * Draws the frame of the subwindow.
     *
     * @param g The Graphics object used for rendering.
     */
    private void drawFrame(Graphics g) {
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(x, y, width, height);
        g.setColor(Color.BLACK);
        g.drawRect(x, y, width, height);
    }

    /**
     * Draws the title bar of the subwindow.
     *
     * @param g The Graphics object used for rendering.
     */
    private void drawTitleBar(Graphics g) {
        int availableWidth = width - 50;
        String fullTitle = getTitle() + (isFocused ? " [Active]" : "");

        g.setColor(isFocused ? new Color(113, 78, 150) : new Color(188, 125, 225));
        g.fillRect(x, y, width, titleBarHeight);

        g.setColor(Color.BLACK);
        g.drawRect(x, y, width, titleBarHeight);

        FontMetrics fm = g.getFontMetrics();
        String displayedTitle = fullTitle;
        while (fm.stringWidth(displayedTitle) > availableWidth && !displayedTitle.isEmpty()) {
            displayedTitle = displayedTitle.substring(0, displayedTitle.length() - 1);
        }
        if (!displayedTitle.equals(fullTitle)) {
            displayedTitle = displayedTitle + "...";
        }

        g.drawString(displayedTitle, x + 10, y + 17);

        g.drawRect(x + width - 25, y + 5, 15, 15);
        g.drawString("X", x + width - 21, y + 17);
    }

    /**
     * Draws the scrollbars of the subwindow.
     *
     * @param g The Graphics object used for rendering.
     */
    private void drawScrollbars(Graphics g) {
        int contentWidth = tableArea.getTotalWidth();
        int contentHeight = tableArea.getTotalHeight();
        int viewWidth = width - (showVerticalScrollbar ? 10 : 0);
        int viewHeight = height - titleBarHeight - (showHorizontalScrollbar ? 10 : 0);

        if (showVerticalScrollbar) {
            int thumbHeight = Math.max(30, viewHeight * viewHeight / contentHeight);
            int maxScrollY = contentHeight - viewHeight;
            int thumbY = (maxScrollY > 0) ? y + titleBarHeight + scrollY * (viewHeight - thumbHeight) / maxScrollY : y + titleBarHeight;

            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(x + width - 10, y + titleBarHeight, 10, viewHeight);
            g.setColor(Color.DARK_GRAY);
            g.fillRect(x + width - 10, thumbY, 10, thumbHeight);
        }

        if (showHorizontalScrollbar) {
            int thumbWidth = Math.max(30, viewWidth * viewWidth / contentWidth);
            int maxScrollX = contentWidth - viewWidth;
            int thumbX = (maxScrollX > 0) ? x + scrollX * (viewWidth - thumbWidth) / maxScrollX : x;

            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(x, y + height - 10, viewWidth, 10);
            g.setColor(Color.DARK_GRAY);
            g.fillRect(thumbX, y + height - 10, thumbWidth, 10);
        }
    }



    /**
     * Draws the content of the subwindow.
     * This method must be implemented by subclasses.
     *
     * @param g The Graphics object used for rendering.
     */
    protected void drawContent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setClip(x + 1, y + titleBarHeight + 1, width - (showVerticalScrollbar ? 10 : 0) - 2, height - titleBarHeight - (showHorizontalScrollbar ? 10 : 0) - 2);
        g2.translate(-scrollX, -scrollY);
        TableAreaDrawer.drawTableArea(g2, tableArea);
        g2.dispose();
    }

    /**
     * Draws the resize handle of the subwindow.
     *
     * @param g The Graphics object used for rendering.
     */
    private void drawResizeHandle(Graphics g) {
        g.setColor(Color.DARK_GRAY);
        g.drawLine(x + width - 10, y + height - 1, x + width - 1, y + height - 10);
        g.drawLine(x + width - 5, y + height - 1, x + width - 1, y + height - 5);
    }

    /**
     * Checks if a point is inside the subwindow.
     *
     * @param mx The x-coordinate of the point.
     * @param my The y-coordinate of the point.
     * @return True if the point is inside the subwindow, false otherwise.
     */
    public boolean isInside(int mx, int my) {
        return mx >= x && mx <= x + width && my >= y && my <= y + height;
    }

    /**
     * Checks if a point is inside the title bar of the subwindow.
     *
     * @param mx The x-coordinate of the point.
     * @param my The y-coordinate of the point.
     * @return True if the point is inside the title bar, false otherwise.
     */
    public boolean isInsideTitleBar(int mx, int my) {
        return mx >= x && mx <= x + width && my >= y && my <= y + titleBarHeight;
    }

    /**
     * Checks if a point is on the close button of the subwindow.
     *
     * @param mx The x-coordinate of the point.
     * @param my The y-coordinate of the point.
     * @return True if the point is on the close button, false otherwise.
     */
    public boolean isClickOnClose(int mx, int my) {
        return mx >= x + width - 25 && mx <= x + width - 10 &&
                my >= y + 5 && my <= y + 20;
    }

    /**
     * Sets the focus state of the subwindow.
     *
     * @param isFocused True to focus the subwindow, false to unfocus it.
     */
    public void setFocused(boolean isFocused) {
        this.isFocused = isFocused;
    }

    /**
     * Checks if the subwindow is focused.
     *
     * @return True if the subwindow is focused, false otherwise.
     */
    public boolean isFocused() {
        return isFocused;
    }

    /**
     * Retrieves the title of the subwindow.
     *
     * @return The title of the subwindow.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Handles the Control+Enter key event.
     * This method must be implemented by subclasses.
     */
    public abstract void onControlEnter();

    /**
     * Handles the Control+F key event.
     * This method must be implemented by subclasses.
     */
    public abstract void onControlF();

    /**
     * Handles the Enter key event.
     * This method must be implemented by subclasses.
     */
    public abstract void onEnter();

    /**
     * Handles the Escape key event.
     * This method must be implemented by subclasses.
     */
    public abstract void onEscape();

    /**
     * Handles the Backspace key event.
     * This method must be implemented by subclasses.
     */
    public abstract void onBackspace();

    /**
     * Handles the Delete key event.
     * This method must be implemented by subclasses.
     */
    public abstract void onDelete();

    /**
     * Handles character input events.
     * This method must be implemented by subclasses.
     *
     * @param keyChar The character input.
     */
    public abstract void onCharacter(char keyChar);

    /**
     * Handles mouse press events.
     *
     * @param mx The x-coordinate of the mouse press.
     * @param my The y-coordinate of the mouse press.
     */
    public void onMousePressed(int mx, int my) {
        if (isInsideTitleBar(mx, my)) {
            dragging = true;
            dragOffsetX = mx - x;
            dragOffsetY = my - y;
        } else if (isInResizeCorner(mx, my)) {
            resizing = true;
            dragOffsetX = mx - (x + width);
            dragOffsetY = my - (y + height);
        } else if (isOnVerticalScrollbar(mx, my)) {
            draggingVerticalScrollbar = true;
            lastMouseY = my;
        } else if (isOnHorizontalScrollbar(mx, my)) {
            draggingHorizontalScrollbar = true;
            lastMouseX = mx;
        }

    }

    /**
     * Handles mouse drag events.
     *
     * @param mx The x-coordinate of the mouse drag.
     * @param my The y-coordinate of the mouse drag.
     */
    public void onMouseDragged(int mx, int my) {
        if (dragging) {
            int newX = mx - dragOffsetX;
            int newY = my - dragOffsetY;

            int canvasWidth = parentWindow.getCanvasWidth();
            int canvasHeight = parentWindow.getCanvasHeight();

            x = Math.max(0, Math.min(newX, canvasWidth - width));
            y = Math.max(0, Math.min(newY, canvasHeight - titleBarHeight));
        } else if (resizing) {
            width = Math.max(100, mx - x - dragOffsetX);
            height = Math.max(100, my - y - dragOffsetY);
            //scrollbar after resizing
            int contentWidth = tableArea.getTotalWidth();
            int contentHeight = tableArea.getTotalHeight();
            int viewWidth = width - (showVerticalScrollbar ? 10 : 0);
            int viewHeight = height - titleBarHeight - (showHorizontalScrollbar ? 10 : 0);
            int maxScrollX = Math.max(0, contentWidth - viewWidth);
            int maxScrollY = Math.max(0, contentHeight - viewHeight);
            scrollX = Math.min(scrollX, maxScrollX);
            scrollY = Math.min(scrollY, maxScrollY);
        } else if (draggingVerticalScrollbar) {
            int deltaY = my - lastMouseY;
            scrollY = Math.max(0, Math.min(scrollY + deltaY, tableArea.getTotalHeight() - (height - titleBarHeight)));
            lastMouseY = my;
        } else if (draggingHorizontalScrollbar) {
            int deltaX = mx - lastMouseX;
            scrollX = Math.max(0, Math.min(scrollX + deltaX, tableArea.getTotalWidth() - (width - (showVerticalScrollbar ? 10 : 0))));
            lastMouseX = mx;
        }
    }

    /**
     * Handles mouse release events.
     */
    public void onMouseReleased() {
        dragging = false;
        resizing = false;
        draggingVerticalScrollbar = false;
        draggingHorizontalScrollbar = false;
    }

    /**
     * Checks if a point is in the resize corner of the subwindow.
     *
     * @param mx The x-coordinate of the point.
     * @param my The y-coordinate of the point.
     * @return True if the point is in the resize corner, false otherwise.
     */
    private boolean isInResizeCorner(int mx, int my) {
        return mx >= x + width - RESIZE_MARGIN && mx <= x + width &&
                my >= y + height - RESIZE_MARGIN && my <= y + height;
    }

    /**
     * Checks if a point is in the vertical scrollbar of the subwindow.
     *
     * @param mx The x-coordinate of the point.
     * @param my The y-coordinate of the point.
     * @return True if the point is in the vertical scrollbar, false otherwise.
     */
    private boolean isOnVerticalScrollbar(int mx, int my) {
        return mx >= x + width - 10 && mx <= x + width && my >= y + titleBarHeight && my <= y + height;
    }

    /**
     * Checks if a point is in the horizontal scrollbar of the subwindow.
     *
     * @param mx The x-coordinate of the point.
     * @param my The y-coordinate of the point.
     * @return True if the point is in the horizontal scrollbar, false otherwise.
     */
    private boolean isOnHorizontalScrollbar(int mx, int my) {
        return my >= y + height - 10 && my <= y + height && mx >= x && mx <= x + width - 10;
    }


    /**
     * Handles click events within the subwindow.
     *
     * @param x The x-coordinate of the click.
     * @param y The y-coordinate of the click.
     */
    public void onClick(int x, int y) {
        if (isClickOnClose(x, y)) {
            // Close the window
            // This should be handled by the parent window or controller
            parentWindow.removeSubWindow(this);
        } else {
            // Handle content area click
            onBodyClick(x, y);
        }
    }

    /**
     * Handles click events within the body of the subwindow.
     * This method must be implemented by subclasses.
     *
     * @param x The x-coordinate of the click.
     * @param y The y-coordinate of the click.
     */
    public abstract void onBodyClick(int x, int y);

    /**
     * Handles double-click events within the subwindow.
     * This method must be implemented by subclasses.
     *
     * @param x The x-coordinate of the double click.
     * @param y The y-coordinate of the double click.
     */
    public abstract void onDoubleClick(int x, int y);

    /**
     * Handles the PageUp key event.
     */
    public void onPageUp() {}

    /**
     * Handles the PageDown key event.
     */
    public void onPageDown() {}

    /**
     * Handles the Control+D key event.
     */
    public void onControlD(){}

    /**
     * Handles the Control+N key event.
     */
    public void onControlN(){}
}