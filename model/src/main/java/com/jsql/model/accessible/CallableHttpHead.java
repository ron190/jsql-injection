package com.jsql.model.accessible;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jsql.model.InjectionModel;
import com.jsql.model.bean.util.Header;
import com.jsql.model.bean.util.Interaction;
import com.jsql.model.bean.util.Request;
import com.jsql.util.HeaderUtil;

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
            LOGGER.warn("Incorrect URL: "+ this.urlAdminPage);
            return this;
        }
            
        HttpURLConnection connection = (HttpURLConnection) targetUrl.openConnection();
        
        connection.setRequestProperty("Pragma", "no-cache");
        connection.setRequestProperty("Cache-Control", "no-cache");
        connection.setRequestProperty("Expires", "-1");
        
        connection.setRequestMethod("HEAD");
        this.responseCodeHttp = ObjectUtils.firstNonNull(connection.getHeaderField(0), StringUtils.EMPTY);

        Map<Header, Object> msgHeader = new EnumMap<>(Header.class);
        msgHeader.put(Header.URL, this.urlAdminPage);
        msgHeader.put(Header.POST, StringUtils.EMPTY);
        msgHeader.put(Header.HEADER, StringUtils.EMPTY);
        msgHeader.put(Header.RESPONSE, HeaderUtil.getHttpHeaders(connection));
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
        return this.responseCodeHttp.matches(".+[23]\\d\\d.+");
    }
    
    
    // Getters and setters
    
    public String getUrl() {
        return this.urlAdminPage;
    }
}