package com.visualizer.asci.visualizer.service;

import com.visualizer.asci.visualizer.model.Table;
import com.visualizer.asci.visualizer.model.TagDetails;
import com.visualizer.asci.visualizer.model.VisualizerResponse;
import com.visualizer.asci.visualizer.parser.LiquibaseChangesetXMLParser;
import com.visualizer.asci.visualizer.parser.LoggingFacade;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

@Service
public class VisualizerService {

    public Table getSchemaTable(String tableName){

        Map<String, Table> parsedTables = new LiquibaseChangesetXMLParser().parse(new File("C:\\Users\\Thota Narendra\\WorkDetails\\Intellij-Projects\\visualizer\\src\\main\\resources\\simpleChangeSet.xml"),null);
        return  parsedTables.get(tableName);
    }
    public Map<String, Table> getSchemaTables(){

        Map<String, Table> parsedTables = new LiquibaseChangesetXMLParser().parse(new File("C:\\Users\\Thota Narendra\\WorkDetails\\Intellij-Projects\\visualizer\\src\\main\\resources\\simpleChangeSet.xml"),null);
        return  parsedTables;
    }
    public VisualizerResponse getSchemaMetaData(){

        Map<String, Table> parsedTables = new LiquibaseChangesetXMLParser().parse(new File("C:\\Users\\Thota Narendra\\WorkDetails\\Intellij-Projects\\visualizer\\src\\main\\resources\\simpleChangeSet.xml"),null);
        VisualizerResponse visualizerResponse = new VisualizerResponse();
        visualizerResponse.setMetadata(parsedTables.values());
        populateMSDetails(visualizerResponse);
        return visualizerResponse;
    }

    private void populateMSDetails(VisualizerResponse visualizerResponse) {

        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("application.properties");
        Properties properties = new Properties();
        try {
            properties.load(inputStream);
            visualizerResponse.setMs_name(properties.getProperty("ms_name"));
            visualizerResponse.setActive(Boolean.valueOf(properties.getProperty("active")));
            visualizerResponse.setTags(populateTagDetails(properties.getProperty("tags")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private Set<TagDetails> populateTagDetails(String tagDescription) {

        Set<TagDetails> setTagDetails = new HashSet<>();
        String tags[] = tagDescription.split(",");
        for (String tag :tags) {
            String tagData[] = tag.split("-");
            TagDetails tagDetails = new TagDetails();
            tagDetails.setTagId(tagData[0]);
            tagDetails.setTagDescription(tagData[1]);
            setTagDetails.add(tagDetails);
        }

        return setTagDetails;
    }

    public Map<String, Table>  ChangeLog_TillTag_1_X_FINAL() {

        Map<String, Table> parsedTables = new LiquibaseChangesetXMLParser(new LoggingFacade() {
            @Override
            public void logIgnoredElement(String element) {

            }

            @Override
            public void logUnsupportedElement(String element) {
                System.out.println("Unsupported element '" + element + "' detected");
            }

            @Override
            public void logParsingError(File inputFile, Exception cause) {

            }
        }).parse(new File("C:\\Users\\Thota Narendra\\WorkDetails\\Intellij-Projects\\visualizer\\src\\main\\resources\\db\\db.changelog-master.xml"), "1.X.FINAL");
        return  parsedTables;
    }
}
