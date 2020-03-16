package spring;

import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractMap.SimpleEntry;
import java.util.Properties;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import model.Student;

@SpringBootApplication
public class TargetApplication {
    
    /**
     * Using default log4j.properties from root /
     */
    protected static final Logger LOGGER = Logger.getRootLogger();

    static Properties propsH2 = new Properties();
    static Properties propsH2Api = new Properties();
    static Properties propsMysql = new Properties();
//    static Properties propsMysqlError = new Properties();
//    static Properties propsPostgres = new Properties();
    static Properties propsSqlServer = new Properties();

    static {
        
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();

        Stream.of(
//            new SimpleEntry<>(propsH2, "spring/hibernate.h2.properties"),
            new SimpleEntry<>(propsMysql, "spring/hibernate.mysql.properties"),
//            new SimpleEntry<>(propsMysqlError, "spring/hibernate.mysql-5.5.40.properties"),
//            new SimpleEntry<>(propsPostgres, "spring/hibernate.postgres.properties"),
            new SimpleEntry<>(propsSqlServer, "spring/hibernate.sqlserver.properties")
        ).forEach(simpleEntry -> {
            try (InputStream inputStream = classloader.getResourceAsStream(simpleEntry.getValue())) {
                simpleEntry.getKey().load(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static void initializeDatabases() {
        
        Stream.of(
//            propsH2,
            propsMysql,
//            propsMysqlError,
//            propsPostgres,
            propsSqlServer
        ).forEach(props -> {
            LOGGER.info("######################## Configuration configuration = new Configuration(); " + props.getProperty("jsql.tenant"));
            Configuration configuration = new Configuration();
            configuration.addProperties(props).configure("spring/hibernate.cfg.xml");
            configuration.addAnnotatedClass(Student.class);
            
            StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties());
            
            LOGGER.info("######################## Session session = factory.openSession() " + props.getProperty("jsql.tenant"));
            try (
                SessionFactory factory = configuration.buildSessionFactory(builder.build());
                Session session = factory.openSession()
            ) {
                LOGGER.info("######################## Transaction transaction = session.beginTransaction(); " + props.getProperty("jsql.tenant"));
                Transaction transaction = session.beginTransaction();
                Student student = new Student();
                student.setAge(1);
                session.save(student);
                transaction.commit();
            }
        });
    }

    /**
     * For debug purpose only.
     * @param args
     */
    public static void main(String[] args) {
        
        initializeDatabases();
        
        SpringApplication.run(TargetApplication.class, args);
    }
}