package spring.tenant;

import org.hibernate.cfg.Environment;
import org.hibernate.engine.jdbc.connections.internal.DatasourceConnectionProviderImpl;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import spring.SpringTargetApplication;

import java.sql.DriverManager;
import java.util.*;

public class MasterService {
    
    private final HashMap<String, ConnectionProvider> hashMap = new HashMap<>();
    
    public MasterService() {
        
        // Remove annoying logs from jdbc driver
        DriverManager.setLogWriter(null);

        ArrayList<Properties> properties = new ArrayList<>(
            Arrays.asList(
                SpringTargetApplication.propsH2,
                SpringTargetApplication.propsMysql,
                SpringTargetApplication.propsMysqlError,
                SpringTargetApplication.propsPostgres,
                SpringTargetApplication.propsSqlServer,
                SpringTargetApplication.propsCubrid,
                SpringTargetApplication.propsSqlite,
                SpringTargetApplication.propsDb2,
                SpringTargetApplication.propsHsqldb,
                SpringTargetApplication.propsDerby
            )
        );
        
        properties
        .forEach(props -> {
            
            DatasourceConnectionProviderImpl connectionProviderPostgres = new DatasourceConnectionProviderImpl();
            
            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setUrl(props.getProperty("hibernate.connection.url"));
            dataSource.setUsername(props.getProperty("hibernate.connection.username"));
            dataSource.setPassword(props.getProperty("hibernate.connection.password"));
            
            connectionProviderPostgres.configure(Map.of(
                Environment.DATASOURCE, dataSource
            ));

            this.hashMap.put(props.getProperty("jsql.tenant"), connectionProviderPostgres);
        });
    }
    
    public HashMap<String, ConnectionProvider> getDataSourceHashMap() {
        
        return this.hashMap;
    }
}