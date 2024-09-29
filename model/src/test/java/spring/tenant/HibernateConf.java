package spring.tenant;

import com.test.method.CustomMethodSuiteIT;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Environment;
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

        Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty(Environment.DIALECT, "org.hibernate.dialect.MySQLDialect");
        hibernateProperties.setProperty(AvailableSettings.MULTI_TENANT_CONNECTION_PROVIDER, "spring.tenant.MultiTenantConnectionProviderImpl");
        hibernateProperties.setProperty(AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER, "spring.tenant.CurrentTenantIdentifierResolverImpl");
        hibernateProperties.setProperty("hibernate.multiTenancy", "DATABASE");

        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setHibernateProperties(hibernateProperties);

        return sessionFactory;
    }

    @Bean
    public StrictHttpFirewall httpFirewall() {

        StrictHttpFirewall firewall = new StrictHttpFirewall();

        List<String> httpMethods = Stream.concat(
                Stream.of(CustomMethodSuiteIT.CUSTOM_METHOD),
                Arrays.stream(RequestMethod.values()).map(Enum::name)
            )
            .collect(Collectors.toList());

        firewall.setAllowedHttpMethods(httpMethods);
        firewall.setUnsafeAllowAnyHttpMethod(true);

        return firewall;
    }
}