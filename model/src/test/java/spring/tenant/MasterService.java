package spring.tenant;

import org.hibernate.cfg.Environment;
import org.hibernate.cfg.JdbcSettings;
import org.hibernate.engine.jdbc.connections.internal.DatasourceConnectionProviderImpl;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import spring.SpringTargetApplication;

import java.sql.DriverManager;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

public class MasterService {
    
    private final HashMap<String, ConnectionProvider> hashMap = new HashMap<>();
    
    public MasterService() {
        
        DriverManager.setLogWriter(null);  // remove annoying logs from jdbc driver
        
        SpringTargetApplication.getPropertiesFilterByProfile().map(AbstractMap.SimpleEntry::getKey).forEach(props -> {
            
            DatasourceConnectionProviderImpl connectionProvider = new DatasourceConnectionProviderImpl();
            
            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setUrl(props.getProperty(JdbcSettings.JAKARTA_JDBC_URL));
            dataSource.setUsername(props.getProperty(JdbcSettings.JAKARTA_JDBC_USER));
            dataSource.setPassword(props.getProperty(JdbcSettings.JAKARTA_JDBC_PASSWORD));
            
            connectionProvider.configure(Map.of(
                Environment.DATASOURCE, dataSource
            ));

            this.hashMap.put(props.getProperty("jsql.tenant"), connectionProvider);
        });
    }
    
    public HashMap<String, ConnectionProvider> getDataSourceHashMap() {
        return this.hashMap;
    }
}