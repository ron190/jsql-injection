package spring;

import com.jsql.util.LogLevelUtil;
import com.mckoi.database.control.DBController;
import com.mckoi.database.control.DBSystem;
import com.mckoi.database.control.DefaultDBConfig;
import com.mckoi.database.control.TCPJDBCServer;
import com.mckoi.debug.Lvl;
import jakarta.annotation.PreDestroy;
import org.apache.commons.lang3.StringUtils;
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
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.core.io.ClassPathResource;
import spring.rest.Student;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@SpringBootApplication(exclude = HibernateJpaAutoConfiguration.class)
@EntityScan({"spring.rest"})
public class SpringTargetApplication {

    static {
        try {  // ensure driver is loaded
            Class.forName("com.mimer.jdbc.Driver");
            Class.forName("nl.cwi.monetdb.jdbc.MonetDriver");
            Class.forName("com.mckoi.JDBCDriver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static final Logger LOGGER = LogManager.getRootLogger();
    
    private static NetworkServerControl serverDerby;
    private static org.hsqldb.server.Server serverHsqldb;
    private static Server serverH2;
    private static TCPJDBCServer serverMckoi;

    public static final Properties propsH2 = new Properties();
    public static final Properties propsMysql = new Properties();
    public static final Properties propsMysqlError = new Properties();
    public static final Properties propsPostgreSql = new Properties();
    public static final Properties propsSqlServer = new Properties();
    public static final Properties propsSqlite = new Properties();
    public static final Properties propsCubrid = new Properties();
    public static final Properties propsDb2 = new Properties();
    public static final Properties propsHsqldb = new Properties();
    public static final Properties propsDerby = new Properties();
    public static final Properties propsFirebird = new Properties();
    public static final Properties propsInformix = new Properties();
    public static final Properties propsSybase = new Properties();

    public static final List<SimpleEntry<Properties, String>> propertiesByEngine = Arrays.asList(
        new SimpleEntry<>(propsH2, "hibernate/hibernate.h2.properties"),
        new SimpleEntry<>(propsMysql, "hibernate/hibernate.mysql.properties"),
        new SimpleEntry<>(propsMysqlError, "hibernate/hibernate.mysql-5-5-40.properties"),
        new SimpleEntry<>(propsPostgreSql, "hibernate/hibernate.postgresql.properties"),
        new SimpleEntry<>(propsSqlServer, "hibernate/hibernate.sqlserver.properties"),
        new SimpleEntry<>(propsCubrid, "hibernate/hibernate.cubrid.properties"),
        new SimpleEntry<>(propsSqlite, "hibernate/hibernate.sqlite.properties"),
        new SimpleEntry<>(propsDb2, "hibernate/hibernate.db2.properties"),
        new SimpleEntry<>(propsHsqldb, "hibernate/hibernate.hsqldb.properties"),
        new SimpleEntry<>(propsDerby, "hibernate/hibernate.derby.properties"),
        new SimpleEntry<>(propsFirebird, "hibernate/hibernate.firebird.properties"),
        new SimpleEntry<>(propsInformix, "hibernate/hibernate.informix.properties"),
        new SimpleEntry<>(propsSybase, "hibernate/hibernate.sybase.properties")
    );

    static {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();

        propertiesByEngine.forEach(simpleEntry -> {
            try (InputStream inputStream = classloader.getResourceAsStream(simpleEntry.getValue())) {
                simpleEntry.getKey().load(inputStream);
            } catch (IOException e) {
                LOGGER.error(e, e);
            }
        });
    }

    public static void initializeDatabases() throws Exception {

        if (!"tests-additional".equals(System.getProperty("profileId", StringUtils.EMPTY))) {

            initializeHsqldb();
            initializeH2();
            initializeNeo4j();
            initializeDerby();
            initializeMckoi();
        }

        SpringTargetApplication.propertiesByEngine.parallelStream()
        .filter(propertyByEngine -> System.getProperty("profileId", "tests").equals(
            propertyByEngine.getKey().getProperty("jsql.profile", "tests")  // undefined by default
        ))
        .forEach(propertyByEngine -> {
            
            Configuration configuration = new Configuration();
            configuration.addProperties(propertyByEngine.getKey()).configure("hibernate/hibernate.cfg.xml");
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
                session.persist(student);
                transaction.commit();
                
            } catch (Exception e) {
                LOGGER.error(e);
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

    private static void initializeMckoi() throws SQLException, IOException {

        DefaultDBConfig config = new DefaultDBConfig(File.createTempFile("mckoi.config", null));
        config.setMinimumDebugLevel(Lvl.ERROR);

        DBSystem database;
        DBController controller = DBController.getDefault();
        boolean isDatabaseExists = controller.databaseExists(config);
        if (!isDatabaseExists) {
            database = controller.createDatabase(config, "user", "password");
        } else {
            database = controller.startDatabase(config);
        }

        serverMckoi = new TCPJDBCServer(database);
        serverMckoi.start();

        if (!isDatabaseExists) {
            try (
                Connection con = DriverManager.getConnection("jdbc:mckoi://127.0.0.1/APP", "user", "password");
                PreparedStatement pstmt = con.prepareStatement("CREATE TABLE APP.pwn (dataz text)");
                PreparedStatement pstmtinsert = con.prepareStatement("INSERT INTO APP.pwn (dataz) VALUES ('jsql data')")
            ) {
                pstmt.execute();
                pstmtinsert.executeUpdate();
            }
        }
    }

    private static void initializeH2() throws SQLException {

        serverH2 = Server.createTcpServer();
        serverH2.start();
    }

    private static void initializeNeo4j() throws IOException {
        
        String graphMovie;
        try (InputStream stream = new ClassPathResource("neo4j/movie-graph.txt").getInputStream()) {
            graphMovie = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }
        
        Driver driver = GraphDatabase.driver("bolt://jsql-neo4j:7687", AuthTokens.basic("neo4j", "test"));
        
        try (org.neo4j.driver.Session session = driver.session()) {
            
            session.run("MATCH (n) DETACH DELETE n");
            
            Result result = session.run(graphMovie);
            result.forEachRemaining(LOGGER::info);
            
        } catch (Exception e) {
            LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
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

        if (!"tests-additional".equals(System.getProperty("profileId", StringUtils.EMPTY))) {

            LOGGER.info("Ending in-memory databases...");
            serverDerby.shutdown();
            serverH2.stop();
            serverHsqldb.stop();
            serverMckoi.stop();
        }
    }
}