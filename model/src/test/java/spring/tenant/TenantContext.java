package spring.tenant;

public class TenantContext {

    private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();

    public static String getCurrentTenant() {
        return TenantContext.CURRENT_TENANT.get();
    }

    public static void setCurrentTenant(String tenant) {
        TenantContext.CURRENT_TENANT.set(tenant);
    }
}