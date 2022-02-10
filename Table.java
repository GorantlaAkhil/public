package com.visualizer.asci.visualizer.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ToString
@EqualsAndHashCode
@Getter
public class Table {

    public final String table_name;
    public final List<Column> table_columns;

    public Table(String table_name) {
        this.table_name = table_name;
        this.table_columns = new ArrayList<>();
    }

    public Column add(String name, String type) {
        Column column = new Column(name, type);
        this.table_columns.add(column);
        return column;
    }


   /* public List<Column> getColums() {
        return Collections.unmodifiableList(table_columns);
    }

    public Map<String, Column> getColumnsAsMap() {
        return getTable_columns().stream().collect(Collectors.toMap(Column::getColumn_name, c -> c));
    }*/

    public void removeColumn(String columnName) {

        Map<String, Column> columnsAsMap = getTable_columns().stream().collect(Collectors.toMap(Column::getColumn_name, c -> c));
        Column column = columnsAsMap.get(columnName);
        this.table_columns.remove(column);
    }

}
