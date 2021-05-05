package spring.security;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
@EnableWebSecurity
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CsrfWebSecurity extends WebSecurityConfigurerAdapter {
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
      
        http
        .antMatcher("/csrf/**")
        .csrf()
        .requireCsrfProtectionMatcher(
            new AndRequestMatcher(
                new DefaultRequiresCsrfMatcher(),
                new RegexRequestMatcher("/csrf.*", null)
            )
        )
        .csrfTokenRepository(new CookieCsrfTokenRepository())
        ;
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