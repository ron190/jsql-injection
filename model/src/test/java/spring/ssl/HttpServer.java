package spring.ssl;

import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.http.LegacyCookieProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class HttpServer {
    
    /**
     * $ keytool -genkeypair -alias jsql-injection -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore jsql-injection.p12 -validity 3650
     *  Enter keystore password:  testtest
     *  Re-enter new password: testtest
     *  What is your first and last name?
     *    [Unknown]:
     *  What is the name of your organizational unit?
     *    [Unknown]:
     *  What is the name of your organization?
     *    [Unknown]:
     *  What is the name of your City or Locality?
     *    [Unknown]:
     *  What is the name of your State or Province?
     *    [Unknown]:
     *  What is the two-letter country code for this unit?
     *    [Unknown]:
     *  Is CN=Unknown, OU=Unknown, O=Unknown, L=Unknown, ST=Unknown, C=Unknown correct?
     *    [no]:  yes
     */
    
    @Bean
    public ServletWebServerFactory servletContainer(@Value("${server.http.port}") int httpPort) {
        
        Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
        connector.setPort(httpPort);

        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
        tomcat.addAdditionalTomcatConnectors(connector);
        
        return tomcat;
    }
    
    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> cookieProcessorCustomizer() {
        
        return tomcatServletWebServerFactory -> tomcatServletWebServerFactory.addContextCustomizers(context -> context.setCookieProcessor(new LegacyCookieProcessor()));
    }
}