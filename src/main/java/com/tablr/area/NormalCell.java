package com.tablr.area;

import java.awt.*;
import java.util.Objects;

/**
 * type of cell to display strings, can store a temporary value while editing the cell
 */
public class NormalCell extends Cell {
    private String value;

    // Value to be displayed while editing
    private String edit = "";
    private Boolean valid = true;

    /**
     * Constructs a NormalCell
     * @param value | Value to be displayed in UI
     * @param region
     */
    public NormalCell(String value, Rectangle region) {
        super(region,false);
        this.value = Objects.requireNonNullElse(value, "");
    }

    /**
     * Retrieves this cell's value
     * @return
     */
    public String getValue() {
        return value;
    }

    /**
     * Retrieves Boolean value "valid" stored in cell
     * @return
     */
    public Boolean isValid() {
        return valid;
    }

    /**
     * Sets given Boolean in this cell's valid field
     * @param valid
     */
    @Override
    public void setValid(boolean valid) {
        this.valid = valid;
    }

    /**
     * Set this cell's value
     * @param value | new value to update Cell
     */
    public void setValue(String value) {
        this.value = Objects.requireNonNullElse(value, "");
    }

    /**
     * Copy value string to edit string
     */
    public void setEdit() {
        this.edit = this.value;
    }

    /**
     * Retrieves value while editing
     * @return
     */
    public String getEdit() {return edit;}

    /**
     * Adds given char to "edit" string
     * @param c
     */
    public void appendEdit(Character c) {this.edit += c;}

    /**
     * Resets this.edit to empty string and restores valid state
     */
    public void resetEdit() {
        this.edit = "";
        this.valid = true;
    }

    /**
     * Removes one character from "edit" string if non-empty string
     */
    public void removeCharEdit() {
        if(!edit.isEmpty()){
            int n = this.edit.length() -1;
            this.edit = this.edit.substring(0, n);
        }
    }

    @Override
    public boolean isEditableDefaultCell() {
        return true;
    }
}
