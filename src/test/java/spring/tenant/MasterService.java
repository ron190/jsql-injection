package spring.tenant;

import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Properties;
import java.util.stream.Stream;

import org.hibernate.cfg.Environment;
import org.hibernate.engine.jdbc.connections.internal.DatasourceConnectionProviderImpl;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import spring.TargetApplication;

public class MasterService {
    
    private HashMap<String, ConnectionProvider> hashMap = new HashMap<>();
    
    public MasterService() {
        
        // Remove annoying logs from jdbc driver
        DriverManager.setLogWriter(null);

        Stream.of(
            TargetApplication.propsH2,
            TargetApplication.propsMysql,
            TargetApplication.propsMysqlError,
            TargetApplication.propsPostgres,
            TargetApplication.propsSqlServer,
            TargetApplication.propsCubrid,
            TargetApplication.propsSqlite,
            TargetApplication.propsDb2
        ).forEach(props -> {
            
            DatasourceConnectionProviderImpl connectionProviderPostgres = new DatasourceConnectionProviderImpl();
            
            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setUrl(props.getProperty("hibernate.connection.url"));
            dataSource.setUsername(props.getProperty("hibernate.connection.username"));
            dataSource.setPassword(props.getProperty("hibernate.connection.password"));
            
            Properties properties = new Properties();
            properties.put(Environment.DATASOURCE, dataSource);
            connectionProviderPostgres.configure(properties);
            
            this.hashMap.put(props.getProperty("jsql.tenant"), connectionProviderPostgres);
        });
    }
    
    public HashMap<String, ConnectionProvider> getDataSourceHashMap() {
        return this.hashMap;
    }
}