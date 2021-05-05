package spring.security;

import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.authentication.www.DigestAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.DigestAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@SuppressWarnings("deprecation")
@Configuration
@EnableWebSecurity
@Order(1)
public class DigestWebSecurity extends WebSecurityConfigurerAdapter {
    
    /**
     * url | header | global | ok | jcifs
     *     basic: n|y|n|y|n
     *    digest: n|n|y|y|n
     * negotiate:
     *      ntlm:
     * 
     * proxy:
     * httpd.conf
     * Define SRVROOT "E:\Dev\httpd-2.4.43-win64-VS16\Apache24"
     * LoadModule access_compat_module modules/mod_access_compat.so
     * LoadModule proxy_module modules/mod_proxy.so
     * LoadModule proxy_connect_module modules/mod_proxy_connect.so
     * LoadModule proxy_html_module modules/mod_proxy_html.so
     * LoadModule proxy_http_module modules/mod_proxy_http.so
     * LoadModule proxy_http2_module modules/mod_proxy_http2.so
     * LoadModule xml2enc_module modules/mod_xml2enc.so
     * 
     * proxy-html.conf
     * LoadFile    E:\Dev\httpd-2.4.43-win64-VS16\Apache24\bin\zlib1.dll
     * LoadFile    E:\Dev\httpd-2.4.43-win64-VS16\Apache24\bin\libapriconv-1.dll
     * LoadFile    E:\Dev\httpd-2.4.43-win64-VS16\Apache24\bin\libxml2.dll
     * LoadModule  proxy_html_module   modules/mod_proxy_html.so
     * LoadModule  xml2enc_module      modules/mod_xml2enc.so
     * 
     * ProxyRequests On
     * ProxyVia On
     * 
     * <Proxy *>
     * AddDefaultCharset off
     * Order deny,allow
     * Allow from all
     * </Proxy>
     */
    
    /**
     * ssh-host-config
     * /usr/sbin/sshd
     * ssh -D 9999 -C Watthieu-x64@127.0.0.1
     */
 
    private static final String DIGEST_ROLE = "ADMIN2";
    private static final String DIGEST_REALM = "Digest Realm";
    public static final String DIGEST_USERNAME = "login-digest";
    public static final String DIGEST_PASSWORD = "password-digest";
    private static final String DIGEST_PASSWORD_ENCODED = DigestAuthUtils.encodePasswordInA1Format(DIGEST_USERNAME, DIGEST_REALM, DIGEST_PASSWORD);
     
    @Autowired
    public void configureGlobalSecurity(AuthenticationManagerBuilder auth) throws Exception {
        
        auth
        .inMemoryAuthentication()
        .passwordEncoder(NoOpPasswordEncoder.getInstance())
        .withUser(DIGEST_USERNAME)
        .password(DIGEST_PASSWORD_ENCODED)
        .roles(DIGEST_ROLE);
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
      
        http
        .antMatcher("/digest/**")
        .authorizeRequests()
        .anyRequest()
        .hasRole(DIGEST_ROLE)
        .and()
        .addFilter(this.digestAuthenticationFilter())
        .csrf(csrf -> csrf.disable()) // Set to lowest position to work
        .exceptionHandling()
        .defaultAuthenticationEntryPointFor(this.digestEntryPoint(), new AntPathRequestMatcher("/digest/**"))
        ;
    }

    public Filter digestAuthenticationFilter() throws Exception {
        
        DigestAuthenticationFilter digestAuthenticationFilter = new DigestAuthenticationFilter();
        digestAuthenticationFilter.setAuthenticationEntryPoint(this.digestEntryPoint());
        digestAuthenticationFilter.setUserDetailsService(this.userDetailsServiceBean());
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
}