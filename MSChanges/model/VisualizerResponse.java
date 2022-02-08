package com.visualizer.asci.visualizer.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Collection;

@Data
@NoArgsConstructor
public class VisualizerResponse {

    private String ms_name;
    private boolean active;
    private Collection<Table> metadata;

}
