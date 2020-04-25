package spring;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.AbstractMap.SimpleEntry;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.derby.drda.NetworkServerControl;
import org.apache.log4j.Logger;
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

import spring.rest.Student;

@SpringBootApplication
public class TargetApplication {
    
    /**
     * Using default log4j.properties from root /
     */
    private static final Logger LOGGER = Logger.getRootLogger();

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
    public static Properties propsOracle = new Properties();

    static {
        
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();

        Stream.of(
            new SimpleEntry<>(propsH2, "hibernate/hibernate.h2.properties"),
            new SimpleEntry<>(propsMysql, "hibernate/hibernate.mysql.properties"),
            new SimpleEntry<>(propsMysqlError, "hibernate/hibernate.mysql-5.5.40.properties"),
            new SimpleEntry<>(propsPostgres, "hibernate/hibernate.postgres.properties"),
            new SimpleEntry<>(propsSqlServer, "hibernate/hibernate.sqlserver.properties"),
            new SimpleEntry<>(propsCubrid, "hibernate/hibernate.cubrid.properties"),
            new SimpleEntry<>(propsSqlite, "hibernate/hibernate.sqlite.properties"),
            new SimpleEntry<>(propsDb2, "hibernate/hibernate.db2.properties"),
            new SimpleEntry<>(propsHsqldb, "hibernate/hibernate.hsqldb.properties"),
            new SimpleEntry<>(propsDerby, "hibernate/hibernate.derby.properties"),
            new SimpleEntry<>(propsOracle, "hibernate/hibernate.oracle.properties")
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
        
        Stream
        .of(
            propsCubrid,
            propsH2,
            propsMysql,
            propsMysqlError,
            propsPostgres,
            propsSqlServer,
            propsDb2,
            propsHsqldb,
            propsDerby,
            propsOracle,
            propsSqlite
        )
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
                
                LOGGER.error(e.getMessage(), e);
            }
        });
    }

    private static void initializeDerby() throws Exception {
        
        NetworkServerControl server = new NetworkServerControl();
        server.start(null);
    }

    private static void initializeHsqldb() {
        
        org.hsqldb.server.Server server = new org.hsqldb.server.Server();
        server.setSilent(true);
        server.setDatabaseName(0, "mainDb");
        server.setDatabasePath(0, "mem:mainDb");
        server.start();
    }

    private static void initializeH2() throws SQLException {
        
        Server.createTcpServer().start();
    }

    private static void initializeNeo4j() throws IOException {
        
        String graphMovie = Files.readAllLines(Paths.get("src/test/resources/data/movie-graph.txt")).stream().collect(Collectors.joining("\n"));
        
        Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "test"));
        
        try (org.neo4j.driver.Session session = driver.session()) {
            
            session.run("MATCH (n) DETACH DELETE n");
            
            Result result = session.run(graphMovie);
            result.forEachRemaining(record -> {
                
                System.out.println(record);
            });
            
        } catch (Exception e) {
            LOGGER.error(e, e);
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
        
        SpringApplication.run(TargetApplication.class, args);
    }
}