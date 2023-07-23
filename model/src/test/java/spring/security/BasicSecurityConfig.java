package spring.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
@Order(2)
public class BasicSecurityConfig {

    public static final String BASIC_REALM = "Basic Realm";
    private static final String BASIC_ROLE = "ADMIN1";
    public static final String BASIC_USERNAME = "login-basic";
    public static final String BASIC_PASSWORD = "password-basic";

    @Autowired private MyBasicAuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {

        auth.inMemoryAuthentication()
            .withUser(BASIC_USERNAME)
            .password(this.passwordEncoder().encode(BASIC_PASSWORD))
            .roles(BASIC_ROLE);
    }

    @Bean
    public SecurityFilterChain filterChainBasic(HttpSecurity http) throws Exception {

        return http.csrf()
            .disable()
            .antMatcher("/basic/**")
            .authorizeRequests()
            .anyRequest()
            .fullyAuthenticated()
            .and()
            .httpBasic()
            .authenticationEntryPoint(this.authenticationEntryPoint)
            .and()
            .addFilterAfter(new CustomFilter(), BasicAuthenticationFilter.class)
            .build();
    }

    class CustomFilter extends GenericFilterBean {

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
            chain.doFilter(request, response);
        }
    }

    @Component
    static class MyBasicAuthenticationEntryPoint extends BasicAuthenticationEntryPoint {

        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {

            //Authentication failed, send error response.
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.addHeader("WWW-Authenticate", "Basic realm=\""+ this.getRealmName() +"\"");

            PrintWriter writer = response.getWriter();
            writer.println("HTTP Status 401: "+ authException.getMessage());
        }

        @Override
        public void afterPropertiesSet() {
            this.setRealmName(BasicSecurityConfig.BASIC_REALM);
            super.afterPropertiesSet();
        }
    }
}