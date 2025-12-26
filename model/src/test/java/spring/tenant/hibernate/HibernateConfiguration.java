package spring.tenant.hibernate;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Environment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.*;

@Configuration
@EnableTransactionManagement
public class HibernateConfiguration {

    @Bean("hibernateTransactionManager")
    public PlatformTransactionManager hibernateTransactionManager() {
        HibernateTransactionManager transactionManager = new HibernateTransactionManager();
        transactionManager.setSessionFactory(this.sessionFactory().getObject());
        return transactionManager;
    }

    @Bean
    public LocalSessionFactoryBean sessionFactory() {
        Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty(Environment.DIALECT, "org.hibernate.dialect.MySQLDialect");
        hibernateProperties.put(AvailableSettings.MULTI_TENANT_CONNECTION_PROVIDER, MultiTenantConnectionProviderImpl.class);
        hibernateProperties.put(AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER, CurrentTenantIdentifierResolverImpl.class);
        hibernateProperties.setProperty("hibernate.multiTenancy", "DATABASE");

        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setHibernateProperties(hibernateProperties);
        return sessionFactory;
    }
}