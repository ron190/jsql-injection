package spring.soap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import spring.rest.Greeting;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class CountryRepository {
    
    private static final Logger LOGGER = LogManager.getRootLogger();

    @Autowired
    private SessionFactory sessionFactory;
    
    private static final String template = "Hello, s!";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @SuppressWarnings("unchecked")
    @Transactional
	public Country findCountry(String name) throws JsonProcessingException {
	    
        Country country = new Country();

        Session session = this.sessionFactory.getCurrentSession();

        // TODO Decode XML invalid chars
        name = URLDecoder.decode(name, StandardCharsets.UTF_8);

        try {
            NativeQuery<Object[]> query = session.createNativeQuery("select 1,2,3,4,First_Name,5,6,7,8 from Student where '1' = '" + name + "'");
            
            List<Object[]> results = query.getResultList();

            // TODO Encode XML invalid chars
            country.setName(URLEncoder.encode(template + StringEscapeUtils.unescapeJava(this.objectMapper.writeValueAsString(results)), StandardCharsets.UTF_8));
            
        } catch (Exception e) {
            
            // Required by multiple columns
            country.setName(URLEncoder.encode(this.initializeErrorMessage(e).getContent(), StandardCharsets.UTF_8));
            
            // Required by transaction rollback
            throw e;
        }
        
        
        return country;
	}

    private Greeting initializeErrorMessage(Exception e) {
        
        String stacktrace = ExceptionUtils.getStackTrace(e);
        
        LOGGER.debug(stacktrace);

        return new Greeting(template + "#" + StringEscapeUtils.unescapeJava(stacktrace));
    }
}