package com.jsql.util;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jsql.i18n.I18n;
import com.jsql.model.InjectionModel;

/**
 * Utility class used to connect to Github Rest webservices.
 * It uses jsql-robot profile to post data to Github.
 */
public class GitUtil {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();
    
    /**
     * Application useful informations as json object from Github repository.
     * Used to get current development version and community news.
     */
    private JSONObject jsonObject;
    
    /**
     * Define explicit labels to declare method parameters.
     * Used for code readability only.
     */
    public enum ShowOnConsole {
        YES,
        NO;
    }

    public GitUtil(InjectionModel injectionModel) {
        this.injectionModel = injectionModel;
    }
    InjectionModel injectionModel;

    /**
     * Verify if application is up to date against the version on Github.
     * @param displayUpdateMessage YES for manual update verification, hidden otherwise
     */
    public void checkUpdate(ShowOnConsole displayUpdateMessage) {
        if (displayUpdateMessage == ShowOnConsole.YES) {
            LOGGER.trace(I18n.valueByKey("UPDATE_LOADING"));
        }
        
        try {
            Float versionGit = Float.parseFloat(this.getJSONObject().getString("version"));
            if (versionGit > Float.parseFloat(this.injectionModel.getVersionJsql())) {
                LOGGER.warn(I18n.valueByKey("UPDATE_NEW_VERSION"));
            } else if(displayUpdateMessage == ShowOnConsole.YES) {
                LOGGER.debug(I18n.valueByKey("UPDATE_UPTODATE"));
            }
        } catch (NumberFormatException | IOException | JSONException e) {
            LOGGER.warn(I18n.valueByKey("UPDATE_EXCEPTION"), e);
        }
    }
    
    /**
     * Define the body of an issue to send to Github for an unhandled exception.
     * It adds different system data to the body and remove sensible data like
     * injection URL.
     * @param threadName name of thread where the exception occured
     * @param throwable unhandled exception to report to Gihub
     */
    public void sendUnhandledException(String threadName, Throwable throwable) {
        String javaVersion = System.getProperty("java.version");
        String osArch = System.getProperty("os.arch");
        
        String clientDescription =
              "```\n"
            + "jSQL: v"+ this.injectionModel.getVersionJsql() +"\n"
            + "Java: v"+ javaVersion +"-"+ osArch +"-"+ System.getProperty("user.language") +" on "+ System.getProperty("java.runtime.name") +"\n"
            + "OS: "+ System.getProperty("os.name") +" (v"+ System.getProperty("os.version") +")\n"
            + "Desktop: "+( System.getProperty("sun.desktop") != null ? System.getProperty("sun.desktop") : "undefined" )+"\n"
            + "Strategy: "+( this.injectionModel.getMediatorStrategy().getStrategy() != null ? this.injectionModel.getMediatorStrategy().getStrategy().getName() : "undefined" )+"\n"
            + "Db engine: "+ this.injectionModel.getMediatorVendor().getVendor().toString() +"\n"
            + "```\n"
            + "```\n"
            + "Exception on "+ threadName +"\n"
            + ExceptionUtils.getStackTrace(throwable).trim() +"\n"
            + "```";
        
        clientDescription = clientDescription.replaceAll("(https?://[.a-zA-Z_0-9]*)+", "");
          
        this.sendReport(clientDescription, ShowOnConsole.NO, "Unhandled "+ throwable.getClass().getSimpleName());
    }
    
    /**
     * Connect to Github webservices and create an Issue on the repository.
     * Used by translation protocol, unhandled exception detection and manual Issue reporting.
     * @param reportBody text of the Issue
     * @param showOnConsole in case of manual Issue reporting. Hidden in case of automatic reporting of unhandled exception.
     * @param reportTitle title of the Issue
     */
    public void sendReport(String reportBody, ShowOnConsole showOnConsole, String reportTitle) {
        // Check proxy
        if (!this.injectionModel.getMediatorUtils().getProxyUtil().isLive(showOnConsole)) {
            return;
        }

        // Connect to Github webservice
        HttpURLConnection connection = null;
        try {
            URL githubUrl = new URL(
                this.injectionModel.getMediatorUtils().getPropertiesUtil().getProperties().getProperty("github.issues.url")
            );

            connection = (HttpURLConnection) githubUrl.openConnection();
            connection.setDefaultUseCaches(false);
            connection.setUseCaches(false);
            connection.setRequestProperty("Pragma", "no-cache");
            connection.setRequestProperty("Cache-Control", "no-cache");
            connection.setRequestProperty("Expires", "-1");
            connection.setRequestProperty("Content-Type", "application/json");
            
            // Authenticate as jsql-robot
            connection.setRequestProperty(
                "Authorization",
                "token "
                + StringUtils.newStringUtf8(
                    Base64.decodeBase64(
                        this.injectionModel.getMediatorUtils().getPropertiesUtil().getProperties().getProperty("github.token")
                    )
                )
            );
            
            connection.setReadTimeout(this.injectionModel.getMediatorUtils().getConnectionUtil().getTimeout());
            connection.setConnectTimeout(this.injectionModel.getMediatorUtils().getConnectionUtil().getTimeout());
            connection.setDoOutput(true);

            // Set the content of the Issue
            DataOutputStream dataOut = new DataOutputStream(connection.getOutputStream());
            dataOut.writeBytes(
                new JSONObject()
                    .put("title", reportTitle)
                    .put("body", StringUtil.decimalHtmlEncode(reportBody))
                    .toString()
            );
            dataOut.flush();
            dataOut.close();
            
            this.readGithubResponse(connection, showOnConsole);
        } catch (IOException | NoClassDefFoundError | JSONException e) {
            // Fix #27623: NoClassDefFoundError on getOutputStream()
            // Implemented by jcifs.http.NtlmHttpURLConnection.getOutputStream()
            if (showOnConsole == ShowOnConsole.YES) {
                LOGGER.warn("Error during Github report connection: "+ e.getMessage(), e);
            }
        }
    }
    
    private void readGithubResponse(HttpURLConnection connection, ShowOnConsole showOnConsole) throws IOException {
        try {
            // Read the response
            String sourcePage = ConnectionUtil.getSourceLineFeed(connection);

            if (showOnConsole == ShowOnConsole.YES) {
                JSONObject jsonObjectResponse = new JSONObject(sourcePage);
                String urlIssue = jsonObjectResponse.getString("html_url");
                LOGGER.debug("Sent to Github: "+ urlIssue);
            }
        } catch (Exception e) {
            throw new IOException("Connection to the Github API failed, check your connection or update jsql");
        }
    }
    
    /**
     * Displays news informations on the console from Github web service.
     * Infos concern the general roadmap for the application, current development status
     * and other useful statements for the community.
     */
    public void showNews() {
        try {
            JSONArray news = this.getJSONObject().getJSONArray("news");
            for (int index = 0 ; index < news.length() ; index++) {
                LOGGER.info(news.get(index));
            }
        } catch (IOException | JSONException e) {
            LOGGER.warn("Connection to the Github API failed", e);
        }
    }
    
    /**
     * Instanciate the jsonObject from json data if not already set.
     * @return jsonObject describing json data
     * @throws IOException if connection to json data fails
     */
    public JSONObject getJSONObject() throws IOException {
        if (this.jsonObject == null) {
            String json = this.injectionModel.getMediatorUtils().getConnectionUtil().getSource(
                this.injectionModel.getMediatorUtils().getPropertiesUtil().getProperties().getProperty("github.webservice.url")
            );
            
            // Fix #45349: JSONException on new JSONObject(json)
            try {
                this.jsonObject = new JSONObject(json);
            } catch (JSONException e) {
                try {
                    this.jsonObject = new JSONObject("{\"version\": \"0\", \"news\": []}");
                } catch (JSONException e1) {
                    // TODO Simplify
                    LOGGER.warn("Fetching default JSON failed", e);
                }
                LOGGER.warn("Fetching JSON configuration from Github failed, check your connection or update jsql", e);
            }
        }
        return this.jsonObject;
    }
    
}
