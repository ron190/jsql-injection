package spring.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
@Order(2)
public class CsrfWebSecurity {

    public static final CustomFilter FILTER = new CustomFilter("csrf");

    @Bean
    public SecurityFilterChain filterChainCsrf(HttpSecurity http) throws Exception {

        CookieCsrfTokenRepository tokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
        requestHandler.setCsrfRequestAttributeName("_csrf");

        return http.securityMatcher("/csrf/**")
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(AntPathRequestMatcher.antMatcher("/csrf/**")).permitAll()
            )
            .csrf((csrf) -> csrf
                .csrfTokenRepository(tokenRepository)
                .csrfTokenRequestHandler(requestHandler)
                .requireCsrfProtectionMatcher(
                    new AndRequestMatcher(
                        new DefaultRequiresCsrfMatcher(),
                        new RegexRequestMatcher("/csrf.*", null)
                    )
                )
            )
            .addFilterAfter(FILTER, AuthorizationFilter.class)
            .exceptionHandling(Customizer.withDefaults())
            .build();
    }

    /**
     * Allow Csrf also for GET.
     * The default is to ignore GET, HEAD, TRACE, OPTIONS and process all other requests.
     */
    private static final class DefaultRequiresCsrfMatcher implements RequestMatcher {

        @Override
        public boolean matches(HttpServletRequest request) {
            return true;
        }
    }
}