package spring.tenant.jpa;

import spring.SpringApp;

public class TenantContext {

    private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();

    public static String getCurrentTenant() {
        return TenantContext.CURRENT_TENANT.get() != null
            ? TenantContext.CURRENT_TENANT.get()
            : SpringApp.H2;
    }

    public static void setCurrentTenant(String tenant) {
        TenantContext.CURRENT_TENANT.set(tenant);
    }
}