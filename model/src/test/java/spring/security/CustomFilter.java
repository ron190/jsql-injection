package spring.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

public class CustomFilter extends GenericFilterBean {

    private static final Logger LOGGER = LogManager.getRootLogger();

    private final String nameConfig;
    public int count;

    public CustomFilter(String nameConfig) {
        this.nameConfig = nameConfig;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        LOGGER.debug("CustomFilter: {}", nameConfig);
        count++;

        filterChain.doFilter(servletRequest, servletResponse);
    }
}