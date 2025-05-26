package com.tablr.area;

import com.tablr.model.Column;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Class to represent any type of Mode table in UI consisting of Cells
 */
public class TableArea {
    //Cells containing the titles
    private Cell[] titles;
    private List<Integer> idList;
    //Cells containing the values of the table to be displayed in current mode
    private Cell[][] table;
    private int selectedRow = -1;
    /**
     * Constructs a new TableArea without Cells
     *
     * @param columns | amount of columns to be displayed
     * @param rows    | amount of rows to be displayed
     */
    public TableArea(int columns, int rows) {
        if(columns<0 || 0 > rows) {
            throw new IllegalArgumentException("Columns/rows must be a positive integer => 0");
        }
        this.table = new Cell[columns][rows];
        this.titles = new Cell[columns];
        this.idList = new ArrayList<>();
    }

    /**
     * Set given list as this.idList
     * @param idList
     */
    public void setIdList(List<Integer> idList) {
        this.idList = new ArrayList<>(idList);
    }

    /**
     * Return id at given index
     * @param index
     * @return
     */
    public int getId(int index){
        if(index<0 || index>=idList.size()) {throw new IndexOutOfBoundsException();}
        return idList.get(index);
    }
    /**
     * Retrieves id from idList corresponding to x coordinate
     * @param x
     * @return
     */
    public int getIdFromX(int x) {
        int left = this.titles[0].region.x;
        int right = this.titles[this.titles.length - 1].region.x + this.titles[this.titles.length - 1].region.width;
        if (x < left || x > right) {
            throw new IllegalArgumentException("x out of range");
        }
        return idList.get((x - left) / 100);
    }

    /**
     * Retrieves id from idList corresponding to y coordinate
     * @param y
     * @return
     */
    public int getIdFromY(int y) {
        int top = this.table[0][0].region.y;
        int bottom = this.table[0][this.table[0].length - 1].region.y + this.table[0][this.table[0].length - 1].region.height;
        if (y < top || y > bottom) {
            throw new IllegalArgumentException("y out of range");
        }
        return idList.get((y - top) / 20);
    }

    /**
     * Retrieves row index from y coordinate
     * @param y
     * @return
     */
    public int getRowfromY(int y) {
        int top = this.table[0][0].region.y;
        int bottom = this.table[0][this.table[0].length-1].region.y + this.table[0][this.table[0].length-1].region.height;
        if (y < top || y > bottom) {
            throw new IllegalArgumentException("y out of range");
        }
        return (y-top)/20;
    }

    public int getColumnFromX(int x) {
        int left = this.titles[0].region.x;
        int right = this.titles[this.titles.length - 1].region.x + this.titles[this.titles.length - 1].region.width;
        if (x < left || x > right) {
            throw new IllegalArgumentException("x out of range");
        }
        return (x - left) / 100;
    }

    /**
     * Calculates the total width of the table area.
     *
     * @return The total width in pixels of all columns in this table area.
     */
    public int getTotalWidth() {
        return table.length * 100 + 40;
    }

    /**
     * Calculates the total height of the table area.
     *
     * @return The total height in pixels of all rows and titles in this table area.
     */
    public int getTotalHeight() {
        if (table.length == 0 || table[0].length == 0) {
            return 0;
        }
        return (table[0].length * 20) + 20 + 50;
    }


    /**
     * Retrieves id from selected row index
     * @return
     */
    public int getIdFromSelectedRowIndex(){
        return idList.get(selectedRow);
    }

    /**
     * Retrieves the array of cells containing the titles
     *
     * @return
     */
    public Cell[] getTitles() {
        return titles;
    }


    /**
     * Retrieves the table of cells containing the values of the table to be displayed
     *
     * @return
     */
    public Cell[][] getTableCells() {
        return table;
    }

    /**
     * Retrieves index of selected row or -1
     * @return
     */
    public int getSelectedRow() {
        return selectedRow;
    }

    /**
     * Retreives x coordinate 20 to the left of this TableArea
     * @return
     */
    private int leftMostX() {
        return this.titles[0].region.x - 20;
    }

    /**
     * Retrieves y coordinate corresponding to top op cell with given index
     * @param index
     * @return
     */
    private int indexToY(int index) {
        return this.table[0][0].region.y + index * 20;
    }

    /**
     * Retrieves Rectangle in margin of selected row
     * @return
     */
    public Rectangle getSelectedRowRectangle() {
        if (selectedRow == -1) {
            return null;
        } else {
            return new Rectangle(leftMostX(), indexToY(selectedRow), 20, 20);
        }
    }

    public NormalCell transferCellWithId(NormalCell cell,int id) {
        int columnIndex = this.idList.indexOf(id);
        int rowIndex = this.getRowfromY(cell.region.y);
        if(columnIndex == -1 || rowIndex == -1 || columnIndex >= this.titles.length || rowIndex >= this.table[columnIndex].length) return null;
        Rectangle newRegion = this.table[columnIndex][rowIndex].region;
        this.table[columnIndex][rowIndex] = cell;
        this.table[columnIndex][rowIndex].region = newRegion;
        return (NormalCell) this.table[columnIndex][rowIndex];
    }

    /**
     * Retrieves y coordinate under all cells in this TableArea
     * @return
     */
    public int getLowestY() {
        Cell lowest;
        if (table[0].length - 1 == -1) {
            lowest = titles[titles.length - 1];
        } else {
            lowest = table[0][table[0].length - 1];
        }
        return lowest.region.y + lowest.region.height;
    }

    /**
     * Sets bounds of all rectangles in cells of this tableArea with new origin coordinates
     * @param x
     * @param y
     */
    public void SetBounds(int x, int y) {
        y += 30;
        x += 20;
        int currentY = this.titles[titles.length - 1].region.y;
        int currentx = this.leftMostX() +20;
        int xDiff2 = x-currentx;
        int yDiff2 = y-currentY;
        for (Cell cell : titles) {
            cell.region.setBounds(cell.region.x+xDiff2, cell.region.y+yDiff2, 100, 20);
        }
        for (Cell[] column : table) {
            for (Cell cell : column) {
                if(cell != null){
                    switch (cell){
                        case NormalCell c-> c.region.setBounds(cell.region.x+xDiff2,cell.region.y+yDiff2,100,20);
                        case BooleanCell b -> {
                            b.region.setBounds(cell.region.x+xDiff2, cell.region.y+yDiff2, 100, 20);
//                            int cx = cell.region.x+xDiff2+ (100 - 12) / 2;
                            int cy =yDiff2-10;
                            b.checkBox.setBounds(b.checkBox.x+xDiff2,b.checkBox.y+yDiff2 , 12, 12);
                        }
                        default -> throw new IllegalStateException("Unexpected value: " + cell);
                    }
                }
            }
        }
    }

    /**
     * Checks if leftmargin is clicked "x and y coordinates" of a tableArea with rows
     * @param x
     * @param y
     * @return | false when no rows or not clicked margin left of a row else true
     */
    public boolean leftMarginClicked(int x, int y) {
        if(table[0].length == 0) {
            return false;
        }
        Cell topLeft = table[0][0];
        Cell bottomLeft = table[0][table[0].length - 1];
        return (topLeft.region.x - 20) <= x
                && topLeft.region.y <= y
                && (bottomLeft.region.x) >= x
                && (bottomLeft.region.y + bottomLeft.region.height) >= y;
    }

    /**
     * Sets this.selectedRow to correct row index of given coordinate
     *
     * @param x
     * @param y
     */
    public void selectRow(int x, int y) {
        if (x < leftMostX() || x >leftMostX()+20){throw new IllegalArgumentException("x must be between " +leftMostX()+ " and "+leftMostX()+20);}
        int top = this.table[0][0].region.y;
        int bottom = this.table[0][this.table[0].length - 1].region.y + this.table[0][this.table[0].length - 1].region.height;
        if (y < top || y > bottom) {
            throw new IllegalArgumentException("y out of range");
        }
        int index = (y - top) / 20;
        if (index == this.selectedRow) {
            this.selectedRow = -1;
        } else {
            this.selectedRow = (y - top) / 20;
        }
    }

    /**
     * Set this tableArea's title array to given titles
     *
     * @param titles | list containing the titles
     */
    public void setAreaTitle(List<String> titles) {
        if (titles == null || titles.size() != this.titles.length) {
            throw new IllegalArgumentException("Titles and titles must be the same length");
        }
        Cell[] titlesRow = new Cell[titles.size()];
        for (int i = 0; i < titles.size(); i++) {
            titlesRow[i] = new NormalCell(titles.get(i), new Rectangle(20 + i * 100, 20, 100, 20));
        }
        this.titles = titlesRow;
    }


    /**
     * Sets row in this.table, to be used to display Design Mode
     *
     * @param column
     * @param row
     */
    public void setRow(Column<?> column, int row) {
        if(row<0 || row>=this.table[0].length) {
            throw new IllegalArgumentException("Row must be a positive integer > 0");
        }
        if (column == null) {
            throw new IllegalArgumentException("Column cannot be null");
        }
        //name,type,blank,default
        int y = 40 + 20 * row;
        this.table[0][row]= new NormalCell(column.getName(),new Rectangle(20,y,100,20));
        this.table[1][row]= new NormalCell(column.getTypeName().toString(),new Rectangle(120,y,100,20));
        this.table[2][row]= new BooleanCell(column.allowsBlank(),false,new Rectangle(220,y,100,20));
        if(column.isBooleanColumn()) {
            this.table[3][row] =new BooleanCell((Boolean) column.getDefaultValue(),null,new Rectangle(320,y,100,20));
        }else{
            if(column.getDefaultValue()==null){
                this.table[3][row] =new NormalCell("",new Rectangle(320,y,100,20));
            }else {
                this.table[3][row] = new NormalCell(column.getDefaultValue().toString(), new Rectangle(320, y, 100, 20));
            }
        }
    }

    /**
     * Sets in this.table a new array of cells at given index
     *
     * @param column | column to create array of cells of each value stored in column
     * @param index
     */
    public void setColumn(Column<?> column, int index) {
        if (index < 0 || index > this.table.length) {
            throw new IndexOutOfBoundsException();
        }
        if(column==null || column.size()!=this.table[0].length) {
            throw new IllegalArgumentException("Column must be of same length");
        }
        Cell[] columnValues = new Cell[column.size()];
        if (column.isBooleanColumn()) {
            for (int i = 0; i < column.size(); i++) {
                columnValues[i] = new BooleanCell((Boolean) column.getRowValue(i),null,new Rectangle(20+100*index,40+20*i,100,20));
            }
        } else {
            for (int i = 0; i < column.size(); i++) {
                Object value = column.getRowValue(i);
                if(value==null){
                    columnValues[i] = new NormalCell(null,new Rectangle(20+100*index,40+20*i,100,20));
                }else{
                    columnValues[i] = new NormalCell(value.toString(),new Rectangle(20+100*index,40+20*i,100,20));
                }
            }
        }
        this.table[index] = columnValues;
    }

    /**
     * Sets in this.table given array of Cells at given index
     *
     * @param column
     * @param index
     */
    public void setColumn(Cell[] column, int index) {
        if (index < 0 || index > this.table.length) {
            throw new IndexOutOfBoundsException();
        }
        if (column.length != this.table[0].length) {
            throw new IllegalArgumentException("Column length does not match dimensions");
        }
        this.table[index] = column;
    }

    /**
     * Returns the cell which represents the table of the given tableId if it exists, otherwise null
     *
     * @param tableId | the id of the table
     * @return the cell which represents the table, or null if it does not exist
     */
    public Cell getTableCellFromTableId(int tableId) {
        for (Integer i : idList) {
            if (i == tableId) {
                return table[0][idList.indexOf(i)];
            }
        }
        return null;
    }

    public boolean isTableAreaClicked(int x, int y) {
        if(table[0].length==0) {
            return false;
        }else{
            Rectangle topLeft = table[0][0].region;
            Rectangle area = new Rectangle(topLeft.x, topLeft.y, 100*table.length, 20*table[0].length);
            return area.contains(x, y);
        }
    }

    public String getTypeFromY(int y) {
        return ((NormalCell) table[1][y]).getValue();
    }

}
