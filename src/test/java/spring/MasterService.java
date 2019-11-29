package spring;
import java.sql.DriverManager;
import java.util.HashMap;

import org.hibernate.engine.jdbc.connections.internal.DatasourceConnectionProviderImpl;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class MasterService {
    
    public static HashMap<String, ConnectionProvider> getDataSourceHashMap() {

        HashMap<String, ConnectionProvider> hashMap = new HashMap<>();
        
        DatasourceConnectionProviderImpl connectionProviderH2 = new DatasourceConnectionProviderImpl();
        DriverManagerDataSource dataSourceH2 = new DriverManagerDataSource();
        dataSourceH2.setConnectionProperties(Application.propsH2);
        dataSourceH2.setUrl(Application.propsH2.getProperty("hibernate.connection.url"));
        connectionProviderH2.setDataSource(dataSourceH2);
        connectionProviderH2.configure(Application.propsH2);
        
        // Remove annoying logs from jdbc driver
        DriverManager.setLogWriter(null);
        
        DatasourceConnectionProviderImpl connectionProviderMySQL = new DatasourceConnectionProviderImpl();
        DriverManagerDataSource dataSourceMySQL = new DriverManagerDataSource();
        dataSourceMySQL.setConnectionProperties(Application.propsMySQL);
        dataSourceMySQL.setUrl(Application.propsMySQL.getProperty("hibernate.connection.url"));
        dataSourceMySQL.setUsername(Application.propsMySQL.getProperty("hibernate.connection.username"));
        dataSourceMySQL.setPassword(Application.propsMySQL.getProperty("hibernate.connection.password"));
        connectionProviderMySQL.setDataSource(dataSourceMySQL);
        connectionProviderMySQL.configure(Application.propsMySQL);
         
        hashMap.put("h2", connectionProviderH2);
        hashMap.put("mysql", connectionProviderMySQL);
        
        return hashMap;
    }
}