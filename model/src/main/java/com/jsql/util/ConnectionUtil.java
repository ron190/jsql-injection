package com.jsql.util;

import java.io.IOException;
import java.net.Authenticator;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jsql.model.InjectionModel;
import com.jsql.model.bean.util.Header;
import com.jsql.model.bean.util.Interaction;
import com.jsql.model.bean.util.Request;
import com.jsql.model.exception.InjectionFailureException;
import com.jsql.model.injection.method.AbstractMethodInjection;
import com.jsql.util.protocol.SessionCookieManager;

/**
 * Utility class in charge of connection to web resources and management
 * of source page and request and response headers.
 * In the same time it allows to fix different lack of functionality induced by
 * library bugs (jcifs) or core design laziness (custom HTTP method).
 */
public class ConnectionUtil {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();
    
    /**
     * URL entered by user
     */
    private String urlByUser;

    /**
     * URL entered by user without the query string
     */
    private String urlBase;

    /**
     * Method of injection: by query string, request or header.
     */
    private AbstractMethodInjection methodInjection;

    /**
     * Default HTTP method. It can be changed to a custom method.
     */
    private String typeRequest = "POST";

    private static final String NO_CACHE = "no-cache";
    
    private Random randomForUserAgent = new Random();
    
    private InjectionModel injectionModel;
    
    public HttpClient getHttpClient() {
        
        HttpClient.Builder httpClientBuilder = HttpClient
            .newBuilder()
            .connectTimeout(Duration.ofSeconds(15))
            .sslContext(CertificateUtil.ignoreCertificationChain())
            .followRedirects(
                injectionModel.getMediatorUtils().getPreferencesUtil().isFollowingRedirection()
                ? HttpClient.Redirect.ALWAYS
                : HttpClient.Redirect.NEVER
            );
        
        if (this.injectionModel.getMediatorUtils().getAuthenticationUtil().isAuthentEnabled()) {
            
            // TODO Make it work for basic, digest, ntlm
            httpClientBuilder.authenticator(new Authenticator() {
              
              @Override
              protected PasswordAuthentication getPasswordAuthentication() {
                  
                  return new PasswordAuthentication (
                      ConnectionUtil.this.injectionModel.getMediatorUtils().getAuthenticationUtil().usernameAuthentication,
                      ConnectionUtil.this.injectionModel.getMediatorUtils().getAuthenticationUtil().passwordAuthentication.toCharArray()
                  );
              }
          });
      }
                
        return httpClientBuilder.build();
    }
    
    public ConnectionUtil(InjectionModel injectionModel) {
        
        this.injectionModel = injectionModel;
    }
    
    /**
     * Check that the connection to the website is working correctly.
     * It uses authentication defined by user, with fixed timeout, and warn
     * user in case of authentication detected.
     * @throws InjectionFailureException when any error occurs during the connection
     */
    public void testConnection() throws InjectionFailureException {

        // Set multithreaded Cookie handler
        // Allows CSRF token processing during ITs, can be used for batch scan
        if (!this.injectionModel.getMediatorUtils().getPreferencesUtil().isNotProcessingCookies()) {
            
            if ("true".equals(System.getenv("FROM_ITS"))) {
                
                CookieHandler.setDefault(SessionCookieManager.getInstance());
                
            } else {
                
                CookieHandler.setDefault(new CookieManager());
            }
            
        } else {
            
            if ("true".equals(System.getenv("FROM_ITS"))) {
                
                SessionCookieManager.getInstance().clear();
            }
            CookieHandler.setDefault(null);
        }

        // Test the HTTP connection
        try {
            Builder httpRequest = HttpRequest
                .newBuilder()
                .uri(
                    URI.create(
                        this.getUrlByUser()
                        // Ignore injection point during the test
                        .replace(InjectionModel.STAR, StringUtils.EMPTY)
                    )
                )
                .setHeader(HeaderUtil.CONTENT_TYPE_REQUEST, "text/plain")
                .timeout(Duration.ofSeconds(15));
            
            // Add headers if exists (Authorization:Basic, etc)
            for (SimpleEntry<String, String> header: this.injectionModel.getMediatorUtils().getParameterUtil().getListHeader()) {
                
                HeaderUtil.sanitizeHeaders(httpRequest, header);
            }

            this.injectionModel.getMediatorUtils().getHeaderUtil()
            .checkResponseHeader(
                httpRequest,
                this.getUrlByUser().replace(InjectionModel.STAR, StringUtils.EMPTY)
            );
            
            // Calling connection.disconnect() is not required, more calls will happen
            
        } catch (Exception e) {
            
            String message = Optional.ofNullable(e.getMessage()).orElse(StringUtils.EMPTY);
            throw new InjectionFailureException("Connection failed: "+ message.replace(e.getClass().getName() +": ", StringUtils.EMPTY), e);
        }
    }
    
    public String getSource(String url, boolean lineFeed) throws IOException {
        
        Map<Header, Object> msgHeader = new EnumMap<>(Header.class);
        msgHeader.put(Header.URL, url);
        
        String pageSource = StringUtils.EMPTY;
        
        try {
            HttpRequest httpRequest =
                HttpRequest
                .newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(15))
                .build();
            
            HttpHeaders httpHeaders;
                
            if (lineFeed) {
                
                HttpResponse<Stream<String>> response = getHttpClient().send(httpRequest, BodyHandlers.ofLines());
                pageSource = response.body().collect(Collectors.joining("\n"));
                httpHeaders = response.headers();
                
            } else {
                
                HttpResponse<String> response = getHttpClient().send(httpRequest, BodyHandlers.ofString());
                pageSource = response.body();
                httpHeaders = response.headers();
            }
            
            Map<String, String> mapHeaders =
                httpHeaders
                .map()
                .entrySet()
                .stream()
                .sorted(Comparator.comparing(Entry::getKey))
                .map(entrySet ->
                    new AbstractMap.SimpleEntry<>(
                        entrySet.getKey(),
                        String.join(", ", entrySet.getValue())
                    )
                )
                .collect(Collectors.toMap(
                    AbstractMap.SimpleEntry::getKey,
                    AbstractMap.SimpleEntry::getValue
                ));
            
            msgHeader.put(Header.RESPONSE, new TreeMap<>(mapHeaders));
            
        } catch (IOException e) {

            LOGGER.log(LogLevel.CONSOLE_JAVA, e.getMessage(), e);
            
        } catch (InterruptedException e) {
            
            LOGGER.log(LogLevel.CONSOLE_JAVA, e.getMessage(), e);
            Thread.currentThread().interrupt();
            
        } finally {
            
            msgHeader.put(Header.SOURCE, pageSource);
            
            // Inform the view about the log infos
            Request request = new Request();
            request.setMessage(Interaction.MESSAGE_HEADER);
            request.setParameters(msgHeader);
            this.injectionModel.sendToViews(request);
        }
        
        return pageSource.trim();
    }
    
    /**
     * Call an URL and return the source page.
     * @param url to call
     * @return the source page of the URL
     * @throws IOException when the reading of source page fails
     */
    public String getSourceLineFeed(String url) throws IOException {
        
        return this.getSource(url, true);
    }
    
    public String getSource(String url) throws IOException {
        
        return this.getSource(url, false);
    }

    public void setCustomUserAgent(Builder httpRequest) {
        
        if (this.injectionModel.getMediatorUtils().getUserAgentUtil().isCustomUserAgent()) {
            
            String agents = this.injectionModel.getMediatorUtils().getUserAgentUtil().getCustomUserAgent();
            List<String> listAgents =
                Stream
                .of(agents.split("[\\r\\n]{1,}"))
                .filter(q -> !q.matches("^#.*"))
                .collect(Collectors.toList());
            
            String randomElement = listAgents.get(this.randomForUserAgent.nextInt(listAgents.size()));
            
            httpRequest.setHeader("User-Agent", randomElement);
        }
    }
    
    
    // Builder

    public ConnectionUtil withMethodInjection(AbstractMethodInjection methodInjection) {
        this.methodInjection = methodInjection;
        return this;
    }
    
    public ConnectionUtil withTypeRequest(String typeRequest) {
        this.typeRequest = typeRequest;
        return this;
    }
    
    
    // Getters and setters
    
    public String getUrlByUser() {
        return this.urlByUser;
    }

    public void setUrlByUser(String urlByUser) {
        this.urlByUser = urlByUser;
    }
    
    public String getUrlBase() {
        return this.urlBase;
    }

    public void setUrlBase(String urlBase) {
        this.urlBase = urlBase;
    }
    
    public AbstractMethodInjection getMethodInjection() {
        return this.methodInjection;
    }

    public void setMethodInjection(AbstractMethodInjection methodInjection) {
        this.methodInjection = methodInjection;
    }
    
    public String getTypeRequest() {
        return this.typeRequest;
    }

    public void setTypeRequest(String typeRequest) {
        this.typeRequest = typeRequest;
    }

    /**
     * Default timeout used by the jcifs fix. It's the default value used usually by the JVM.
     */
    public Integer getTimeout() {
        return this.injectionModel.getMediatorUtils().getPreferencesUtil().countConnectionTimeout() * 1000;
    }
}