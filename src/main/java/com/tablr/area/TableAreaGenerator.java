package com.tablr.area;

import com.tablr.model.Column;
import com.tablr.model.ColumnType;
import com.tablr.model.Table;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class TableAreaGenerator {

    /**
     * Generates Tables UI TableArea object from list of tables
     *
     * @param tables
     * @return
     */
    public static TableArea GenerateTableArea(List<Table> tables) {
        TableArea tableArea = new TableArea(1, tables.size());
        tableArea.setAreaTitle(new ArrayList<>(List.of("Table Name")));
        Cell[] column = new Cell[tables.size()];
        List<Integer> ids = new ArrayList<>();
        int row = 0;
        for (Table table : tables) {
            column[row++] = new NormalCell(table.getName(), new Rectangle(20, 20 * (row+1), 100, 20));
            ids.add(table.getId());
        }
        tableArea.setColumn(column, 0);
        tableArea.setIdList(ids);
        return tableArea;
    }

    /**
     * Generates Design UI TableArea object from given table
     *
     * @param table
     * @return
     */
    public static TableArea GenerateDesignArea(Table table) {
        TableArea tableArea = new TableArea(4, table.getColumnCount());
        tableArea.setAreaTitle(asList("Column Name", "Type", "Blanks", "Default"));
        List<Integer> ids = new ArrayList<>();
        int row = 0;
        for (Column<?> column : table.getColumns()) {
            tableArea.setRow(column, row++);
            ids.add(column.getId());
        }
        tableArea.setIdList(ids);
        return tableArea;
    }

    /**
     * Generates Rows UI TableArea object from given table
     *
     * @param table
     * @return
     */
    public static TableArea GenerateRowsArea(Table table) {
        TableArea tableArea = new TableArea(table.getColumnCount(), table.getRowCount());
        List<Column<?>> columns = table.getColumns();
        List<String> titles = new ArrayList<>();
        List<Integer> ids = new ArrayList<>();
        int columnIndex = 0;
        for (Column<?> column : columns) {
            titles.add(column.getName());
            tableArea.setColumn(column, columnIndex);
            ids.add(column.getId());
            columnIndex += 1;
        }
        tableArea.setAreaTitle(titles);
        tableArea.setIdList(ids);

        return tableArea;
    }

    public static TableArea GenerateFormArea(List<String> columnNames, List<Object> values, List<ColumnType> columnTypes,List<Integer> columnIds) {
        TableArea tableArea = new TableArea(2, columnNames.size());
        tableArea.setAreaTitle(asList("Column Name", "Value"));
        Cell[] names = new Cell[columnNames.size()];
        for (int i = 0; i < columnNames.size(); i++) {
            NormalCell cell = new NormalCell(columnNames.get(i), new Rectangle(20, 40 + 30 * i, 100, 20));
            names[i] = cell;
        }
        tableArea.setColumn(names, 0);
        if (values != null) {
            Cell[] valuesArray = new Cell[values.size()];
            for (int i = 0; i < values.size(); i++) {
                switch (columnTypes.get(i)) {
                    case ColumnType.INTEGER ,ColumnType.STRING,ColumnType.EMAIL  -> {
                        if(values.get(i)==null){
                            NormalCell cell = new NormalCell(null, new Rectangle(120, 40 + 30 * i, 100, 20));
                            valuesArray[i] = cell;
                        }else {
                            NormalCell cell = new NormalCell(values.get(i).toString(), new Rectangle(120, 40 + 30 * i, 100, 20));
                            valuesArray[i] = cell;
                        }
                    }
                    case ColumnType.BOOLEAN -> {
                        BooleanCell cell = new BooleanCell((Boolean) values.get(i), null, new Rectangle(120, 40 + 30 * i, 100, 20));
                        valuesArray[i] = cell;
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + columnTypes.get(i));
                }
            }
            tableArea.setColumn(valuesArray, 1);
        }
        tableArea.setIdList(columnIds);
        return tableArea;
    }

}
