package spring.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CsrfWebSecurity {

    @Bean
    public SecurityFilterChain filterChainCsrf(HttpSecurity http) throws Exception {

        return http.httpBasic()
            .disable()
            .antMatcher("/csrf/**")
            .csrf()
            .requireCsrfProtectionMatcher(
                new AndRequestMatcher(
                    new DefaultRequiresCsrfMatcher(),
                    new RegexRequestMatcher("/csrf.*", null)
                )
            )
            .csrfTokenRepository(new CookieCsrfTokenRepository())
            .and()
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