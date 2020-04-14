package spring.security;

import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.authentication.www.DigestAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.DigestAuthenticationFilter;

@Configuration
@EnableWebSecurity
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    
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
 
    private static final String BASIC_REALM = "Basic Realm";
    public static final String BASIC_USERNAME = "a";
    public static final String BASIC_PASSWORD = "a";
    
    private static final String DIGEST_REALM = "myrealm";
    public static final String DIGEST_USERNAME = "user";
    public static final String DIGEST_PASSWORD = "p@ssw0rd";
    private static final String DIGEST_PASSWORD_ENCODED = DigestAuthUtils.encodePasswordInA1Format(DIGEST_USERNAME, DIGEST_REALM, DIGEST_PASSWORD);
     
    @Autowired
    public void configureGlobalSecurity(AuthenticationManagerBuilder auth) throws Exception {
        
        auth
        .inMemoryAuthentication()
        .withUser(BASIC_USERNAME)
        .password(this.passwordEncoder().encode(BASIC_PASSWORD))
        .roles("ADMIN1");
        
        auth
        .inMemoryAuthentication()
        .passwordEncoder(NoOpPasswordEncoder.getInstance())
        .withUser(DIGEST_USERNAME)
        .password(DIGEST_PASSWORD_ENCODED)
        .roles("ADMIN2");
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
      
        http
        .csrf()
        .disable()
        .authorizeRequests()
        .antMatchers("/basic/**")
        .hasRole("ADMIN1")
        .and()
        .httpBasic()
        .realmName(BASIC_REALM)
        .authenticationEntryPoint(this.getBasicAuthEntryPoint())
        .and()
        .authorizeRequests()
        .antMatchers("/digest/**")
        .hasRole("ADMIN2")
        .and()
        .addFilterBefore(this.digestAuthenticationFilter(), BasicAuthenticationFilter.class)
        .httpBasic()
        .realmName(DIGEST_REALM)
        .authenticationEntryPoint(this.digestEntryPoint())
        .and()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    public Filter digestAuthenticationFilter() throws Exception {
        
        DigestAuthenticationFilter digestAuthenticationFilter = new DigestAuthenticationFilter();
        digestAuthenticationFilter.setAuthenticationEntryPoint(this.digestEntryPoint());
        digestAuthenticationFilter.setUserDetailsService(this.userDetailsServiceBean());
        digestAuthenticationFilter.setPasswordAlreadyEncoded(true);
        
        return digestAuthenticationFilter;
    }
    
    @Override
    @Bean
    public UserDetailsService userDetailsServiceBean() throws Exception {
        
        return super.userDetailsServiceBean();
    }

    @Bean
    public DigestAuthenticationEntryPoint digestEntryPoint() {
        
        DigestAuthenticationEntryPoint digestAuthenticationEntryPoint = new DigestAuthenticationEntryPoint();
        digestAuthenticationEntryPoint.setKey("mykey");
        digestAuthenticationEntryPoint.setRealmName(DIGEST_REALM);
        
        return digestAuthenticationEntryPoint;
    }
     
    @Bean
    public CustomBasicAuthenticationEntryPoint getBasicAuthEntryPoint(){
        
        return new CustomBasicAuthenticationEntryPoint();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        
        return new BCryptPasswordEncoder();
    }
}