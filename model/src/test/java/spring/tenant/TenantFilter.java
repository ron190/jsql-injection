package spring.tenant;

import jakarta.servlet.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
@Order(1)
class TenantFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String tenant = request.getParameter("tenant");  // Undefined used by SOAP
        if (tenant == null) {
            tenant = "h2";
        }
        try {
            chain.doFilter(request, response);
        } finally {
            TenantContext.setCurrentTenant(tenant);
        }
    }
}