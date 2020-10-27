package spring.tenant;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


public class CurrentTenantIdentifierResolverImpl implements CurrentTenantIdentifierResolver {

    @Override
    public String resolveCurrentTenantIdentifier() {
        
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        
        // TODO Set dummy value instead to avoid fallback on mysql
        // Used by SOAP
        String tenant = "mysql";
        
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