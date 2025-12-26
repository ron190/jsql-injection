package spring.tenant.hibernate;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class CurrentTenantIdentifierResolverImpl implements CurrentTenantIdentifierResolver<String> {

    @Override
    public String resolveCurrentTenantIdentifier() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String tenant = "mysql";  // Undefined used by SOAP
        if (attributes != null && attributes.getRequest().getParameter("tenant") != null) {
            tenant = attributes.getRequest().getParameter("tenant");
        }
        return tenant;
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}