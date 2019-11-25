package spring;

import org.hibernate.engine.jdbc.connections.spi.AbstractMultiTenantConnectionProvider;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;



public class MultiTenantConnectionProviderImpl3 extends AbstractMultiTenantConnectionProvider {

    @Override
    protected ConnectionProvider  getAnyConnectionProvider() {
        return MasterService3.getDataSourceHashMap().get("tenantId");
    }

    @Override
    protected ConnectionProvider selectConnectionProvider(String tenantIdentifier) {
        return MasterService3.getDataSourceHashMap().get(tenantIdentifier);
    }

}