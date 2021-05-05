package spring;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PreDestroy;

import org.apache.derby.drda.NetworkServerControl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.h2.tools.Server;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Result;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.jsql.util.LogLevel;

import spring.rest.Student;

@SpringBootApplication
public class SpringTargetApplication {
    
    private static final Logger LOGGER = LogManager.getRootLogger();
    
    private static NetworkServerControl serverDerby;
    private static org.hsqldb.server.Server serverHsqldb;
    private static Server serverH2;

    public static Properties propsH2 = new Properties();
    public static Properties propsH2Api = new Properties();
    public static Properties propsMysql = new Properties();
    public static Properties propsMysqlError = new Properties();
    public static Properties propsPostgres = new Properties();
    public static Properties propsSqlServer = new Properties();
    public static Properties propsSqlite = new Properties();
    public static Properties propsCubrid = new Properties();
    public static Properties propsDb2 = new Properties();
    public static Properties propsHsqldb = new Properties();
    public static Properties propsDerby = new Properties();

    static {
        
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();

        Stream
        .of(
            new SimpleEntry<>(propsH2, "hibernate/hibernate.h2.properties"),
            new SimpleEntry<>(propsMysql, "hibernate/hibernate.mysql.properties"),
            new SimpleEntry<>(propsMysqlError, "hibernate/hibernate.mysql-5-5-40.properties"),
            new SimpleEntry<>(propsPostgres, "hibernate/hibernate.postgres.properties"),
            new SimpleEntry<>(propsSqlServer, "hibernate/hibernate.sqlserver.properties"),
            new SimpleEntry<>(propsCubrid, "hibernate/hibernate.cubrid.properties"),
            new SimpleEntry<>(propsSqlite, "hibernate/hibernate.sqlite.properties"),
            new SimpleEntry<>(propsDb2, "hibernate/hibernate.db2.properties"),
            new SimpleEntry<>(propsHsqldb, "hibernate/hibernate.hsqldb.properties"),
            new SimpleEntry<>(propsDerby, "hibernate/hibernate.derby.properties")
        )
        .forEach(simpleEntry -> {
            
            try (InputStream inputStream = classloader.getResourceAsStream(simpleEntry.getValue())) {
                
                simpleEntry.getKey().load(inputStream);
                
            } catch (IOException e) {
                
                e.printStackTrace();
            }
        });
    }

    public static void initializeDatabases() throws Exception {
        
        initializeHsqldb();
        initializeH2();
        initializeNeo4j();
        initializeDerby();
        
        ArrayList<Properties> properties = new ArrayList<>(
            Arrays.asList(
                SpringTargetApplication.propsH2,
                SpringTargetApplication.propsMysql,
                SpringTargetApplication.propsMysqlError,
                SpringTargetApplication.propsPostgres,
                SpringTargetApplication.propsSqlServer,
                SpringTargetApplication.propsCubrid,
                SpringTargetApplication.propsSqlite,
                SpringTargetApplication.propsDb2,
                SpringTargetApplication.propsHsqldb,
                SpringTargetApplication.propsDerby
            )
        );
        
        properties
        .parallelStream()
        .forEach(props -> {
            
            Configuration configuration = new Configuration();
            configuration.addProperties(props).configure("hibernate/hibernate.cfg.xml");
            configuration.addAnnotatedClass(Student.class);
            
            StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
            
            try (
                SessionFactory factory = configuration.buildSessionFactory(builder.build());
                Session session = factory.openSession()
            ) {
                Transaction transaction = session.beginTransaction();
                Student student = new Student();
                student.setAge(1);
                student.setFirstName("firstName");
                student.setClassName("className");
                student.setLastName("lastName");
                student.setRollNo("rollNo");
                session.save(student);
                transaction.commit();
                
            } catch (Exception e) {
                // Ignore
            }
        });
    }

    private static void initializeDerby() throws Exception {
        
        serverDerby = new NetworkServerControl();
        serverDerby.start(null);
    }

    private static void initializeHsqldb() {
        
        serverHsqldb = new org.hsqldb.server.Server();
        serverHsqldb.setSilent(true);
        serverHsqldb.setDatabaseName(0, "mainDb");
        serverHsqldb.setDatabasePath(0, "mem:mainDb");
        serverHsqldb.setPort(9002);
        serverHsqldb.start();
    }

    private static void initializeH2() throws SQLException {
        
        serverH2 = Server.createTcpServer();
        serverH2.start();
    }

    private static void initializeNeo4j() throws IOException {
        
        String graphMovie = Files.readAllLines(Paths.get("src/test/resources/neo4j/movie-graph.txt")).stream().collect(Collectors.joining("\n"));
        
        Driver driver = GraphDatabase.driver("bolt://jsql-neo4j:7687", AuthTokens.basic("neo4j", "test"));
        
        try (org.neo4j.driver.Session session = driver.session()) {
            
            session.run("MATCH (n) DETACH DELETE n");
            
            Result result = session.run(graphMovie);
            result.forEachRemaining(record -> {
                
                LOGGER.info(record);
            });
            
        } catch (Exception e) {
            
            LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
        }
        
        driver.close();
    }

    /**
     * For debug purpose only.
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        
        initializeDatabases();
        
        SpringApplication.run(SpringTargetApplication.class, args);
    }
    
    @PreDestroy
    public void onDestroy() throws Exception {
        
        LOGGER.info("Ending in-memory databases...");
        
        serverDerby.shutdown();
        serverH2.stop();
        serverHsqldb.stop();
    }
}