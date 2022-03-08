package com.visualizer.asci.visualizer.parser;

import com.visualizer.asci.visualizer.model.Column;
import com.visualizer.asci.visualizer.model.Table;

import java.util.List;

public interface DBOperations {

    String dbTableQueryGen(Table table);
    String dbFKQueryGen(String baseTable , String baseColumn , String referenceTable , String referenceColumn);
    List<String> dbAddQueryGen(List<Column> columns,String tableName);
    String dbRenameQueryGen(String tableName , String oldColumnName , String newColumnName , String columnType);
    String dbDropColumnQueryGen(String tableName,String ColumnName);
    String dbInsertColumnValQueryGen(String tableName,String columnName,String columnValue);
}
