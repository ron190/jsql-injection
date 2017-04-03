package com.jsql.model.accessible;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.ObjectUtils;

import com.jsql.model.MediatorModel;
import com.jsql.model.bean.util.Request;
import com.jsql.model.bean.util.TypeHeader;
import com.jsql.model.bean.util.TypeRequest;
import com.jsql.util.ConnectionUtil;

/**
 * Thread unit to test if an administration page exists on the server.
 * The process can be cancelled by the user.
 */
public class CallableAdminPage implements Callable<CallableAdminPage> {
	
    /**
     * URL to an administration page on the website to get tested.
     */
    private String urlAdminPage;
    
    /**
     * HTTP header response code.
     */
    private String responseCodeHTTP = "";

    /**
     * Create a callable to find admin page.
     * @param urlAdminPage URL of admin page
     */
    public CallableAdminPage(String urlAdminPage) {
        this.urlAdminPage = urlAdminPage;
    }

    /**
     * Call URL to a administration page in HEAD mode and send the result back to view.
     */
    @Override
    public CallableAdminPage call() throws Exception {
        if (!RessourceAccess.isSearchAdminStopped()) {
            URL targetUrl = new URL(urlAdminPage);
            HttpURLConnection connection = (HttpURLConnection) targetUrl.openConnection();
            
            connection.setRequestProperty("Pragma", "no-cache");
            connection.setRequestProperty("Cache-Control", "no-cache");
            connection.setRequestProperty("Expires", "-1");
            
            connection.setRequestMethod("HEAD");
            responseCodeHTTP = ObjectUtils.firstNonNull(connection.getHeaderField(0), "");

            Map<TypeHeader, Object> msgHeader = new EnumMap<>(TypeHeader.class);
            msgHeader.put(TypeHeader.URL, urlAdminPage);
            msgHeader.put(TypeHeader.POST, "");
            msgHeader.put(TypeHeader.HEADER, "");
            msgHeader.put(TypeHeader.RESPONSE, ConnectionUtil.getHttpHeaders(connection));

            Request request = new Request();
            request.setMessage(TypeRequest.MESSAGE_HEADER);
            request.setParameters(msgHeader);
            MediatorModel.model().sendToViews(request);
        }
        return this;
    }

    /**
     * Check if HTTP response is either 2xx or 3xx, which corrsponds to
     * a acceptable response from the website.
     * @return true if HTTP code start with 2 or 3
     */
    public boolean isHttpResponseOk() {
        return responseCodeHTTP.matches(".+[23]\\d\\d.+");
    }
    
    // Getters and setters
    
    public String getUrl() {
        return urlAdminPage;
    }
    
}