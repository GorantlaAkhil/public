package com.visualizer.asci.visualizer.model;

import lombok.Data;

import java.util.Objects;

@Data
public class ForeignKeyColumn {

    private String relationType;
    private String foreign_key_column;
    private String foreign_key_table;

    public ForeignKeyColumn() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ForeignKeyColumn that = (ForeignKeyColumn) o;
        return Objects.equals(foreign_key_column, that.foreign_key_column) && Objects.equals(foreign_key_table, that.foreign_key_table);
    }

    @Override
    public int hashCode() {
        return Objects.hash(foreign_key_column, foreign_key_table);
    }
}
