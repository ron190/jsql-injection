package com.jsql.model.accessible;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.AbstractMap.SimpleEntry;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jsql.model.InjectionModel;
import com.jsql.model.bean.util.Header;
import com.jsql.model.bean.util.Interaction;
import com.jsql.model.bean.util.Request;
import com.jsql.util.ConnectionUtil;
import com.jsql.util.LogLevel;

/**
 * Thread unit to test if an administration page exists on the server.
 * The process can be cancelled by the user.
 */
public class CallableHttpHead implements Callable<CallableHttpHead> {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();
    
    /**
     * URL to an administration page on the website to get tested.
     */
    private String urlAdminPage;
    
    /**
     * HTTP header response code.
     */
    private String responseCodeHttp = StringUtils.EMPTY;

    private InjectionModel injectionModel;

    private String metadataInjectionProcess;
    
    /**
     * Create a callable to find admin page.
     * @param urlAdminPage URL of admin page
     */
    public CallableHttpHead(String urlAdminPage, InjectionModel injectionModel, String metadataInjectionProcess) {
        
        this.urlAdminPage = urlAdminPage;
        this.injectionModel= injectionModel;
        this.metadataInjectionProcess= metadataInjectionProcess;
    }

    /**
     * Call URL to a administration page in HEAD mode and send the result back to view.
     */
    @Override
    public CallableHttpHead call() throws Exception {
        
        if (this.injectionModel.getResourceAccess().isSearchAdminStopped()) {
            
            return this;
        }
        
        try {
            var builderHttpRequest = HttpRequest
                .newBuilder()
                .uri(URI.create(this.urlAdminPage))
                .method("HEAD", BodyPublishers.noBody())
                .timeout(Duration.ofSeconds(4));
            
            Stream
            .of(
                this.injectionModel.getMediatorUtils().getParameterUtil().getHeaderFromEntries()
                .split("\\\\r\\\\n")
            )
            .map(e -> {
                
                if (e.split(":").length == 2) {
                    
                    return new SimpleEntry<>(
                        e.split(":")[0],
                        e.split(":")[1]
                    );
                } else {
                    
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .forEach(e -> builderHttpRequest.header(e.getKey(), e.getValue()));
            
            var httpRequest = builderHttpRequest.build();
            
            var httpClient = HttpClient
                .newBuilder()
                .connectTimeout(Duration.ofSeconds(4))
                .build();
            
            HttpResponse<Void> response = httpClient.send(httpRequest, BodyHandlers.discarding());
            
            Map<Header, Object> msgHeader = new EnumMap<>(Header.class);
            msgHeader.put(Header.URL, this.urlAdminPage);
            msgHeader.put(Header.POST, StringUtils.EMPTY);
            msgHeader.put(Header.HEADER, ConnectionUtil.getHeadersMap(httpRequest.headers()));
            msgHeader.put(Header.RESPONSE, ConnectionUtil.getHeadersMap(response));
            msgHeader.put(Header.METADATA_PROCESS, this.metadataInjectionProcess);
            
            var request = new Request();
            request.setMessage(Interaction.MESSAGE_HEADER);
            request.setParameters(msgHeader);
            this.injectionModel.sendToViews(request);
            
        } catch (InterruptedException e) {
            
            LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
            Thread.currentThread().interrupt();
            
        } catch (Exception e) {
            
            var eMessageImplicit = String.format(
                "Problem connecting to %s (implicit reason): %s", 
                this.urlAdminPage,
                InjectionModel.getImplicitReason(e)
            );
            
            String eMessage = Optional.ofNullable(e.getMessage()).orElse(eMessageImplicit);
            
            LOGGER.log(LogLevel.CONSOLE_ERROR, eMessage);
        }
        
        return this;
    }

    /**
     * Check if HTTP response is either 2xx or 3xx, which corresponds to
     * a acceptable response from the website.
     * @return true if HTTP code start with 2 or 3
     */
    public boolean isHttpResponseOk() {
        return this.responseCodeHttp.matches("[23]\\d\\d");
    }
    
    
    // Getters and setters
    
    public String getUrl() {
        return this.urlAdminPage;
    }
}