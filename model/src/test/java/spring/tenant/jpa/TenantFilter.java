package spring.tenant.jpa;

import jakarta.servlet.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import spring.SpringApp;

import java.io.IOException;

@Component
@Order(1)
class TenantFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String tenant = request.getParameter(SpringApp.TENANT);  // Undefined used by SOAP
        if (tenant == null) {
            tenant = SpringApp.TENANT_H2;
        }
        TenantContext.setCurrentTenant(tenant);
        chain.doFilter(request, response);
    }
}