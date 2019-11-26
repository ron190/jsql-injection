package spring;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class HibernateConf {
 
    @Primary
    @Bean
    public LocalSessionFactoryBean sessionFactorya() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        //sessionFactory.setDataSource(dataSource());
        //sessionFactory.setPackagesToScan({"com.baeldung.hibernate.bootstrap.model" });
        sessionFactory.setHibernateProperties(hibernateProperties());
//        sessionFactory.setMappingResources("spring/student.hbm.xml");
        
        //sessionFactory.setCurrentTenantIdentifierResolver(new CurrentTenantIdentifierResolverImpl());
        //sessionFactory.setMultiTenantConnectionProvider(new MultiTenantConnectionProviderImpl());
        

 
        return sessionFactory;
    }
 
    //@Bean
    //public LocalSessionFactoryBean sessionFactoryab() {
    //    LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
    //    //sessionFactory.setDataSource(dataSource());
    //    //sessionFactory.setPackagesToScan({"com.baeldung.hibernate.bootstrap.model" });
    //    sessionFactory.setHibernateProperties(hibernatePropertiesa());
    //    sessionFactory.setMappingResources("student.hbm.xml");
    //    
    //    //sessionFactory.setCurrentTenantIdentifierResolver(new CurrentTenantIdentifierResolverImpl());
    //    //sessionFactory.setMultiTenantConnectionProvider(new MultiTenantConnectionProviderImpl());
    //    
    //
    //
    //    return sessionFactory;
    //}
    
    
 
    //@Bean
    //public DataSource dataSource() {
    //    BasicDataSource dataSource = new BasicDataSource();
    //    dataSource.setDriverClassName("org.h2.Driver");
    //    dataSource.setUrl("jdbc:h2:mem:db;DB_CLOSE_DELAY=-1");
    //    dataSource.setUsername("sa");
    //    dataSource.setPassword("sa");
    //
    //    return dataSource;
    //}
 
    //@Bean
    //public PlatformTransactionManager hibernateTransactionManager() {
    //    HibernateTransactionManager transactionManager
    //      = new HibernateTransactionManager();
    //    transactionManager.setSessionFactory(sessionFactory().getObject());
    //    return transactionManager;
    //}
 
    private final Properties hibernateProperties() {
        Properties hibernateProperties = new Properties();
        //hibernateProperties.setProperty(          "hibernate.hbm2ddl.auto", "create-drop");
        hibernateProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
//        hibernateProperties.setProperty("hibernate.multi_tenant_connection_provider", "hello.MultiTenantConnectionProviderImpl2");
        hibernateProperties.setProperty("hibernate.multi_tenant_connection_provider", "spring.MultiTenantConnectionProviderImpl3");
        hibernateProperties.setProperty("hibernate.tenant_identifier_resolver", "spring.CurrentTenantIdentifierResolverImpl");
        hibernateProperties.setProperty("hibernate.multiTenancy", "DATABASE");
        //hibernateProperties.setProperty("hibernate.hbm2ddl.auto", "create");
 
        return hibernateProperties;
    }
 
    //private final Properties hibernatePropertiesa() {
    //    Properties hibernateProperties = new Properties();
    //    //hibernateProperties.setProperty(          "hibernate.hbm2ddl.auto", "create-drop");
    //    hibernateProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
    //    hibernateProperties.setProperty("hibernate.multiTenancy", "NONE");
    //    hibernateProperties.setProperty("hibernate.hbm2ddl.auto", "create");        
    //    
    //    hibernateProperties.put("hibernate.connection.driver_class", "org.h2.Driver");
    //    hibernateProperties.put("hibernate.connection.url", "jdbc:h2:mem:tenantId4;DB_CLOSE_DELAY=-1;");
    //    hibernateProperties.put("hibernate.connection.username", "sa");
    //    hibernateProperties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
    //    hibernateProperties.put("hibernate.hbm2ddl.auto", "create");
    //
    //    return hibernateProperties;
    //}
}