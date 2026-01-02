package spring.ssl;

import org.apache.catalina.Context;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.scan.StandardJarScanner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.tomcat.servlet.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.servlet.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class HttpServer {

    /**
     * $ keytool -genkeypair -alias jsql-injection -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore jsql-injection.p12 -validity 3650
     *  Enter keystore password:  testtest
     *  Re-enter new password: testtest
     *  then leave all default answer, and accept 'yes'
     */
    
    @Bean
    public ServletWebServerFactory servletContainer(@Value("${server.http.port}") int httpPort) {
        Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
        connector.setPort(httpPort);

        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {  // no effect
            @Override
            protected void postProcessContext(Context context) {
                ((StandardJarScanner)context.getJarScanner()).setScanManifest(false);
                context.setAddWebinfClassesResources(true);
                context.setReloadable(true);
                Wrapper jsp = (Wrapper) context.findChild("jsp");
                jsp.addInitParameter("modificationTestInterval", "0");
                jsp.addInitParameter("development","true");
            }
        };
        tomcat.addAdditionalConnectors(connector);
        return tomcat;
    }
}