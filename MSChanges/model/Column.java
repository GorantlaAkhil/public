package com.visualizer.asci.visualizer.model;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@Data
public class Column {

    private String column_name;
    private String type;
    private boolean primary_key;
    private String foreign_key_column;
    private String foreign_key_table;

    public Column(String column_name, String type) {
        this.column_name = column_name;
        this.type = type;
    }

}
