package spring;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;

import javax.persistence.EntityManager;
import javax.persistence.Query;

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
    private SessionFactory sessionFactorya;

    @RequestMapping("/greeting")
    public Greeting greeting(@RequestParam(value="name", defaultValue="World") String name) throws IOException {
        EntityManager em = sessionFactorya.createEntityManager();
        //Query q = em.createNativeQuery("select * from MYDB1.TBL1");
        Query q = em.createNativeQuery("select First_Name from Student where '1' = '"+name+"'");
        List<Object[]> l = q.getResultList();
//        l.stream().forEach(e -> Arrays.asList((Object[]) e).stream().forEach(System.out::println));
        
        ObjectMapper objectMapper = new ObjectMapper();
        
        Properties p = new Properties();
        p.load(new StringReader("key="+objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(l)));
        
        return new Greeting(counter.incrementAndGet(),
                            String.format(template, name) + StringEscapeUtils.unescapeJava(objectMapper.writeValueAsString(l)));
    }
}