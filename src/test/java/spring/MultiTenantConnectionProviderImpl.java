package spring;

import org.hibernate.engine.jdbc.connections.spi.AbstractMultiTenantConnectionProvider;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;

public class MultiTenantConnectionProviderImpl extends AbstractMultiTenantConnectionProvider {

    @Override
    protected ConnectionProvider getAnyConnectionProvider() {
        return MasterService.getDataSourceHashMap().get("h2");
    }

    @Override
    protected ConnectionProvider selectConnectionProvider(String tenantIdentifier) {
        return MasterService.getDataSourceHashMap().get(tenantIdentifier);
    }

}