package com.jsql.model.ao;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import com.jsql.model.InjectionModel;
import com.jsql.model.bean.Request;
import com.jsql.tool.StringTool;
import com.jsql.view.GUIMediator;

/**
 * Callable for admin page finder.
 */
public class AdminPageCallable implements Callable<AdminPageCallable> {
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
    public AdminPageCallable(String url) {
        this.url = url;
    }

    @Override
    public AdminPageCallable call() throws Exception {
        if (!InjectionModel.RAO.endAdminSearch) {
            URL targetUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) targetUrl.openConnection();
            connection.setRequestMethod("HEAD");
            responseCodeHTTP = connection.getHeaderField(0);

            Map<String, Object> msgHeader = new HashMap<String, Object>();
            msgHeader.put("Url", url);
            msgHeader.put("Cookie", "");
            msgHeader.put("Post", "");
            msgHeader.put("Header", "");
            msgHeader.put("Response", StringTool.getHTTPHeaders(connection));

            connection.disconnect();

            Request request = new Request();
            request.setMessage("MessageHeader");
            request.setParameters(msgHeader);
            GUIMediator.model().interact(request);
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