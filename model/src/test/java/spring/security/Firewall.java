package spring.security;

import com.test.method.CustomMethodSuiteIT;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
public class Firewall {

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(HttpFirewall firewall) {
        return web -> web.httpFirewall(firewall);
    }

    @Bean
    public StrictHttpFirewall httpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();

        List<String> httpMethods = Stream.concat(
            Stream.of(CustomMethodSuiteIT.CUSTOM_METHOD),
            Arrays.stream(RequestMethod.values()).map(Enum::name)
        ).collect(Collectors.toList());

        firewall.setAllowedHttpMethods(httpMethods);
        firewall.setUnsafeAllowAnyHttpMethod(true);
        return firewall;
    }
}
