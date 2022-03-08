package com.visualizer.asci.visualizer.parser;

import com.visualizer.asci.visualizer.model.Column;
import com.visualizer.asci.visualizer.model.ForeignKeyColumn;
import com.visualizer.asci.visualizer.model.RelationalMappings;
import com.visualizer.asci.visualizer.model.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Getter(AccessLevel.PROTECTED)
public class LiquibaseChangesetXMLSqlParser implements LiquibaseChangesetParser, ILiquibaseParseChangelogFile {

    private LoggingFacade loggingFacade;
    public static final String TABLE_NAME = "tableName";
    public static final String COLUMN = "column";
    private static final String COLUMN_NAME = "columnName";
    private final SAXBuilder saxBuilder;
    @Setter(AccessLevel.PACKAGE)
    private Element currentElement;
    private final Map<String, Table> parsedTables;
    private LiquibaseParseChangelogFileRules decisionTableRules;
    private boolean finishParsing;
    private File masterFile;
    private String tillTag;
    private DBQueriesGenerator dbQueriesGenerator;

    public LiquibaseChangesetXMLSqlParser() {
        this(new HashMap<>(), new LoggingFacade() {
            @Override
            public void logIgnoredElement(String element) {
                log.info(String.format(LOG_MESSAGE_IGNORED_ELEMENT, element));
            }

            @Override
            public void logUnsupportedElement(String element) {
                log.warn(String.format(LOG_MESSAGE_UNSUPPORTED_ELEMENT, element));
            }

            @Override
            public void logParsingError(File inputFile, Exception cause) {
                log.error(String.format(LOG_ERROR_ON_PARSING_FILE, inputFile.getPath(), cause.getMessage()), cause);
            }
        },new DBQueriesGenerator(new PGDBOperations()));
    }

    public LiquibaseChangesetXMLSqlParser(LoggingFacade loggingFacade) {
        this(new HashMap<>(), loggingFacade,new DBQueriesGenerator(new PGDBOperations()));
    }

    private LiquibaseChangesetXMLSqlParser(Map<String, Table> parsedTables, LoggingFacade loggingFacade,DBQueriesGenerator dbQueriesGenerator) {
        this.loggingFacade = loggingFacade;
        this.saxBuilder = new SAXBuilder();
        this.parsedTables = parsedTables;
        this.decisionTableRules = new LiquibaseParseChangelogFileRules();
        this.dbQueriesGenerator = dbQueriesGenerator;
    }

    private boolean parse(File inputFile, String tillTag, Element rootElement) {
        this.masterFile = inputFile;
        this.tillTag = tillTag;
        List<Element> elementList = rootElement.getChildren();
        for (Element element : elementList) {
            setCurrentElement(element);
            getDecisionTableRules().execute(this);
            if (isFinishParsing()) {
                break;
            }
        }
        return isFinishParsing();
    }

    public List<String> parse(File inputFile, DBQueriesGenerator dbQueriesGenerator ) {
        this.masterFile = inputFile;
        this.dbQueriesGenerator = dbQueriesGenerator;
        try {
            Document rootDocument = getSaxBuilder().build(getMasterFile());
            List<Element> listOfElements = rootDocument.getRootElement().getChildren();
            for (Element listOfElement : listOfElements) {
                setCurrentElement(listOfElement);
                getDecisionTableRules().execute(this);
                if (isFinishParsing()) {
                    break;
                }
            }
        } catch (JDOMException | IOException e) {
            getLoggingFacade().logParsingError(getMasterFile(), e);
        }
        return this.dbQueriesGenerator.getGenQueries();
    }


    private Table extractTable(Namespace namespace, Element tableElement) {
        String tableName = tableElement.getAttributeValue(TABLE_NAME);
        Table table = new Table(tableName);
        List<Element> columns = tableElement.getChildren(COLUMN, namespace);
        for (Element columnElement : columns) {
            extractColumn(table, namespace, columnElement);
        }
        return table;
    }

    private void extractColumn(Table table, Namespace namespace, Element columnElement) {
        String name = columnElement.getAttributeValue("name");
        String type = columnElement.getAttributeValue("type");
        Column column = table.add(name, type);
        Element constraints = columnElement.getChild("constraints", namespace);
        if (constraints != null) {
            column.setPrimary_key(Boolean.parseBoolean(constraints.getAttributeValue("primaryKey")));
        }


    }

    private Namespace getNamespace4Liquibase(Element rootElement) {
        return rootElement.getNamespace();
    }

    @Override
    public boolean isElementEqualCreateTable() {
        return isElementNameEqual("createTable");
    }

    @Override
    public boolean isElementEqualInclude() {
        return isElementNameEqual("include");
    }

    @Override
    public boolean isElementEqualChangeSet() {
        return isElementNameEqual("changeSet");
    }

    @Override
    public void doExtractTable() {
        Table table = extractTable(getNamespace4Liquibase(getCurrentElement()), getCurrentElement());
        getParsedTables().put(table.getTable_name(), table);
        List<String> queries = getDbQueriesGenerator().getGenQueries();
        queries.add(this.dbQueriesGenerator.getDbOperations().dbTableQueryGen(table));
    }

    @Override
    public void doFollowInclude() {
        String fileName = getCurrentElement().getAttributeValue("file");
        LiquibaseChangesetXMLSqlParser parser = new LiquibaseChangesetXMLSqlParser(getParsedTables(), getLoggingFacade(),new DBQueriesGenerator(new PGDBOperations()));
        parser.parse(new File(getMasterFile().getParentFile(), fileName), getTillTag());
        this.finishParsing = parser.isFinishParsing();
    }

    @Override
    public void doLogUnsupportedElement() {
        getLoggingFacade().logUnsupportedElement(getCurrentElement().getAttributeValue("tag"));
    }

    @Override
    public void doTrace(String dtName, String version, int rules, int rule) {
        log.trace("DecisionTable.doTrace," +dtName +"," + version + "," + rules + ","+rule);
    }

    @Override
    public void doTraceAfterRule(String dtName, String version, int rules, int rule) {
        log.trace("DecisionTable.doTraceAfterRule," +dtName +"," + version + "," + rules + ","+rule);
    }

    @Override
    public void doParseChangeSet() {
        this.finishParsing = new LiquibaseChangesetXMLSqlParser(getParsedTables(), getLoggingFacade(),getDbQueriesGenerator()).parse(getMasterFile(),
                getTillTag(), getCurrentElement());
    }

    @Override
    public boolean isElementEqualAddPrimaryKey() {
        return isElementNameEqual("addPrimaryKey");
    }

    @Override
    public void doSetPrimaryKeyForColumn() {
        String tableName = getCurrentElement().getAttributeValue(TABLE_NAME);
        String columnName = getCurrentElement().getAttributeValue("columnNames");
        getParsedTables().get(tableName).getTable_columns().stream().filter(c -> c.getColumn_name().equals(columnName)).findFirst().ifPresent(c -> c.setPrimary_key(true));
    }

    @Override
    public boolean isElementEqualDropTable() {
        return isElementNameEqual("dropTable");
    }

    private boolean isElementNameEqual(String elementName) {
        return getCurrentElement().getName().equals(elementName);
    }

    @Override
    public void doDropTable() {
        String tableName = getCurrentElement().getAttributeValue(TABLE_NAME);
        getParsedTables().remove(tableName);
    }

    @Override
    public boolean isElementEqualPreConditions() {
        return isElementNameEqual("preConditions");
    }

    @Override
    public void doIgnoreElement() {
        log.debug("Ignore liquibase element '" + getCurrentElement().getName() +"'");
    }

    @Override
    public boolean isElementEqualSql() {
        return isElementNameEqual("sql");
    }

    @Override
    public boolean isElementEqualCreateSequence() {
        return isElementNameEqual("createSequence");
    }

    @Override
    public boolean isElementEqualCreateProcedure() {
        return isElementNameEqual("createProcedure");
    }

    @Override
    public boolean isElementEqualRenameColumn() {
        return isElementNameEqual("renameColumn");
    }

    @Override
    public boolean isElementEqualComment() {
        return isElementNameEqual("comment");
    }

    @Override
    public void doRenameColumn() {
        String oldColumnName = getCurrentElement().getAttributeValue("oldColumnName");
        String newColumnName = getCurrentElement().getAttributeValue("newColumnName");
        String columnDataType = getCurrentElement().getAttributeValue("columnDataType");
        String tableName = getCurrentElement().getAttributeValue("tableName");

        List<String> queries = getDbQueriesGenerator().getGenQueries();
        queries.add(this.dbQueriesGenerator.getDbOperations().dbRenameQueryGen(tableName,oldColumnName,newColumnName,columnDataType));


    }


    private Table getTableBasedOnCurrentElementAttributeValue(String attributeName) {
        return getParsedTables().get(getCurrentElement().getAttributeValue(attributeName));
    }
    @Override
    public boolean isElementEqualAddColumn() {
        return isElementNameEqual("addColumn");
    }

    @Override
    public boolean isElementEqualDropColumn() {
        return isElementNameEqual("dropColumn");
    }

    @Override
    public void doAddColumn() {

        List<Element> columns = getCurrentElement().getChildren(COLUMN, getCurrentElement().getNamespace());
        List<Column> columnList = new ArrayList<>();
        String tableName = getCurrentElement().getAttributeValue("tableName");
        for (Element column : columns) {
            String name = column.getAttributeValue("name");
            String type = column.getAttributeValue("type");
            columnList.add(new Column(name,type));
        }
        List<String> queries = getDbQueriesGenerator().getGenQueries();
        queries.addAll(this.dbQueriesGenerator.getDbOperations().dbAddQueryGen(columnList,tableName));
    }

    @Override
    public void doDropColumn() {

        String tableName = getCurrentElement().getAttributeValue(TABLE_NAME);
        String columnName = getCurrentElement().getAttributeValue(COLUMN_NAME);
        List<String> queries = getDbQueriesGenerator().getGenQueries();
        queries.add(this.dbQueriesGenerator.getDbOperations().dbDropColumnQueryGen(tableName,columnName));
    }

    @Override
    public boolean isElementEqualRollback() {
        return isElementNameEqual("rollback");
    }

    @Override
    public boolean isElementEqualDropSequence() {
        return isElementNameEqual("dropSequence");
    }

    @Override
    public boolean isElementEqualAddUniqueConstraint() {
        return isElementNameEqual("addUniqueConstraint");
    }

    @Override
    public boolean isElementEqualAddForeignKeyConstraint() {
        return isElementNameEqual("addForeignKeyConstraint");
    }


    @Override
    public void doAddForeignKeyConstraint() {

        String baseTableName = getCurrentElement().getAttributeValue("baseTableName");
        String baseColumnName = getCurrentElement().getAttributeValue("baseColumnNames");
        String referencedTableName = getCurrentElement().getAttributeValue("referencedTableName");
        String referencedColumnName = getCurrentElement().getAttributeValue("referencedColumnNames");
        List<String> queries = getDbQueriesGenerator().getGenQueries();
        queries.add(this.dbQueriesGenerator.getDbOperations().dbFKQueryGen(baseTableName,baseColumnName,referencedTableName,referencedColumnName));
    }

    @Override
    public boolean isElementEqualTagDatabase() {
        return isElementNameEqual("tagDatabase");
    }

    @Override
    public boolean isTagEqualTillTag() {
        return getTillTag() != null && getTillTag().equals(getCurrentElement().getAttributeValue("tag"));
    }

    @Override
    public void doSkipTag() {
        this.finishParsing = false;
        getLoggingFacade().logIgnoredElement(getCurrentElement().getAttributeValue("tag"));

    }

    @Override
    public void doFinishParsing() {
        this.finishParsing = true;
    }

    @Override
    public Map<String, Table> parse(File inputFile, String tillTag) {
        return null;
    }

    @Override
    public boolean isElementEqualInsertColumnValue() {
        return isElementNameEqual("insert");
    }

    @Override
    public void doInsertColumnValue() {

        String tableName = getCurrentElement().getAttributeValue("tableName");
        List<Element> columns = getCurrentElement().getChildren(COLUMN, getCurrentElement().getNamespace());
        for (Element column : columns) {
            String columnName = column.getAttributeValue("name");
            String columnValue = column.getAttributeValue("value");
            List<String> queries = getDbQueriesGenerator().getGenQueries();
            queries.add(this.dbQueriesGenerator.getDbOperations().dbInsertColumnValQueryGen(tableName,columnName,columnValue));
        }

    }
}
