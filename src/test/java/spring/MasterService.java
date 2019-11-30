package spring;

import java.sql.DriverManager;
import java.util.HashMap;
import java.util.stream.Stream;

import org.hibernate.engine.jdbc.connections.internal.DatasourceConnectionProviderImpl;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class MasterService {
    
    public static HashMap<String, ConnectionProvider> getDataSourceHashMap() {
        
        // Remove annoying logs from jdbc driver
        DriverManager.setLogWriter(null);

        HashMap<String, ConnectionProvider> hashMap = new HashMap<>();
        
        Stream.of(
            Application.propsH2, 
            Application.propsMySQL, 
            Application.propsMySQLError, 
            Application.propsPostgres
        ).forEach(props -> {
            DatasourceConnectionProviderImpl connectionProviderPostgres = new DatasourceConnectionProviderImpl();
            DriverManagerDataSource dataSourcePostgres = new DriverManagerDataSource();
            dataSourcePostgres.setConnectionProperties(props);
            dataSourcePostgres.setUrl(props.getProperty("hibernate.connection.url"));
            dataSourcePostgres.setUsername(props.getProperty("hibernate.connection.username"));
            dataSourcePostgres.setPassword(props.getProperty("hibernate.connection.password"));
            connectionProviderPostgres.setDataSource(dataSourcePostgres);
            connectionProviderPostgres.configure(props);
            hashMap.put(props.getProperty("jsql.tenant"), connectionProviderPostgres);
        });
         
        return hashMap;
    }
}