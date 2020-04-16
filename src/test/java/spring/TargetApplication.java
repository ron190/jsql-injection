package spring;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.AbstractMap.SimpleEntry;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
//import org.neo4j.driver.AuthTokens;
//import org.neo4j.driver.Driver;
//import org.neo4j.driver.GraphDatabase;
//import org.neo4j.driver.Result;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import spring.model.Student;

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

    static {
        
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();

        Stream.of(
            new SimpleEntry<>(propsH2, "spring/hibernate.h2.properties"),
            new SimpleEntry<>(propsMysql, "spring/hibernate.mysql.properties"),
            new SimpleEntry<>(propsMysqlError, "spring/hibernate.mysql-5.5.40.properties"),
            new SimpleEntry<>(propsPostgres, "spring/hibernate.postgres.properties"),
            new SimpleEntry<>(propsSqlServer, "spring/hibernate.sqlserver.properties"),
            new SimpleEntry<>(propsSqlite, "spring/hibernate.sqlite.properties")
        )
        .forEach(simpleEntry -> {
            
            try (InputStream inputStream = classloader.getResourceAsStream(simpleEntry.getValue())) {
                
                simpleEntry.getKey().load(inputStream);
                
            } catch (IOException e) {
                
                e.printStackTrace();
            }
        });
    }

    public static void initializeDatabases() throws IOException {
        
        initializeNeo4j();
        
        Stream.of(
            propsH2,
            propsMysql,
            propsMysqlError,
            propsPostgres,
            propsSqlServer,
            propsSqlite
        )
        .forEach(props -> {
            
            Configuration configuration = new Configuration();
            configuration.addProperties(props).configure("spring/hibernate.cfg.xml");
            configuration.addAnnotatedClass(Student.class);
            
            StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
            
            try (
                SessionFactory factory = configuration.buildSessionFactory(builder.build());
                Session session = factory.openSession()
            ) {
                Transaction transaction = session.beginTransaction();
                Student student = new Student();
                student.setAge(1);
                session.save(student);
                transaction.commit();
            }
        });
    }

    private static void initializeNeo4j() throws IOException {
        
//        String graphMovie = Files.readAllLines(Paths.get("src/test/resources/docker/movie-graph.txt")).stream().collect(Collectors.joining("\n"));
//        
//        Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "test"));
//        
//        try (org.neo4j.driver.Session session = driver.session()) {
//            
//            session.run("MATCH (n) DETACH DELETE n");
//            
//            Result result = session.run(graphMovie);
//            result.forEachRemaining(record -> {
//                
//                System.out.println(record);
//            });
//            
//        } catch (Exception e) {
//            LOGGER.error(e, e);
//        }
//        
//        driver.close();
    }

    /**
     * For debug purpose only.
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        
        initializeDatabases();
        
        SpringApplication.run(TargetApplication.class, args);
    }
}