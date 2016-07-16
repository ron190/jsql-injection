package com.jsql.model.accessible;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import com.jsql.model.MediatorModel;
import com.jsql.model.bean.util.Request;
import com.jsql.util.StringUtil;

/**
 * Callable for admin page finder.
 */
public class CallableAdminPage implements Callable<CallableAdminPage> {
    /**
     * url: SQL query
     */
    private String urlAdminPage;
    
    /**
     * HTTP header response code.
     */
    private String responseCodeHTTP;

    /**
     * Create a callable to find admin page.
     * @param urlAdminPage URL of admin page
     */
    public CallableAdminPage(String urlAdminPage) {
        this.urlAdminPage = urlAdminPage;
    }

    @Override
    public CallableAdminPage call() throws Exception {
        if (!RessourceAccess.isSearchAdminStopped) {
            URL targetUrl = new URL(urlAdminPage);
            HttpURLConnection connection = (HttpURLConnection) targetUrl.openConnection();
            connection.setRequestMethod("HEAD");
            responseCodeHTTP = connection.getHeaderField(0);
            
            if (responseCodeHTTP == null) {
                responseCodeHTTP = "";
            }

            Map<String, Object> msgHeader = new HashMap<>();
            msgHeader.put("Url", urlAdminPage);
            msgHeader.put("Post", "");
            msgHeader.put("Header", "");
            msgHeader.put("Response", StringUtil.getHTTPHeaders(connection));

            Request request = new Request();
            request.setMessage("MessageHeader");
            request.setParameters(msgHeader);
            MediatorModel.model().sendToViews(request);
        }
        return this;
    }
    
    public String getUrl() {
        return urlAdminPage;
    }

    public boolean isHttpResponseOk() {
        return responseCodeHTTP.indexOf("200 OK") >= 0;
    }
}