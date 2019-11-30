package spring;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

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
    static Properties propsPostgres = new Properties();

    static {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();

        try (
            InputStream inputStreamH2 = classloader.getResourceAsStream("spring/hibernate.h2.properties");
            InputStream inputStreamMySQL = classloader.getResourceAsStream("spring/hibernate.mysql.properties");
            InputStream inputStreamPostgres = classloader.getResourceAsStream("spring/hibernate.postgres.properties");
            InputStream inputStreamHibernate = classloader.getResourceAsStream("spring/hibernate.cfg.properties")
        ) {
            propsH2.load(inputStreamH2);
            propsMySQL.load(inputStreamMySQL);
            propsPostgres.load(inputStreamPostgres);
            
            propsH2.load(inputStreamHibernate);
            propsMySQL.load(inputStreamHibernate);
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
//        try {
//            org.h2.tools.Server server = org.h2.tools.Server.createTcpServer().start();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }

        Configuration configuration = new Configuration();
        configuration.addProperties(propsH2).configure("spring/hibernate.cfg.xml");
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

        Configuration configuration3 = new Configuration();
        configuration3.addProperties(propsMySQL).configure("spring/hibernate.cfg.xml");
        configuration3.addAnnotatedClass(Student.class);
        StandardServiceRegistryBuilder builder3 = new StandardServiceRegistryBuilder()
                .applySettings(configuration3.getProperties());
        SessionFactory factory3 = configuration3.buildSessionFactory(builder3.build());
        Session session3 = factory3.openSession();
        Transaction transaction3 = session3.beginTransaction();
        Student student3 = new Student();
        student3.setAge(3);
        session3.save(student3);
        transaction3.commit();
        session3.close();
        factory3.close();

        Configuration configuration4 = new Configuration();
        configuration4.addProperties(propsPostgres).configure("spring/hibernate.cfg.xml");
        configuration4.addAnnotatedClass(Student.class);
        StandardServiceRegistryBuilder builder4 = new StandardServiceRegistryBuilder()
                .applySettings(configuration4.getProperties());
        SessionFactory factory4 = configuration4.buildSessionFactory(builder4.build());
        Session session4 = factory4.openSession();
        Transaction transaction4 = session4.beginTransaction();
        Student student4 = new Student();
        student4.setAge(4);
        session4.save(student4);
        transaction4.commit();
        session4.close();
        factory4.close();
    }
}