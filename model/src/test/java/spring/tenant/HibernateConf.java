package spring.tenant;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
@EnableTransactionManagement
public class HibernateConf {

    @Bean
    public PlatformTransactionManager hibernateTransactionManager() {

        HibernateTransactionManager transactionManager = new HibernateTransactionManager();
        transactionManager.setSessionFactory(sessionFactory().getObject());

        return transactionManager;
    }

    @Bean
    public LocalSessionFactoryBean sessionFactory() {

        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setHibernateProperties(this.hibernateProperties());

        return sessionFactory;
    }

    @Bean
    public StrictHttpFirewall httpFirewall() {

        StrictHttpFirewall firewall = new StrictHttpFirewall();

        List<String> httpMethods = Stream
            .concat(
                Stream.of("CUSTOM-JSQL"),
                Arrays.stream(RequestMethod.values()).map(Enum::name)
            )
            .collect(Collectors.toList());

        firewall.setAllowedHttpMethods(httpMethods);
        firewall.setUnsafeAllowAnyHttpMethod(true);

        return firewall;
    }
 
    private Properties hibernateProperties() {
        
        Properties hibernateProperties = new Properties();
        
        hibernateProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        hibernateProperties.setProperty("hibernate.multi_tenant_connection_provider", "spring.tenant.MultiTenantConnectionProviderImpl");
        hibernateProperties.setProperty("hibernate.tenant_identifier_resolver", "spring.tenant.CurrentTenantIdentifierResolverImpl");
        hibernateProperties.setProperty("hibernate.multiTenancy", "DATABASE");
        
        return hibernateProperties;
    }
}