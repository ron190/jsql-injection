package spring.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.DigestAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.DigestAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.Filter;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("deprecation")
@Configuration
@Order(1)
public class DigestSecurityConfig {
    
    private static final String DIGEST_ROLE = "ADMIN2";
    private static final String DIGEST_REALM = "Digest Realm";
    public static final String DIGEST_USERNAME = "login-digest";
    public static final String DIGEST_PASSWORD = "password-digest";
    private static final String DIGEST_PASSWORD_ENCODED = DigestAuthUtils.encodePasswordInA1Format(DIGEST_USERNAME, DIGEST_REALM, DIGEST_PASSWORD);

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {

        auth.inMemoryAuthentication()
            .passwordEncoder(NoOpPasswordEncoder.getInstance())
            .withUser(DIGEST_USERNAME)
            .password(DIGEST_PASSWORD_ENCODED)
            .roles(DIGEST_ROLE);
    }

    @Bean
    @Order(1)
    public SecurityFilterChain filterChainDigest(HttpSecurity http) throws Exception {

        return http.antMatcher("/digest/**")
            .authorizeRequests()
            .anyRequest()
            .hasRole(DIGEST_ROLE)
            .and()
            .addFilter(this.digestAuthenticationFilter())
            .csrf(AbstractHttpConfigurer::disable) // Set to lowest position to work
            .exceptionHandling()
            .defaultAuthenticationEntryPointFor(this.digestEntryPoint(), new AntPathRequestMatcher("/digest/**"))
            .and()
            .build();
    }

    public Filter digestAuthenticationFilter() {

        DigestAuthenticationFilter digestAuthenticationFilter = new DigestAuthenticationFilter();
        digestAuthenticationFilter.setAuthenticationEntryPoint(this.digestEntryPoint());
        digestAuthenticationFilter.setUserDetailsService(username -> new MyUserDetails());
        digestAuthenticationFilter.setPasswordAlreadyEncoded(true);

        return digestAuthenticationFilter;
    }

    @Bean
    public DigestAuthenticationEntryPoint digestEntryPoint() {

        DigestAuthenticationEntryPoint digestAuthenticationEntryPoint = new DigestAuthenticationEntryPoint();
        digestAuthenticationEntryPoint.setKey("mykey");
        digestAuthenticationEntryPoint.setRealmName(DIGEST_REALM);

        return digestAuthenticationEntryPoint;
    }

    private static class MyUserDetails implements UserDetails {

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return List.of((GrantedAuthority) () -> DIGEST_ROLE);
        }

        @Override
        public String getPassword() {
            return DIGEST_PASSWORD_ENCODED;
        }

        @Override
        public String getUsername() {
            return DIGEST_USERNAME;
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }
}