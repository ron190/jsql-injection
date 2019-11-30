package spring;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;
import java.util.stream.Stream;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import model.Student;

@SpringBootApplication
public class Application {

    static Properties propsH2 = new Properties();
    static Properties propsMySQL = new Properties();
    static Properties propsMySQLError = new Properties();
    static Properties propsPostgres = new Properties();

    static {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();

        try (
            InputStream inputStreamH2 = classloader.getResourceAsStream("spring/hibernate.h2.properties");
            InputStream inputStreamMySQL = classloader.getResourceAsStream("spring/hibernate.mysql.properties");
            InputStream inputStreamMySQLError = classloader.getResourceAsStream("spring/hibernate.mysql-5.5.40.properties");
            InputStream inputStreamPostgres = classloader.getResourceAsStream("spring/hibernate.postgres.properties");
            InputStream inputStreamHibernate = classloader.getResourceAsStream("spring/hibernate.cfg.properties")
        ) {
            propsH2.load(inputStreamH2);
            propsMySQL.load(inputStreamMySQL);
            propsMySQLError.load(inputStreamMySQLError);
            propsPostgres.load(inputStreamPostgres);
            
            propsH2.load(inputStreamHibernate);
            propsMySQL.load(inputStreamHibernate);
            propsMySQLError.load(inputStreamHibernate);
            propsPostgres.load(inputStreamHibernate);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        init();
        SpringApplication.run(Application.class, args);
    }

    public static void init() {
        Stream.of(
            propsH2, 
            propsMySQL, 
            propsMySQLError, 
            propsPostgres
        ).forEach(props -> {
            Configuration configuration = new Configuration();
            configuration.addProperties(props).configure("spring/hibernate.cfg.xml");
            configuration.addAnnotatedClass(Student.class);
            StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties());
            SessionFactory factory = configuration.buildSessionFactory(builder.build());
            Session session = factory.openSession();
            Transaction transaction = session.beginTransaction();
            Student student = new Student();
            student.setAge(1);
            session.save(student);
            transaction.commit();
            session.close();
            factory.close();
        });
    }
}