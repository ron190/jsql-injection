package spring;

import com.jsql.util.LogLevelUtil;
import com.mckoi.database.control.DBController;
import com.mckoi.database.control.DBSystem;
import com.mckoi.database.control.DefaultDBConfig;
import com.mckoi.database.control.TCPJDBCServer;
import com.mckoi.debug.Lvl;
import jakarta.annotation.PreDestroy;
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
import spring.rest.StudentForDelete;

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
import java.util.stream.Stream;

@SpringBootApplication(exclude = HibernateJpaAutoConfiguration.class)
@EntityScan({"spring.rest"})
public class SpringApp {

    static {
        try {  // ensure driver is loaded
            Class.forName("com.mckoi.JDBCDriver");
            Class.forName("oracle.jdbc.OracleDriver");
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
    public static final Properties propsPostgres = new Properties();
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
        new SimpleEntry<>(SpringApp.propsH2, "hibernate/hibernate.h2.properties"),
        new SimpleEntry<>(SpringApp.propsMysql, "hibernate/hibernate.mysql.properties"),
        new SimpleEntry<>(SpringApp.propsMysqlError, "hibernate/hibernate.mysql-5-5-53.properties"),
        new SimpleEntry<>(SpringApp.propsPostgres, "hibernate/hibernate.postgres.properties"),
        new SimpleEntry<>(SpringApp.propsSqlServer, "hibernate/hibernate.sqlserver.properties"),
        new SimpleEntry<>(SpringApp.propsCubrid, "hibernate/hibernate.cubrid.properties"),
        new SimpleEntry<>(SpringApp.propsSqlite, "hibernate/hibernate.sqlite.properties"),
        new SimpleEntry<>(SpringApp.propsDb2, "hibernate/hibernate.db2.properties"),
        new SimpleEntry<>(SpringApp.propsHsqldb, "hibernate/hibernate.hsqldb.properties"),
        new SimpleEntry<>(SpringApp.propsDerby, "hibernate/hibernate.derby.properties"),
        new SimpleEntry<>(SpringApp.propsFirebird, "hibernate/hibernate.firebird.properties"),
        new SimpleEntry<>(SpringApp.propsInformix, "hibernate/hibernate.informix.properties")
    );

    static {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        SpringApp.propertiesByEngine.forEach(simpleEntry -> {
            try (InputStream inputStream = classloader.getResourceAsStream(simpleEntry.getValue())) {
                simpleEntry.getKey().load(inputStream);
            } catch (IOException e) {
                LOGGER.error(e, e);
            }
        });
    }

    public static void initDatabases() throws Exception {
        if (System.getProperty("profileId") == null || "tests".equals(System.getProperty("profileId"))) {
            SpringApp.initHsqldb();
            SpringApp.initH2();
            SpringApp.initNeo4j();
            SpringApp.initDerby();
            SpringApp.initMckoi();
        }

        SpringApp.getPropertiesFilterByProfile().forEach(propertyByEngine -> {
            Configuration configuration = new Configuration();
            configuration.addProperties(propertyByEngine.getKey()).configure("hibernate/hibernate.cfg.xml");
            configuration.addAnnotatedClass(Student.class);
            configuration.addAnnotatedClass(StudentForDelete.class);

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
                session.persist(new StudentForDelete());
                transaction.commit();
            } catch (Exception e) {
                LOGGER.error(e);
            }
        });
    }

    private static void initDerby() throws Exception {
        SpringApp.serverDerby = new NetworkServerControl();
        SpringApp.serverDerby.start(null);
    }

    private static void initHsqldb() {
        SpringApp.serverHsqldb = new org.hsqldb.server.Server();
        SpringApp.serverHsqldb.setSilent(true);
        SpringApp.serverHsqldb.setDatabaseName(0, "mainDb");
        SpringApp.serverHsqldb.setDatabasePath(0, "mem:mainDb");
        SpringApp.serverHsqldb.setPort(9002);
        SpringApp.serverHsqldb.start();
    }

    private static void initMckoi() throws SQLException, IOException {
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

        SpringApp.serverMckoi = new TCPJDBCServer(database);
        SpringApp.serverMckoi.start();

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

    private static void initH2() throws SQLException {
        SpringApp.serverH2 = Server.createTcpServer();
        SpringApp.serverH2.start();
    }

    private static void initNeo4j() throws IOException {
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

    public static Stream<SimpleEntry<Properties, String>> getPropertiesFilterByProfile() {
        return SpringApp.propertiesByEngine.parallelStream().filter(propertyByEngine ->
            System.getProperty("profileId") == null
            || propertyByEngine.getKey().getProperty("jsql.profile").equals(System.getProperty("profileId"))
        );
    }

    /**
     * For debug purpose only.
     */
    public static void main(String[] args) throws Exception {
        SpringApp.initDatabases();
        SpringApplication.run(SpringApp.class, args);
    }
    
    @PreDestroy
    public void onDestroy() throws Exception {
        if (System.getProperty("profileId") == null || "tests".equals(System.getProperty("profileId"))) {
            LOGGER.info("Ending in-memory databases...");
            SpringApp.serverDerby.shutdown();
            SpringApp.serverH2.stop();
            SpringApp.serverHsqldb.stop();
            SpringApp.serverMckoi.stop();
        }
    }
}