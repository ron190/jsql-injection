package spring.tenant;

import com.test.method.CustomMethodSuiteIT;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.cfg.JdbcSettings;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitManager;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RequestMethod;
import spring.SpringApp;

import javax.sql.DataSource;
import java.sql.DriverManager;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
@EnableTransactionManagement
public class HibernateConf {

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }

    @Bean
    public EntityManagerFactoryBuilder entityManagerFactoryBuilder(
        JpaVendorAdapter jpaVendorAdapter,
        ObjectProvider<PersistenceUnitManager> persistenceUnitManager
    ) {
        return new EntityManagerFactoryBuilder(
            jpaVendorAdapter,
            f -> Collections.emptyMap(),
            persistenceUnitManager.getIfAvailable()
        );
    }

    @Bean
    public DataSource dataSource() {
        Map<Object, Object> resolvedDataSources = new HashMap<>();

        DriverManager.setLogWriter(null);  // remove annoying logs from jdbc driver
        SpringApp.getPropertiesFilterByProfile().map(AbstractMap.SimpleEntry::getKey).forEach(props -> {
            DataSource dataSource = DataSourceBuilder.create()
                .url(props.getProperty(JdbcSettings.JAKARTA_JDBC_URL))
                .username(props.getProperty(JdbcSettings.JAKARTA_JDBC_USER))
                .password(props.getProperty(JdbcSettings.JAKARTA_JDBC_PASSWORD))
                .driverClassName(props.getProperty(JdbcSettings.JAKARTA_JDBC_DRIVER))
            .build();
            resolvedDataSources.put(props.getProperty("jsql.tenant"), dataSource);
        });

        AbstractRoutingDataSource dataSource = new MultitenantDataSource();
        dataSource.setDefaultTargetDataSource(resolvedDataSources.get("h2"));
        dataSource.setTargetDataSources(resolvedDataSources);

        dataSource.afterPropertiesSet();
        return dataSource;
    }

    @Bean
    public StrictHttpFirewall httpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();

        List<String> httpMethods = Stream.concat(
            Stream.of(CustomMethodSuiteIT.CUSTOM_METHOD),
            Arrays.stream(RequestMethod.values()).map(Enum::name)
        ).collect(Collectors.toList());

        firewall.setAllowedHttpMethods(httpMethods);
        firewall.setUnsafeAllowAnyHttpMethod(true);
        return firewall;
    }
}