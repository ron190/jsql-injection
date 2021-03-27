package com.jsql.model.accessible;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.AbstractMap;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jsql.model.InjectionModel;
import com.jsql.model.bean.util.Header;
import com.jsql.model.bean.util.Interaction;
import com.jsql.model.bean.util.Request;

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
        
        boolean isUrlIncorrect = false;
        
        URL targetUrl = null;
        try {
            targetUrl = new URL(this.urlAdminPage);
        } catch (MalformedURLException e) {
            isUrlIncorrect = true;
        }
        
        if (
            this.injectionModel.getResourceAccess().isSearchAdminStopped()
            || isUrlIncorrect
            || StringUtils.isEmpty(targetUrl.getHost())
        ) {
            LOGGER.warn("Incorrect URL: {}", this.urlAdminPage);
            return this;
        }
        
        HttpRequest httpRequest =
            HttpRequest
            .newBuilder()
            .uri(URI.create(this.urlAdminPage))
            .method("HEAD", BodyPublishers.noBody())
            .timeout(Duration.ofSeconds(15))
            .build();
        
        HttpClient httpClient =
            HttpClient
            .newBuilder()
            .connectTimeout(Duration.ofSeconds(15))
            .build();
            
        HttpResponse<Void> response = httpClient.send(httpRequest, BodyHandlers.discarding());
        HttpHeaders httpHeaders = response.headers();
        
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
        
        
        this.responseCodeHttp = ""+ response.statusCode();
        mapHeaders.put(":status", this.responseCodeHttp);

        Map<Header, Object> msgHeader = new EnumMap<>(Header.class);
        msgHeader.put(Header.URL, this.urlAdminPage);
        msgHeader.put(Header.POST, StringUtils.EMPTY);
        msgHeader.put(Header.HEADER, StringUtils.EMPTY);
        msgHeader.put(Header.RESPONSE, new TreeMap<>(mapHeaders));
        msgHeader.put(Header.METADATA_PROCESS, this.metadataInjectionProcess);

        Request request = new Request();
        request.setMessage(Interaction.MESSAGE_HEADER);
        request.setParameters(msgHeader);
        this.injectionModel.sendToViews(request);
        
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