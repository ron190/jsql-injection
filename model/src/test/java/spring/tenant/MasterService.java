package spring.tenant;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.cfg.Environment;
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
        
        // Remove annoying logs from jdbc driver
        DriverManager.setLogWriter(null);
        
        SpringTargetApplication.propertiesByEngine.stream()
        .filter(propertyByEngine -> System.getProperty("profileId", StringUtils.EMPTY).equals(
            propertyByEngine.getKey().getProperty("jsql.profile", StringUtils.EMPTY)
        ))
        .map(AbstractMap.SimpleEntry::getKey).forEach(props -> {
            
            DatasourceConnectionProviderImpl connectionProvider = new DatasourceConnectionProviderImpl();
            
            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setUrl(props.getProperty(Environment.URL));
            dataSource.setUsername(props.getProperty(Environment.USER));
            dataSource.setPassword(props.getProperty(Environment.PASS));
            
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