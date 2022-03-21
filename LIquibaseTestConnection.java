import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.integration.ant.type.ChangeLogOutputFile;
import liquibase.resource.ClassLoaderResourceAccessor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Set;

public class LIquibaseTestConnection {

    private Set<ChangeLogOutputFile> changeLogOutputFiles = new LinkedHashSet<>();
    private boolean includeSchema = true;
    private boolean includeCatalog = true;
    private boolean includeTablespace = true;

    public static void main(String[] args) throws ClassNotFoundException, LiquibaseException, SQLException, IOException {


        //liquibaseUpdate();
        liquibaseUpdateSql();
    }

    private static void liquibaseUpdateSql() throws ClassNotFoundException, SQLException, LiquibaseException, IOException {
        Connection connection = openConnection();
        Database database =  DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
        Liquibase liquibase = new Liquibase("master.xml", new ClassLoaderResourceAccessor(), database);
        //liquibase.update(1,new Contexts(), new LabelExpression(),new FileWriter("test.sql"));
        liquibase.update(new Contexts(),new FileWriter("test.sql"));

    }
    private static void liquibaseUpdate() throws ClassNotFoundException, SQLException, LiquibaseException {
        Connection connection = openConnection();
        Database database =  DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
        Liquibase liquibase = new Liquibase("master.xml", new ClassLoaderResourceAccessor(), database);
        liquibase.update(new Contexts(), new LabelExpression());
    }

    private static Connection openConnection() throws ClassNotFoundException, SQLException {

        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection con =  DriverManager.getConnection("jdbc:mysql://localhost:3306/sample","narendra","narendra");
        return con;
    }

}
