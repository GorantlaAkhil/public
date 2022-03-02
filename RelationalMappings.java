package com.visualizer.asci.visualizer.model;

public enum RelationalMappings {

    ONE_TO_ONE("ONE_TO_ONE"),ONE_TO_MANY("ONE_TO_MANY");

    RelationalMappings(String relationCode) {
        this.relationCode = relationCode;
    }

    String relationCode;
}
