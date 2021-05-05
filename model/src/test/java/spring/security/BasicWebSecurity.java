package spring.security;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

@Configuration
@EnableWebSecurity
@Order(2)
public class BasicWebSecurity extends WebSecurityConfigurerAdapter {

    public static final String BASIC_REALM = "Basic Realm";
    private static final String BASIC_ROLE = "ADMIN1";
    public static final String BASIC_USERNAME = "login-basic";
    public static final String BASIC_PASSWORD = "password-basic";
    
    @Autowired
    private MyBasicAuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        
        auth
        .inMemoryAuthentication()
        .withUser(BASIC_USERNAME)
        .password(this.passwordEncoder().encode(BASIC_PASSWORD))
        .roles(BASIC_ROLE);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        
        http
        .authorizeRequests()
        .antMatchers("/basic/**")
        .authenticated()
        .and()
        .csrf(csrf -> csrf.disable()) // Set to lowest position to work
        .httpBasic()
        .authenticationEntryPoint(this.authenticationEntryPoint)
        ;

        http.addFilterAfter(new CustomFilter(), BasicAuthenticationFilter.class);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

class CustomFilter extends GenericFilterBean {
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(request, response);
    }
}

@Component
class MyBasicAuthenticationEntryPoint extends BasicAuthenticationEntryPoint {
    
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
        this.setRealmName(BasicWebSecurity.BASIC_REALM);
        super.afterPropertiesSet();
    }
}