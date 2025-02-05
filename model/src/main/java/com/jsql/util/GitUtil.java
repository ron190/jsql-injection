package com.jsql.util;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.JSqlRuntimeException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.Locale;

/**
 * Utility class used to connect to GitHub Rest webservices.
 * It uses jsql-robot profile to post data to GitHub.
 */
public class GitUtil {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();
    
    /**
     * Application useful information as json object from GitHub repository.
     * Used to get current development version and community news.
     */
    private JSONObject jsonObject;
    
    /**
     * Define explicit labels to declare method parameters.
     * Used for code readability only.
     */
    public enum ShowOnConsole { YES, NO }

    private final InjectionModel injectionModel;
    
    public GitUtil(InjectionModel injectionModel) {
        this.injectionModel = injectionModel;
    }

    /**
     * Verify if application is up-to-date against the version on GitHub.
     * @param displayUpdateMessage YES for manual update verification, hidden otherwise
     */
    public void checkUpdate(ShowOnConsole displayUpdateMessage) {
        if (displayUpdateMessage == ShowOnConsole.YES) {
            LOGGER.log(LogLevelUtil.CONSOLE_DEFAULT, () -> I18nUtil.valueByKey("UPDATE_LOADING"));
        }
        try {
            var versionGit = Float.parseFloat(this.callService().getString("version"));
            if (versionGit > Float.parseFloat(this.injectionModel.getPropertiesUtil().getVersionJsql())) {
                LOGGER.log(LogLevelUtil.CONSOLE_ERROR, () -> I18nUtil.valueByKey("UPDATE_NEW_VERSION"));
            } else if (displayUpdateMessage == ShowOnConsole.YES) {
                LOGGER.log(LogLevelUtil.CONSOLE_SUCCESS, () -> I18nUtil.valueByKey("UPDATE_UPTODATE"));
            }
        } catch (NumberFormatException | JSONException e) {
            LOGGER.log(LogLevelUtil.CONSOLE_ERROR, I18nUtil.valueByKey("UPDATE_EXCEPTION"));
        }
    }

    /**
     * Define the body of an issue to send to GitHub for an unhandled exception.
     * It adds different system data to the body and remove sensible data like
     * injection URL.
     * @param threadName name of thread where the exception occurred
     * @param throwable unhandled exception to report to GitHub
     */
    public void sendUnhandledException(String threadName, Throwable throwable) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) screenSize.getWidth();
        int height = (int) screenSize.getHeight();

        var osMetadata = String.join(
            "\n",
            String.format(
                "jSQL: v%s",
                this.injectionModel.getPropertiesUtil().getVersionJsql()
            ),
            String.format(
                "Java: v%s-%s-%s on %s%s",
                SystemUtils.JAVA_VERSION,
                SystemUtils.OS_ARCH,
                SystemUtils.USER_LANGUAGE,
                SystemUtils.JAVA_VENDOR,
                SystemUtils.JAVA_RUNTIME_NAME
            ),
            String.format(
                "OS: %s (v%s)",
                SystemUtils.OS_NAME, SystemUtils.OS_VERSION
            ),
            String.format(
                "Display: %s (%sx%s, %s)",
                UIManager.getLookAndFeel().getName(),
                width,
                height,
                Locale.getDefault().getLanguage()
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
        
        var exceptionText = String.format("Exception on %s%n%s%n", threadName, ExceptionUtils.getStackTrace(throwable).trim());
        var clientDescription = String.format("```yaml%n%s%n```%n```java%n%s```", osMetadata, exceptionText);
        
        clientDescription = clientDescription.replaceAll("(https?://[.a-zA-Z_0-9]*)+", StringUtils.EMPTY);
        this.sendReport(clientDescription, ShowOnConsole.NO, "Unhandled "+ throwable.getClass().getSimpleName());
    }
    
    /**
     * Connect to GitHub webservices and create an Issue on the repository.
     * Used by translation protocol, unhandled exception detection and manual Issue reporting.
     * @param reportBody text of the Issue
     * @param showOnConsole in case of manual Issue reporting. Hidden in case of automatic reporting of unhandled exception.
     * @param reportTitle title of the Issue
     */
    public void sendReport(String reportBody, ShowOnConsole showOnConsole, String reportTitle) {
        if (this.injectionModel.getMediatorUtils().getProxyUtil().isNotLive(showOnConsole)) {
            return;
        }

        String token;
        try {
            token = StringUtil.fromHexZip(
                this.injectionModel.getMediatorUtils().getPropertiesUtil().getProperty("github.token")
            );
        } catch (IOException e) {
            throw new JSqlRuntimeException(e);
        }

        var httpRequest = HttpRequest.newBuilder()
            .uri(URI.create(this.injectionModel.getMediatorUtils().getPropertiesUtil().getProperty("github.issues.url")))
            .setHeader("Authorization", "token "+ token)
            .POST(BodyPublishers.ofString(
                new JSONObject()
                .put("title", reportTitle)
                .put("body", reportBody)
                .toString()
            ))
            .timeout(Duration.ofSeconds(15))
            .build();
            
        try {
            HttpResponse<String> response = this.injectionModel.getMediatorUtils().getConnectionUtil().getHttpClient().build().send(httpRequest, BodyHandlers.ofString());
            this.readGithubResponse(response, showOnConsole);
        } catch (InterruptedException | IOException e) {
            if (showOnConsole == ShowOnConsole.YES) {
                LOGGER.log(LogLevelUtil.CONSOLE_ERROR, String.format("Error during GitHub report connection: %s", e.getMessage()));
            }
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    private void readGithubResponse(HttpResponse<String> response, ShowOnConsole showOnConsole) throws IOException {
        try {
            if (showOnConsole == ShowOnConsole.YES) {
                var jsonObjectResponse = new JSONObject(response.body());
                var urlIssue = jsonObjectResponse.getString("html_url");
                LOGGER.log(LogLevelUtil.CONSOLE_SUCCESS, "Sent to GitHub: {}", urlIssue);
            }
        } catch (Exception e) {
            throw new IOException("Connection to the GitHub API failed, check your connection or update jSQL");
        }
    }
    
    /**
     * Displays news information on the console from GitHub web service.
     * Infos concern the general roadmap for the application, current development status
     * and other useful statements for the community.
     */
    public void showNews() {
        try {
            var news = this.callService().getJSONArray("news");
            for (var index = 0 ; index < news.length() ; index++) {
                LOGGER.log(LogLevelUtil.CONSOLE_INFORM, news.get(index));
            }
        } catch (JSONException e) {
            LOGGER.log(LogLevelUtil.CONSOLE_ERROR, "Connection to the GitHub API failed");
        }
    }
    
    /**
     * Instantiate the jsonObject from json data if not already set.
     * @return jsonObject describing json data
     */
    public JSONObject callService() {
        if (this.jsonObject == null) {
            String json = this.injectionModel.getMediatorUtils().getConnectionUtil().getSource(
                this.injectionModel.getMediatorUtils().getPropertiesUtil().getProperty("github.webservice.url")
            );
            // Fix #45349: JSONException on new JSONObject(json)
            try {
                this.jsonObject = new JSONObject(json);
            } catch (JSONException e) {
                try {
                    this.jsonObject = new JSONObject("{\"version\": \"0\", \"news\": []}");
                } catch (JSONException eInner) {
                    LOGGER.log(LogLevelUtil.CONSOLE_ERROR, "Fetching default JSON failed", eInner);
                }
                LOGGER.log(
                    LogLevelUtil.CONSOLE_ERROR,
                    "Fetching configuration from GitHub failed. Wait for service to be available, check your connection or update jSQL"
                );
            }
        }
        return this.jsonObject;
    }
}
