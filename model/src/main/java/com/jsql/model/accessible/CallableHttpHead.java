package com.jsql.model.accessible;

import com.jsql.model.InjectionModel;
import com.jsql.model.bean.util.Request3;
import com.jsql.util.ConnectionUtil;
import com.jsql.util.LogLevelUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.AbstractMap.SimpleEntry;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

/**
 * Thread unit to test if an administration page exists on the server.
 * The process can be cancelled by the user.
 */
public class CallableHttpHead implements Callable<CallableHttpHead> {
    
    private static final Logger LOGGER = LogManager.getRootLogger();
    
    /**
     * URL to an administration page on the website to get tested.
     */
    private final String urlAdminPage;
    
    /**
     * HTTP header response code.
     */
    private String responseCodeHttp = StringUtils.EMPTY;

    private final InjectionModel injectionModel;

    private final String metadataInjectionProcess;
    
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
     * Call URL to an administration page in HEAD mode and send the result back to view.
     */
    @Override
    public CallableHttpHead call() {
        if (this.injectionModel.getResourceAccess().isSearchAdminStopped()) {
            return this;
        }
        
        try {
            var builderHttpRequest = HttpRequest.newBuilder()
                .uri(URI.create(this.urlAdminPage))
                .method("HEAD", BodyPublishers.noBody())
                .timeout(Duration.ofSeconds(4));
            
            Stream.of(
                this.injectionModel.getMediatorUtils().parameterUtil().getHeaderFromEntries().split("\\\\r\\\\n")
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
            var httpClient = this.injectionModel.getMediatorUtils().connectionUtil().getHttpClient()
                .connectTimeout(Duration.ofSeconds(4))
                .build();
            HttpResponse<Void> response = httpClient.send(httpRequest, BodyHandlers.discarding());

            this.responseCodeHttp = String.valueOf(response.statusCode());

            this.injectionModel.sendToViews(new Request3.MessageHeader(
                this.urlAdminPage,
                null,
                ConnectionUtil.getHeadersMap(httpRequest.headers()),
                ConnectionUtil.getHeadersMap(response),
                null,
                null,
                null,
                this.metadataInjectionProcess,
                null
            ));
        } catch (InterruptedException e) {
            LOGGER.log(LogLevelUtil.IGNORE, e, e);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            var eMessageImplicit = String.format(
                "Problem connecting to %s (implicit reason): %s", 
                this.urlAdminPage,
                InjectionModel.getImplicitReason(e)
            );
            String eMessage = Optional.ofNullable(e.getMessage()).orElse(eMessageImplicit);
            LOGGER.log(LogLevelUtil.CONSOLE_ERROR, eMessage);
        }
        
        return this;
    }

    /**
     * Check if HTTP response is either 2xx or 3xx, which corresponds to
     * an acceptable response from the website.
     * @return true if HTTP code start with 2 or 3
     */
    public boolean isHttpResponseOk() {
        return this.responseCodeHttp.matches("[23]\\d\\d");
    }
    
    
    // Getters
    
    public String getUrl() {
        return this.urlAdminPage;
    }
}