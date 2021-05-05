package com.jsql.util;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.jsql.model.InjectionModel;

/**
 * Utility class used to connect to Github Rest webservices.
 * It uses jsql-robot profile to post data to Github.
 */
public class GitUtil {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();
    
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

    private InjectionModel injectionModel;
    
    public GitUtil(InjectionModel injectionModel) {
        
        this.injectionModel = injectionModel;
    }

    /**
     * Verify if application is up to date against the version on Github.
     * @param displayUpdateMessage YES for manual update verification, hidden otherwise
     */
    public void checkUpdate(ShowOnConsole displayUpdateMessage) {
        
        if (displayUpdateMessage == ShowOnConsole.YES) {
            
            LOGGER.log(LogLevel.CONSOLE_DEFAULT, () -> I18nUtil.valueByKey("UPDATE_LOADING"));
        }
        
        try {
            var versionGit = Float.parseFloat(this.getJSONObject().getString("version"));
            
            if (versionGit > Float.parseFloat(this.injectionModel.getVersionJsql())) {
                
                LOGGER.log(LogLevel.CONSOLE_ERROR, () -> I18nUtil.valueByKey("UPDATE_NEW_VERSION"));
                
            } else if (displayUpdateMessage == ShowOnConsole.YES) {
                
                LOGGER.log(LogLevel.CONSOLE_SUCCESS, () -> I18nUtil.valueByKey("UPDATE_UPTODATE"));
            }
            
        } catch (NumberFormatException | IOException | JSONException e) {
            
            LOGGER.log(LogLevel.CONSOLE_ERROR, I18nUtil.valueByKey("UPDATE_EXCEPTION"), e);
        }
    }
    
    /**
     * Define the body of an issue to send to Github for an unhandled exception.
     * It adds different system data to the body and remove sensible data like
     * injection URL.
     * @param threadName name of thread where the exception occurred
     * @param throwable unhandled exception to report to Gihub
     */
    public void sendUnhandledException(String threadName, Throwable throwable) {
        
        String javaVersion = System.getProperty("java.version");
        String osArch = System.getProperty("os.arch");
        
        var osMetadata = String
            .join(
                "\n",
                String.format(
                    "jSQL: v%s",
                    this.injectionModel.getVersionJsql()
                ),
                String.format(
                    "Java: v%s-%s-%s on %s",
                    javaVersion,
                    osArch,
                    System.getProperty("user.language"),
                    System.getProperty("java.runtime.name")
                ),
                String.format(
                    "OS: %s (v%s)",
                    System.getProperty("os.name"), System.getProperty("os.version")
                ),
                String.format(
                    "Desktop: %s",
                    System.getProperty("sun.desktop") != null
                    ? System.getProperty("sun.desktop")
                    : "undefined"
                ),
                String.format(
                    "Strategy: %s",
                    this.injectionModel.getMediatorStrategy().getStrategy() != null
                    ? this.injectionModel.getMediatorStrategy().getStrategy().getName()
                    : "undefined"
                ),
                String.format(
                    "Db engine: %s",
                    this.injectionModel.getMediatorVendor().getVendor().toString()
                )
            );
        
        var exceptionText = String
            .format(
                "Exception on %s%n%s%n",
                threadName,
                ExceptionUtils.getStackTrace(throwable).trim()
            );
        
        var clientDescription = String
            .format(
                "```%n%s%n```%n```%n%s```",
                osMetadata,
                exceptionText
            );
        
        clientDescription = clientDescription.replaceAll("(https?://[.a-zA-Z_0-9]*)+", org.apache.commons.lang3.StringUtils.EMPTY);
          
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

        var httpRequest = HttpRequest
            .newBuilder()
            .uri(URI.create(this.injectionModel.getMediatorUtils().getPropertiesUtil().getProperties().getProperty("github.issues.url")))
            .setHeader(
                "Authorization",
                "token "
                + StringUtils.newStringUtf8(
                    Base64.decodeBase64(
                        this.injectionModel.getMediatorUtils().getPropertiesUtil().getProperties().getProperty("github.token")
                    )
                )
            )
            .POST(BodyPublishers.ofString(
                new JSONObject()
                .put("title", reportTitle)
                .put("body", reportBody)
                .toString()
            ))
            .timeout(Duration.ofSeconds(15))
            .build();
            
        try {
            HttpResponse<String> response = this.injectionModel.getMediatorUtils().getConnectionUtil().getHttpClient().send(httpRequest, BodyHandlers.ofString());
                        
            this.readGithubResponse(response, showOnConsole);
            
        } catch (InterruptedException | IOException e) {
            
            if (showOnConsole == ShowOnConsole.YES) {
                
                LOGGER.log(
                    LogLevel.CONSOLE_ERROR,
                    String.format("Error during Github report connection: %s", e.getMessage()),
                    e
                );
            }
            
            if (e instanceof InterruptedException) {
                
                Thread.currentThread().interrupt();
            }
        }
    }
    
    private void readGithubResponse(HttpResponse<String> response, ShowOnConsole showOnConsole) throws IOException {
        
        try {
            // Read the response
            String sourcePage = response.body();

            if (showOnConsole == ShowOnConsole.YES) {
                
                var jsonObjectResponse = new JSONObject(sourcePage);
                var urlIssue = jsonObjectResponse.getString("html_url");
                LOGGER.log(LogLevel.CONSOLE_SUCCESS, "Sent to Github: {}", urlIssue);
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
            var news = this.getJSONObject().getJSONArray("news");
            
            for (var index = 0 ; index < news.length() ; index++) {
                
                LOGGER.log(LogLevel.CONSOLE_INFORM, news.get(index));
            }
            
        } catch (IOException | JSONException e) {
            
            LOGGER.log(LogLevel.CONSOLE_ERROR, "Connection to the Github API failed", e);
        }
    }
    
    /**
     * Instantiate the jsonObject from json data if not already set.
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

                    LOGGER.log(LogLevel.CONSOLE_ERROR, "Fetching default JSON failed", e);
                }
                
                LOGGER.log(LogLevel.CONSOLE_ERROR, "Fetching configuration from Github failed. Wait for service to be available, check your connection or update jsql", e);
            }
        }
        
        return this.jsonObject;
    }
}
