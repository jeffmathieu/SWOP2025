package com.tablr.area;

import java.awt.*;

/**
 * Type of cell to display boolean values
 */
public class BooleanCell extends Cell {
    private Boolean grayedOut;
    protected Rectangle checkBox;
    private boolean valid = true;

    /**
     * Constructs a new BooleanCell with given boolean and rectangle
     *
     * @param selected  |
     * @param grayedOut |
     * @param region    |
     */
    public BooleanCell(Boolean selected,Boolean grayedOut,Rectangle region) {
        super(region,selected);
        this.grayedOut = grayedOut;

        // 20 x 100 cell
        int checkBoxSize = 12;
        int x = region.x + (region.width - checkBoxSize) / 2;
        int y = region.y + (region.height - checkBoxSize) -3;

        this.checkBox = new Rectangle(x, y, checkBoxSize, checkBoxSize);
    }

    /**
     * Retrieves Boolean value stored in cell
     * @return | True if grayed out, false otherwise.
     */
    public Boolean isGrayedOut() {return grayedOut;}

    /**
     * Sets grayout to opposite of what it was.
     */
    public void grayOut() {
        this.grayedOut = !this.grayedOut;
    }

    @Override
    public void setValid(boolean valid) {
        this.valid = valid;
    }

    /**
     * Retrieves if boolean cell is valid.
     * @return | True if valid, False otherwise.
     */
    public boolean isValid() {
        return valid;
    }

    @Override
    public boolean isToggleableDefaultCell() {
        return true;
    }
    /**
     * Retrieves Rectangle representing area of checkbox
     * @return
     */
    public Rectangle getCheckBox() {
        return checkBox;
    }

    /**
     * Checks if given coordinates are in checkbox rectangle
     * @param x
     * @param y
     * @return
     */
    public boolean isCheckBoxClicked(int x, int y) {
        return checkBox.contains(x, y);
    }

    /**
     * Sets given Boolean in abstract Cell class field "selected"
     * @param next
     */
    public void setSelectValue(Boolean next) {
        this.selected = next;
    }
}
