package com.jsql.model.accessible;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.Callable;

import com.jsql.model.MediatorModel;
import com.jsql.model.bean.util.Request;
import com.jsql.model.bean.util.TypeHeader;
import com.jsql.model.bean.util.TypeRequest;
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
    private String responseCodeHTTP = "";

    /**
     * Create a callable to find admin page.
     * @param urlAdminPage URL of admin page
     */
    public CallableAdminPage(String urlAdminPage) {
        this.urlAdminPage = urlAdminPage;
    }

    @Override
    public CallableAdminPage call() throws Exception {
        if (!RessourceAccess.isSearchAdminStopped()) {
            URL targetUrl = new URL(urlAdminPage);
            HttpURLConnection connection = (HttpURLConnection) targetUrl.openConnection();
            
            connection.setRequestProperty("Pragma", "no-cache");
            connection.setRequestProperty("Cache-Control", "no-cache");
            connection.setRequestProperty("Expires", "-1");
            
            connection.setRequestMethod("HEAD");
            responseCodeHTTP = connection.getHeaderField(0);
            
            if (responseCodeHTTP == null) {
                responseCodeHTTP = "";
            }

            Map<TypeHeader, Object> msgHeader = new EnumMap<>(TypeHeader.class);
            msgHeader.put(TypeHeader.URL, urlAdminPage);
            msgHeader.put(TypeHeader.POST, "");
            msgHeader.put(TypeHeader.HEADER, "");
            msgHeader.put(TypeHeader.RESPONSE, StringUtil.getHttpHeaders(connection));

            Request request = new Request();
            request.setMessage(TypeRequest.MESSAGE_HEADER);
            request.setParameters(msgHeader);
            MediatorModel.model().sendToViews(request);
        }
        return this;
    }
    
    public String getUrl() {
        return urlAdminPage;
    }

    public boolean isHttpResponseOk() {
        return responseCodeHTTP.matches(".+[23]\\d\\d.+");
    }
}