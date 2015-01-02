package com.jsql.model.accessible;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import com.jsql.model.bean.Request;
import com.jsql.model.injection.InjectionModel;
import com.jsql.model.injection.MediatorModel;
import com.jsql.tool.ToolsString;

/**
 * Callable for admin page finder.
 */
public class CallableAdminPage implements Callable<CallableAdminPage> {
    /**
     * url: SQL query
     */
    private String url;
    
    /**
     * HTTP header response code.
     */
    private String responseCodeHTTP;

    /**
     * Create a callable to find admin page.
     * @param url URL of admin page
     */
    public CallableAdminPage(String url) {
        this.url = url;
    }

    @Override
    public CallableAdminPage call() throws Exception {
        if (!MediatorModel.model().ressourceAccessObject.endAdminSearch) {
            URL targetUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) targetUrl.openConnection();
            connection.setRequestMethod("HEAD");
            responseCodeHTTP = connection.getHeaderField(0);

            Map<String, Object> msgHeader = new HashMap<String, Object>();
            msgHeader.put("Url", url);
            msgHeader.put("Cookie", "");
            msgHeader.put("Post", "");
            msgHeader.put("Header", "");
            msgHeader.put("Response", ToolsString.getHTTPHeaders(connection));

            connection.disconnect();

            Request request = new Request();
            request.setMessage("MessageHeader");
            request.setParameters(msgHeader);
            MediatorModel.model().interact(request);
        }
        return this;
    }
    
    public String getUrl() {
        return url;
    }

    public String getResponseCodeHTTP() {
        return responseCodeHTTP;
    }
}