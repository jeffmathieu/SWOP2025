package com.tablr.view;

import com.tablr.area.BooleanCell;
import com.tablr.area.Cell;
import com.tablr.area.NormalCell;
import com.tablr.area.TableArea;

import java.awt.*;

/**
 * Utility class for rendering a TableArea and its cells.
 * Provides methods to draw the table area, its title, and individual cells.
 */
public class TableAreaDrawer {

    /**
     * Draws the entire table area, including its title and table cells.
     *
     * @param g The Graphics object used for rendering.
     * @param tableArea The TableArea to be drawn.
     */
    public static void drawTableArea(Graphics g, TableArea tableArea) {
        drawTableAreaTitle(g, tableArea);
        drawTableAreaTable(g, tableArea);
    }

    /**
     * Draws a single cell based on its type (NormalCell or BooleanCell).
     *
     * @param g The Graphics object used for rendering.
     * @param cell The cell to be drawn.
     * @throws IllegalArgumentException If the cell is null.
     * @throws IllegalStateException If the cell type is unexpected.
     */
    private static void drawCell(Graphics g, Cell cell) {
        switch (cell) {
            case null -> throw new IllegalArgumentException();
            case NormalCell c -> drawNormalCell(g, c);
            case BooleanCell c -> drawBooleanCell(g, c);
            default -> throw new IllegalStateException("Unexpected value: " + cell);
        }
    }

    /**
     * Draws a NormalCell, including its selection state and validity.
     *
     * @param g The Graphics object used for rendering.
     * @param cell The NormalCell to be drawn.
     */
    private static void drawNormalCell(Graphics g, NormalCell cell) {
        Rectangle region = cell.getRegion();
        g.setColor(Color.white);
        g.fillRect(region.x, region.y, region.width, region.height);
        g.setColor(Color.BLACK);
        if (cell.isCellSelected()) {
            if (cell.isValid()) {
                g.drawRect(region.x, region.y, region.width, region.height);
                g.drawString(cell.getEdit() + "|", region.x, region.y + 15);
            } else {g.setColor(Color.RED);
                g.fillRect(region.x, region.y, region.width, region.height);
                g.setColor(Color.BLACK);
                g.drawRect(region.x, region.y, region.width, region.height);
                g.drawString(cell.getEdit() + "|", region.x + 2, region.y + 15);
            }
        }else{
            if(cell.isValid()){
                g.drawRect(region.x,region.y,region.width,region.height);
                if(cell.getValue() == null){
                    g.drawString("",region.x,region.y+15 );
                }else{
                    g.drawString(cell.getValue(),region.x,region.y+15 );
                }
            }else{
                g.setColor(new Color(166, 56, 48));
                g.drawRect(region.x,region.y,region.width,region.height);
                g.setColor(Color.BLACK);
                g.drawString(cell.getValue(),region.x,region.y+15 );
            }
        }

    }

    /**
     * Draws a BooleanCell, including its selection state and grayed-out state.
     *
     * @param g The Graphics object used for rendering.
     * @param cell The BooleanCell to be drawn.
     */
    public static void drawBooleanCell(Graphics g, BooleanCell cell) {
        Rectangle region = cell.getRegion();
        g.setColor(Color.white);
        g.fillRect(region.x, region.y, region.width, region.height);
        g.setColor(Color.BLACK);
        g.drawRect(region.x, region.y, region.width, region.height);
        Rectangle checkBox = cell.getCheckBox();
        // rows mode is grayed out altijd null!!!
        if (cell.isGrayedOut() == null) {
            if (cell.isCellSelected() == null) {
                g.setColor(Color.DARK_GRAY);
                g.fillRect(region.x, region.y, region.width, region.height);
            } else if (cell.isCellSelected()) {
                g.drawLine(checkBox.x, checkBox.y, checkBox.x + checkBox.width, checkBox.y + checkBox.height);
                g.drawLine(checkBox.x, checkBox.y + checkBox.height, checkBox.x + checkBox.width, checkBox.y);
            }
        }else{
            // fix voor design mode
            if(cell.isGrayedOut()){
                //dit is bij invalid state
                //red border
                g.setColor(Color.RED);
                g.drawRect(region.x,region.y,region.width,region.height);
            }
            if(!cell.isGrayedOut()){
                if(!cell.isValid()){
                    g.setColor(new Color(166, 56, 48));
                    g.fillRect(region.x, region.y, region.width, region.height);
                }}
            if(cell.isCellSelected()){
                g.setColor(Color.BLACK);
                g.drawLine(checkBox.x,checkBox.y,checkBox.x + checkBox.width,checkBox.y + checkBox.height);
                g.drawLine(checkBox.x, checkBox.y + checkBox.height, checkBox.x + checkBox.width, checkBox.y);
            }
        }
        g.setColor(Color.BLACK);
        g.drawRect(checkBox.x,checkBox.y,checkBox.width,checkBox.height);
    }

    /**
     * Draws the title cells of the TableArea.
     *
     * @param g The Graphics object used for rendering.
     * @param tableArea The TableArea containing the title cells.
     */
    private static void drawTableAreaTitle(Graphics g, TableArea tableArea) {
        if(tableArea != null){
            Cell[] titles = tableArea.getTitles();
            for (Cell cell : titles) {
                if(cell != null){
                    drawCell(g, cell);
                }

            }
        }

    }

    /**
     * Draws the table cells of the TableArea, including the selected row if applicable.
     *
     * @param g The Graphics object used for rendering.
     * @param tableArea The TableArea containing the table cells.
     */
    private static void drawTableAreaTable(Graphics g, TableArea tableArea) {
        if(tableArea != null){
            Cell[][] table = tableArea.getTableCells();
            for (Cell[] column : table) {
                for (Cell cell : column) {
                    if(cell != null){
                        drawCell(g, cell);
                    }
                }
            }
            if (tableArea.getSelectedRow() != -1) {
                Rectangle selected = tableArea.getSelectedRowRectangle();
                g.setColor(Color.RED);
                g.fillRect(selected.x, selected.y, selected.width, selected.height);
            }
        }

    }
}
