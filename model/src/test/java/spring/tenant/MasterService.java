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
                SpringTargetApplication.propsPostgreSql,
                SpringTargetApplication.propsSqlServer,
                SpringTargetApplication.propsCubrid,
                SpringTargetApplication.propsSqlite,
                SpringTargetApplication.propsDb2,
                SpringTargetApplication.propsHsqldb,
                SpringTargetApplication.propsDerby,
                SpringTargetApplication.propsFirebird,
                SpringTargetApplication.propsInformix
            )
        );
        
        properties.forEach(props -> {
            
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