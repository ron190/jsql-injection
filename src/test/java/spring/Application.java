package spring;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;

import model.Student;

@SpringBootApplication
public class Application {
    
    
    static Properties prop = new Properties();
    static Properties prop2 = new Properties();
    static Properties prop3 = new Properties();
    
    static {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        
        try (
            InputStream is = classloader.getResourceAsStream("spring/hibernate.cfg3.xml.properties");
            InputStream is2 = classloader.getResourceAsStream("spring/hibernate.cfg3.xml2.properties");
                InputStream is3 = classloader.getResourceAsStream("spring/hibernate.cfg3.xml3.properties");
                InputStream is4 = classloader.getResourceAsStream("spring/hibernate.cfg3.xml4.properties");
                ) {
            prop.load(is);
            prop2.load(is2);
            prop3.load(is3);
            prop.load(is4);
            prop2.load(is4);
            prop3.load(is4);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
//        public static void main2(String[] args) {
        
        
        Configuration configuration = new Configuration();
//        configuration.addProperties(prop).configure("hibernate.cfg.xml");
        configuration.addProperties(prop).configure("spring/hibernate.cfg4.xml");
        //configuration.addAnnotatedClass(Student.class);
//        configuration.addResource("student.hbm.xml");
        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
//        SessionFactory factory = configuration.buildSessionFactory(builder.build());
        SessionFactory factory = configuration.buildSessionFactory();
        Session session = factory.openSession();
        Transaction transaction = session.beginTransaction();
        Student student = new Student();
        student.setAge(1);
        session.save(student);
        transaction.commit();
        //session.flush();
        session.close();
        factory.close();
        
        Configuration configuration2 = new Configuration();
        //.configure("hibernate.cfg2.xml");
        configuration2.addProperties(prop2).configure("spring/hibernate.cfg4.xml");
        //configuration2.addAnnotatedClass(Student.class);
//        configuration2.addResource("student.hbm.xml");
        StandardServiceRegistryBuilder builder2 = new StandardServiceRegistryBuilder().applySettings(configuration2.getProperties());
//        SessionFactory factory2 = configuration2.buildSessionFactory(builder2.build());
        SessionFactory factory2 = configuration2.buildSessionFactory();
        Session session2 = factory2.openSession();
        Transaction transaction2 = session2.beginTransaction();
        Student student2 = new Student();
        student2.setAge(2);
        session2.save(student2);
        transaction2.commit();
        //session.flush();
        session2.close();
        factory2.close();
        
        Configuration configuration3 = new Configuration();
        //.configure("hibernate.cfg2.xml");
        configuration3.addProperties(prop3).configure("spring/hibernate.cfg4.xml");
        //configuration2.addAnnotatedClass(Student.class);
//        configuration3.addResource("student.hbm.xml");
        StandardServiceRegistryBuilder builder3 = new StandardServiceRegistryBuilder().applySettings(configuration3.getProperties());
//        SessionFactory factory2 = configuration2.buildSessionFactory(builder2.build());
        SessionFactory factory3 = configuration3.buildSessionFactory();
        Session session3 = factory3.openSession();
        Transaction transaction3 = session3.beginTransaction();
        Student student3 = new Student();
        student3.setAge(3);
        session3.save(student3);
        transaction3.commit();
        //session.flush();
        session3.close();
        factory3.close();
        
        SpringApplication.run(Application.class, args);
    }
}