package com.visualizer.asci.visualizer.model;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Data
public class Column {

    private String column_name;
    private String type;
    private boolean primary_key;
    private Set<ForeignKeyColumn> relation = new HashSet<ForeignKeyColumn>();
    public Column(String column_name, String type) {
        this.column_name = column_name;
        this.type = type;
    }

}
