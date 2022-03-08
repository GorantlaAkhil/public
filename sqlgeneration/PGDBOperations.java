package com.visualizer.asci.visualizer.parser;

import com.visualizer.asci.visualizer.model.Column;
import com.visualizer.asci.visualizer.model.Table;

import java.util.ArrayList;
import java.util.List;

public class PGDBOperations implements DBOperations{


    @Override
    public String dbTableQueryGen(Table table){

        String createTableQuery = "CREATE TABLE ${TABLE_NAME} (${COLUMN_NAMES});" ;
        createTableQuery = createTableQuery.replace("${TABLE_NAME}",table.table_name);

        StringBuilder colmnsStr = new StringBuilder();
        List<Column> columns = table.getTable_columns();
        for (int columnCount = 0 ; columnCount < columns.size(); columnCount++){

            Column column = columns.get(columnCount);
            colmnsStr.append(column.getColumn_name() + " "+column.getType());
            if(columnCount != columns.size()-1){
                colmnsStr.append(" ,");
            }
        }
        createTableQuery = createTableQuery.replace("${COLUMN_NAMES}",
                colmnsStr.toString());
        return createTableQuery;
    }

    @Override
    public String dbFKQueryGen(String baseTable, String baseColumn, String referenceTable, String referenceColumn) {

        String createFKQuery = "ALTER TABLE ${BASE_TABLE_NAME} FOREIGN KEY (${BASE_COLUMN_NAME}) " +
                "REFERENCES ${REF_TABLE_NAME}(${REF_COLUMN_NAME});" ;
        createFKQuery = createFKQuery.replace("${BASE_TABLE_NAME}",baseTable);
        createFKQuery = createFKQuery.replace("${BASE_COLUMN_NAME}",baseColumn);
        createFKQuery = createFKQuery.replace("${REF_TABLE_NAME}",referenceTable);
        createFKQuery = createFKQuery.replace("${REF_COLUMN_NAME}",referenceColumn);
        return createFKQuery;
    }

    @Override
    public List<String> dbAddQueryGen(List<Column> columns,String table_name) {

        List<String> addQueries = new ArrayList<>();
        for (int columnCount = 0 ; columnCount < columns.size(); columnCount++){

            Column column = columns.get(columnCount);
            String createAddQuery = "ALTER TABLE ${TABLE_NAME} ADD ${COLUMN_NAME} ${COLUMN_TYPE};" ;
            createAddQuery = createAddQuery.replace("${TABLE_NAME}",table_name);
            createAddQuery = createAddQuery.replace("${COLUMN_NAME}",column.getColumn_name());
            createAddQuery = createAddQuery.replace("${COLUMN_TYPE}",column.getType());
            addQueries.add(createAddQuery);
        }
        return addQueries;
    }

    @Override
    public String dbRenameQueryGen(String tableName , String oldColumnName , String newColumnName , String columnType) {

        String createRenameQuery = "ALTER TABLE ${TABLE_NAME} RENAME ${OLD_COLUMN_NAME} TO ${NEW_COLUMN_NAME};" ;
        createRenameQuery = createRenameQuery.replace("${TABLE_NAME}",tableName);
        createRenameQuery = createRenameQuery.replace("${OLD_COLUMN_NAME}",oldColumnName);
        createRenameQuery = createRenameQuery.replace("${NEW_COLUMN_NAME}",newColumnName);
        return createRenameQuery;
    }

    @Override
    public String dbDropColumnQueryGen(String tableName, String ColumnName) {

        String createDropQuery = "ALTER TABLE ${TABLE_NAME} DROP COLUMN ${COLUMN_NAME};" ;
        createDropQuery = createDropQuery.replace("${TABLE_NAME}",tableName);
        createDropQuery = createDropQuery.replace("${COLUMN_NAME}",ColumnName);
        return createDropQuery;
    }

    @Override
    public String dbInsertColumnValQueryGen(String tableName, String columnName, String columnValue) {
        String createInsertQuery = "INSERT ${TABLE_NAME}(${COLUMN_NAME}) VALUES('${COLUMN_VALUE}');" ;
        createInsertQuery = createInsertQuery.replace("${TABLE_NAME}",tableName);
        createInsertQuery = createInsertQuery.replace("${COLUMN_NAME}",columnName);
        createInsertQuery = createInsertQuery.replace("${COLUMN_VALUE}",columnValue);
        return createInsertQuery;
    }
}
