package spring.tenant;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class HibernateConf {
 
    @Bean
    public LocalSessionFactoryBean sessionFactory() {
        
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setHibernateProperties(this.hibernateProperties());
 
        return sessionFactory;
    }
 
    private final Properties hibernateProperties() {
        
        Properties hibernateProperties = new Properties();
        
        hibernateProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
        hibernateProperties.setProperty("hibernate.multi_tenant_connection_provider", "spring.tenant.MultiTenantConnectionProviderImpl");
        hibernateProperties.setProperty("hibernate.tenant_identifier_resolver", "spring.tenant.CurrentTenantIdentifierResolverImpl");
        hibernateProperties.setProperty("hibernate.multiTenancy", "DATABASE");
        
        return hibernateProperties;
    }
}