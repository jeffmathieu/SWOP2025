package com.tablr.area;

import java.awt.*;

/**
 * abstract to represent a cell in UI, contains a Rectangle
 */
public abstract class Cell {
    protected Rectangle region;
    protected Boolean selected;

    /**
     * Constructs a new Cell
     * @param region contains the location to check if this cell is clicked
     */
    protected Cell(Rectangle region,Boolean selected) {
        this.region = region;
        this.selected = selected;
    }

    public void selectCell(){
        selected = !selected;
    }

    /**
     * Checks if this cell value is selected
     */
    public Boolean isCellSelected() {
        return selected;
    }

    /**
     * Retrives this cell's region
     */
    public Rectangle getRegion() {
        return region;
    }

    /**
     * Sets valid property of a cell to given value.
     * @param valid | new valid value of cell.
     */
    public abstract void setValid(boolean valid);

    /**
     * Checks if cell is a toggleable default cell.
     * @return | true or false
     */
    public boolean isToggleableDefaultCell() {
        return false;
    }

    /**
     * Checks if cell is editable.
     * @return | true or false.
     */
    public boolean isEditableDefaultCell() {
        return false;
    }

    /**
     * Resets edit of the cell.
     */
    public void resetEdit() {}

}
