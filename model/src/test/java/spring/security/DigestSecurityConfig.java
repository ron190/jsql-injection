package spring.security;

import jakarta.servlet.Filter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.authentication.www.DigestAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.DigestAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@Order(3)
public class DigestSecurityConfig {
    
    private static final String DIGEST_AUTHORITY = "ADMIN2";
    private static final String DIGEST_REALM = "Digest Realm";
    public static final String DIGEST_USERNAME = "login-digest";
    public static final String DIGEST_PASSWORD = "password-digest";
    private static final String DIGEST_PASSWORD_ENCODED = DigestAuthUtils.encodePasswordInA1Format(DIGEST_USERNAME, DIGEST_REALM, DIGEST_PASSWORD);
    public static final CustomFilter FILTER = new CustomFilter("digest");

    @Bean
    public SecurityFilterChain filterChainDigest(HttpSecurity http) throws Exception {

        return http.securityMatcher("/digest/**")
            .csrf(Customizer.withDefaults())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(AntPathRequestMatcher.antMatcher("/digest/**")).hasAuthority(DIGEST_AUTHORITY)
            )
            .addFilterAfter(FILTER, AuthorizationFilter.class)
            .addFilter(this.digestAuthenticationFilter())
            .exceptionHandling()
            // Deprecated but no compatible entry point setting available
            .defaultAuthenticationEntryPointFor(this.digestEntryPoint(), new AntPathRequestMatcher("/digest/**"))
            .and()
            .build();
    }

    public Filter digestAuthenticationFilter() {

        UserDetails user = User.builder()
            .username(DIGEST_USERNAME)
            .password(DIGEST_PASSWORD_ENCODED)
            .authorities(DIGEST_AUTHORITY)
            .build();

        DigestAuthenticationFilter digestAuthenticationFilter = new DigestAuthenticationFilter();
        digestAuthenticationFilter.setAuthenticationEntryPoint(this.digestEntryPoint());
        digestAuthenticationFilter.setUserDetailsService(username -> user);
        digestAuthenticationFilter.setPasswordAlreadyEncoded(true);
        digestAuthenticationFilter.setCreateAuthenticatedToken(true);

        return digestAuthenticationFilter;
    }

    @Bean
    public DigestAuthenticationEntryPoint digestEntryPoint() {

        DigestAuthenticationEntryPoint digestAuthenticationEntryPoint = new DigestAuthenticationEntryPoint();
        digestAuthenticationEntryPoint.setKey("mykey");
        digestAuthenticationEntryPoint.setRealmName(DIGEST_REALM);

        return digestAuthenticationEntryPoint;
    }
}