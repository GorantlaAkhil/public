package com.visualizer.asci.visualizer.parser;

import java.util.ArrayList;
import java.util.List;

public class DBQueriesGenerator {

    List<String> genQueries = new ArrayList<>();

    private DBOperations dbOperations;

    public DBQueriesGenerator(DBOperations dbOperations){
        this.dbOperations = dbOperations;
    }

    public List<String> getGenQueries() {
        return genQueries;
    }

    public void setGenQueries(List<String> genQueries) {
        this.genQueries = genQueries;
    }

    public DBOperations getDbOperations() {
        return dbOperations;
    }

    public void setDbOperations(DBOperations dbOperations) {
        this.dbOperations = dbOperations;
    }
}
