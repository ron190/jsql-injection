package spring;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class GreetingController {

    private static final String template = "Hello, s!";
    private final AtomicLong counter = new AtomicLong();
    
    @Autowired
    private SessionFactory sessionFactory;

    ObjectMapper objectMapper = new ObjectMapper();
    
    @RequestMapping("/greeting")
    public Greeting greeting(@RequestParam(value="name", defaultValue="World") String name) throws IOException {
        
        EntityManager em = sessionFactory.createEntityManager();
        Query q = em.createNativeQuery("select First_Name from Student where '1' = '"+name+"'");
        
        Greeting greeting = null;
        try {
            List<Object[]> l = q.getResultList();
            
            greeting = new Greeting(
                counter.incrementAndGet(),
                String.format(template, name)
                + StringEscapeUtils.unescapeJava(objectMapper.writeValueAsString(l))
            );
        } catch (Exception e) {
            // Hide useless SQL error messages
        } finally {
            em.close();
            sessionFactory.getCurrentSession().close();
        }
        
        return greeting;
    }

    @RequestMapping("/greeting-error")
    public Greeting greetingError(@RequestParam(value="name", defaultValue="World") String name) throws IOException {
        
        EntityManager em = sessionFactory.createEntityManager();
        Query q = em.createNativeQuery("select First_Name from Student where '1' = '"+name+"'");
        
        Greeting greeting = null;
        try {
            q.getResultList();
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            
            greeting = new Greeting(
                counter.incrementAndGet(),
                String.format(template+"#", name) 
                + StringEscapeUtils.unescapeJava(stacktrace)
            );
        } finally {
            em.close();
            sessionFactory.getCurrentSession().close();
        }
        
        return greeting;
    }

    @RequestMapping("/greeting-blind")
    public Greeting greetingBlind(@RequestParam(value="name", defaultValue="World") String name) throws IOException {
        
        EntityManager em = sessionFactory.createEntityManager();
        Query q = em.createNativeQuery("select First_Name from Student where '1' = '"+name+"'");
        
        Greeting greeting = null;
        try {
            List l = q.getResultList();
            
            if (l.isEmpty()) {
                greeting = new Greeting(
                    counter.incrementAndGet(),
                    String.format(template+"#", name) 
                    + StringEscapeUtils.unescapeJava("PREFIX It's true SUFFIX")
                );
            } else {
                greeting = new Greeting(
                    counter.incrementAndGet(),
                    String.format(template+"#", name) 
                    + StringEscapeUtils.unescapeJava("PREFIX It's false SUFFIX")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
            sessionFactory.getCurrentSession().close();
        }
        
        return greeting;
    }

}