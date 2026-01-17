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
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import spring.rest.Student;
import spring.rest.StudentForDelete;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;

@SpringBootApplication(exclude = {
    UserDetailsServiceAutoConfiguration.class  // remove password warning at startup
})
@EntityScan({"spring.rest"})
public class SpringApp {

    public static final String TEST_PROFILE = "testProfile";
    public static final String TEST_PROFILE_MAIN = "Main";
    public static final String TENANT = "tenant";
    public static final String TENANT_H2 = "h2";

    static {
        try {  // ensure driver is loaded, required static for expected jdbc result
            Class.forName("virtuoso.jdbc3.Driver");
            Class.forName("com.mimer.jdbc.Driver");
            Class.forName("com.mckoi.JDBCDriver");
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static final Logger LOGGER = LogManager.getRootLogger();
    
    private static NetworkServerControl serverDerby;
    private static org.hsqldb.server.Server serverHsqldb;
    private static Server serverH2;
    private static TCPJDBCServer serverMckoi;

    public static final List<Properties> propertiesByEngine;

    static {
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] hibernateProperties = resolver.getResources("classpath:hibernate/hibernate.*.properties");
            propertiesByEngine = Arrays.stream(hibernateProperties).map(SpringApp::getProperties).toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static Properties getProperties(Resource resource) {
        try {
            var properties = new Properties();
            properties.load(resource.getInputStream());
            return properties;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void initDatabases() throws Exception {
        LOGGER.info("Current testProfile: {}", System.getProperty(SpringApp.TEST_PROFILE));
        SpringApp.initH2();
        if (SpringApp.TEST_PROFILE_MAIN.equals(System.getProperty(SpringApp.TEST_PROFILE))) {
            SpringApp.initHsqldb();
            SpringApp.initNeo4j();
            SpringApp.initDerby();
            SpringApp.initMckoi();
        }

        SpringApp.getPropertiesFilterByProfile().forEach(propertyByEngine -> {
            LOGGER.info("Configuring {} hibernate entities...", propertyByEngine.getProperty(SpringApp.TENANT));
            Configuration configuration = new Configuration()
                .addProperties(propertyByEngine)
                .configure("hibernate/hibernate.cfg.xml")
                .addAnnotatedClass(Student.class)
                .addAnnotatedClass(StudentForDelete.class);

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
                LOGGER.error(e, e);
            }
        });
    }

    private static void initDerby() throws Exception {
        LOGGER.info("Starting Derby...");
        SpringApp.serverDerby = new NetworkServerControl();
        SpringApp.serverDerby.start(null);
    }

    private static void initHsqldb() {
        LOGGER.info("Starting Hsqldb...");
        SpringApp.serverHsqldb = new org.hsqldb.server.Server();
        SpringApp.serverHsqldb.setSilent(true);
        SpringApp.serverHsqldb.setDatabaseName(0, "mainDb");
        SpringApp.serverHsqldb.setDatabasePath(0, "mem:mainDb");
        SpringApp.serverHsqldb.setPort(9002);
        SpringApp.serverHsqldb.start();
    }

    private static void initMckoi() throws SQLException, IOException {
        LOGGER.info("Starting Mckoi...");
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
                Connection connection = DriverManager.getConnection("jdbc:mckoi://127.0.0.1/APP", "user", "password");
                PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE APP.pwn (dataz text)");
                PreparedStatement prepareStatementInsert = connection.prepareStatement("INSERT INTO APP.pwn (dataz) VALUES ('jsql data')")
            ) {
                preparedStatement.execute();
                prepareStatementInsert.executeUpdate();
            }
        }
    }

    private static void initH2() throws SQLException {
        LOGGER.info("Starting H2...");
        SpringApp.serverH2 = Server.createTcpServer();
        SpringApp.serverH2.start();
    }

    private static void initNeo4j() throws IOException {
        LOGGER.info("Starting Neo4j...");
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

    public static Stream<Properties> getPropertiesFilterByProfile() {
        return SpringApp.propertiesByEngine.parallelStream().filter(propertyByEngine ->
            System.getProperty(SpringApp.TEST_PROFILE).equals(propertyByEngine.getProperty(SpringApp.TEST_PROFILE))
            || SpringApp.TENANT_H2.equals(propertyByEngine.getProperty(SpringApp.TENANT))
        );
    }

    public static void main(String[] args) throws Exception {
        SpringApp.initDatabases();
        SpringApplication.run(SpringApp.class, args);
    }

    public static Properties get(String tenant) {
        return SpringApp.propertiesByEngine.stream()
            .filter(p -> p.get(SpringApp.TENANT).equals(tenant))
            .findFirst()
            .get();
    }
    
    @PreDestroy
    public void onDestroy() throws Exception {
        if (System.getProperty(SpringApp.TEST_PROFILE) == null || SpringApp.TEST_PROFILE_MAIN.equals(System.getProperty(SpringApp.TEST_PROFILE))) {
            LOGGER.info("Ending in-memory databases...");
            SpringApp.serverDerby.shutdown();
            SpringApp.serverH2.stop();
            SpringApp.serverHsqldb.stop();
            SpringApp.serverMckoi.stop();
        }
    }
}