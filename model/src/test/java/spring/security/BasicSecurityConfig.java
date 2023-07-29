package spring.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

@Configuration
@Order(1)
public class BasicSecurityConfig {

    public static final String BASIC_REALM = "Basic Realm";
    private static final String BASIC_ROLE = "ADMIN1";
    public static final String BASIC_USERNAME = "login-basic";
    public static final String BASIC_PASSWORD = "password-basic";
    public static final CustomFilter FILTER = new CustomFilter("basic");

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {

        auth.inMemoryAuthentication()
            .withUser(BASIC_USERNAME)
            .password(this.passwordEncoder().encode(BASIC_PASSWORD))
            .authorities(BASIC_ROLE);
    }

    @Bean
    public SecurityFilterChain filterChainBasic(HttpSecurity http) throws Exception {

        return http.securityMatcher("/basic/**")
            .csrf().disable()
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/basic/**").hasAuthority(BASIC_ROLE)
            )
            .addFilterAfter(FILTER, AuthorizationFilter.class)
            .httpBasic()
            .and()
            .build();
    }
}