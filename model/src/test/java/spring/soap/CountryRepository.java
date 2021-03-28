package spring.soap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import spring.rest.Greeting;

@Component
public class CountryRepository {
    
    private static final Logger LOGGER = LogManager.getRootLogger();
	private static final Map<String, Country> countries = new HashMap<>();
	
    @Autowired
    private SessionFactory sessionFactory;
    
    private static final String template = "Hello, s!";
    private ObjectMapper objectMapper = new ObjectMapper();

    @SuppressWarnings("unchecked")
    @Transactional
	public Country findCountry(String name) throws Exception {
	    
        Country country = new Country();
        
        Session session = this.sessionFactory.getCurrentSession();
        
        try {
            Query query = session.createNativeQuery("select 1,2,3,4,First_Name,5,6,7,8 from Student where '1' = '"+ name +"'");
            
            List<Object[]> results = query.getResultList();
            
            country.setName(
                String.format(template, name)
                + StringEscapeUtils.unescapeJava(this.objectMapper.writeValueAsString(results))
            );
            
        } catch (Exception e) {
            
            // Required by multiple columns
            country.setName(this.initializeErrorMessage(e).getContent());
            
            // Required by transaction rollback
            throw e;
        }
        
        
        return country;
	}

    private Greeting initializeErrorMessage(Exception e) {
        
        String stacktrace = ExceptionUtils.getStackTrace(e);
        
        LOGGER.debug(stacktrace);
        
        Greeting greeting = new Greeting(
            0,
            template+"#"
            + StringEscapeUtils.unescapeJava(stacktrace)
        );
        
        return greeting;
    }

    @PostConstruct
    public void initData() {
        
        Country spain = new Country();
        spain.setName("Spain");
        spain.setCapital("Madrid");
        spain.setCurrency(Currency.EUR);
        spain.setPopulation(46704314);

        countries.put(spain.getName(), spain);

        Country poland = new Country();
        poland.setName("Poland");
        poland.setCapital("Warsaw");
        poland.setCurrency(Currency.PLN);
        poland.setPopulation(38186860);

        countries.put(poland.getName(), poland);

        Country uk = new Country();
        uk.setName("United Kingdom");
        uk.setCapital("London");
        uk.setCurrency(Currency.GBP);
        uk.setPopulation(63705000);

        countries.put(uk.getName(), uk);
    }
}