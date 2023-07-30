package spring.tenant;

import org.hibernate.engine.jdbc.connections.spi.AbstractMultiTenantConnectionProvider;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;

public class MultiTenantConnectionProviderImpl extends AbstractMultiTenantConnectionProvider {

    private static final MasterService MASTER_SERVICE = new MasterService();
    
    @Override
    protected ConnectionProvider getAnyConnectionProvider() {
        
        return MASTER_SERVICE.getDataSourceHashMap().get("h2");
    }

    @Override
    protected ConnectionProvider selectConnectionProvider(String tenant) {
        
        return MASTER_SERVICE.getDataSourceHashMap().getOrDefault(
            tenant,
            // Provide default connection to prevent error logs when tenant is undefined
            getAnyConnectionProvider()
        );
    }
}