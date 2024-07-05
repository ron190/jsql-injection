package spring.tenant;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class CurrentTenantIdentifierResolverImpl implements CurrentTenantIdentifierResolver<String> {

    @Override
    public String resolveCurrentTenantIdentifier() {
        
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String tenant;  // Undefined used by SOAP
        
        if (attributes != null) {
            
            tenant = attributes.getRequest().getParameter("tenant");
            
            if (tenant == null) {
                tenant = "mysql";
            }
        } else {
            tenant = "mysql";
        }
        
        return tenant;
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}