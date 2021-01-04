package com.jsql.util;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;

import com.jsql.model.InjectionModel;

public class CsrfUtil {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();

    private SimpleEntry<String, String> tokenCsrf = null;
    
    private static final String SET_COOKIE = "Set-Cookie";
    private static final String INPUT_ATTR_VALUE = "value";
    
    private InjectionModel injectionModel;
    
    public CsrfUtil(InjectionModel injectionModel) {
        
        this.injectionModel = injectionModel;
    }

    public void parseForCsrfToken(StringBuilder pageSource, Map<String, String> headers) {
        
        this.parseCsrfFromCookie(headers);
        
        this.parseCsrfFromHtml(pageSource);
    }

    private void parseCsrfFromHtml(StringBuilder pageSource) {
        
        List<String> tags =
            Arrays
            .asList(
                "[name=_csrf]",
                "[name=_token]",
                "[name=csrf-token]",
                "[name=_csrf_header]",
                "[name=csrf_token]",
                "[name=csrfToken]",
                "[name=user_token]",
                "[name=csrfmiddlewaretoken]",
                "[name=form_build_id]"
            );
        
        if (this.injectionModel.getMediatorUtils().getPreferencesUtil().isCsrfUserTag()) {
            
            tags.add(
                String
                .format(
                    "[name=%s]",
                    this.injectionModel.getMediatorUtils().getPreferencesUtil().csrfUserTag()
                )
            );
        }
        
        Optional<SimpleEntry<String, String>> optionalTokenCsrf =
            Jsoup
            .parse(pageSource.toString())
            .select("input")
            .select(
                String.join(",", tags)
            )
            .stream()
            .findFirst()
            .map(input ->
                new SimpleEntry<>(
                    input.attr("name"),
                    input.attr(INPUT_ATTR_VALUE)
                )
            );
        
        if (optionalTokenCsrf.isPresent()) {
            
            SimpleEntry<String, String> tokenCsrfFound = optionalTokenCsrf.get();
            
            LOGGER.info(
                String.format(
                    "Found Csrf token from HTML body: %s=%s",
                    tokenCsrfFound.getKey(),
                    tokenCsrfFound.getValue()
                )
            );
            
            if (
                !this.injectionModel.getMediatorUtils().getPreferencesUtil().isNotProcessingCookies()
                && this.injectionModel.getMediatorUtils().getPreferencesUtil().isProcessingCsrf()
            ) {
                
                this.tokenCsrf = tokenCsrfFound;
                LOGGER.debug(
                    String.format(
                        "Csrf token added to query and header: %s",
                        tokenCsrfFound.getValue()
                    )
                );
                
            } else {
                
                LOGGER.info("Activate CSRF processing in Preferences if required");
            }
        }
    }

    private void parseCsrfFromCookie(Map<String, String> mapResponse) {
        
        Optional<SimpleEntry<String, String>> optionalCookieCsrf = Optional.empty();
        
        if (mapResponse.containsKey(SET_COOKIE)) {
            
            // Spring: Cookie XSRF-TOKEN => Header X-XSRF-TOKEN, GET/POST parameter _csrf
            // Laravel, Zend, Symfony
            
            String[] cookieValues = StringUtils.split(mapResponse.get(SET_COOKIE), ";");
            
            optionalCookieCsrf =
                Stream
                .of(cookieValues)
                .filter(cookie -> cookie.trim().startsWith("XSRF-TOKEN"))
                .map(cookie -> {
                    
                    String[] cookieEntry = StringUtils.split(cookie, "=");

                    return
                        new SimpleEntry<>(
                            cookieEntry[0].trim(),
                            cookieEntry[1].trim()
                        );
                })
                .findFirst();
        }
        
        if (optionalCookieCsrf.isPresent()) {
            
            SimpleEntry<String, String> cookieCsrf = optionalCookieCsrf.get();
            
            LOGGER.warn(
                String
                .format(
                    "Found CSRF token from Cookie: %s=%s",
                    cookieCsrf.getKey(),
                    cookieCsrf.getValue()
                )
            );
            
            SimpleEntry<String, String> headerCsrf =
                new SimpleEntry<>(
                    cookieCsrf.getKey(),
                    cookieCsrf.getValue()
                );
            
            if (
                !this.injectionModel.getMediatorUtils().getPreferencesUtil().isNotProcessingCookies()
                && this.injectionModel.getMediatorUtils().getPreferencesUtil().isProcessingCsrf()
            ) {
                
                this.tokenCsrf = headerCsrf;
                
            } else {
                
                LOGGER.info("Activate CSRF processing in Preferences if required");
            }
        }
    }
    
    public void addHeaderToken(HttpURLConnection connection) {
        
        if (this.tokenCsrf == null) {
            
             return;
        }

        connection.setRequestProperty(
            "X-XSRF-TOKEN",
            this.tokenCsrf.getValue()
        );
        
        connection.setRequestProperty(
            "X-CSRF-TOKEN",
            this.tokenCsrf.getValue()
        );
        
        if (this.injectionModel.getMediatorUtils().getPreferencesUtil().isCsrfUserTag()) {

            connection.setRequestProperty(
                this.injectionModel.getMediatorUtils().getPreferencesUtil().csrfUserTagOutput(),
                this.tokenCsrf.getValue()
            );
        }
    }
    
    public void addRequestToken(DataOutputStream dataOut) throws IOException {

        if (this.tokenCsrf == null) {
            
            return;
        }

        dataOut.writeBytes(
            String.format(
                "%s=%s&",
                this.tokenCsrf.getKey(),
                this.tokenCsrf.getValue()
            )
        );
        
        dataOut.writeBytes(
            String.format(
                "_csrf=%s&",
                this.tokenCsrf.getValue()
            )
        );
        
        if (this.injectionModel.getMediatorUtils().getPreferencesUtil().isCsrfUserTag()) {
            
            dataOut.writeBytes(
                String.format(
                    "%s=%s&",
                    this.injectionModel.getMediatorUtils().getPreferencesUtil().csrfUserTagOutput(),
                    this.tokenCsrf.getValue()
                )
            );
        }
    }
    
    public String addQueryStringToken(String urlInjection) {
        
        String urlInjectionFixed = urlInjection;

        if (this.tokenCsrf == null) {
            
            return urlInjectionFixed;
        }

        urlInjectionFixed +=
            String.format(
                "&%s=%s",
                this.tokenCsrf.getKey(),
                this.tokenCsrf.getValue()
            );
        
        urlInjectionFixed +=
            String.format(
                "&_csrf=%s",
                this.tokenCsrf.getValue()
            );
        
        if (this.injectionModel.getMediatorUtils().getPreferencesUtil().isCsrfUserTag()) {

            urlInjectionFixed +=
                String.format(
                    "&%s=%s",
                    this.injectionModel.getMediatorUtils().getPreferencesUtil().csrfUserTagOutput(),
                    this.tokenCsrf.getValue()
                );
        }
        
        return urlInjectionFixed;
    }
    
    
    // Getter / Setter

    public SimpleEntry<String, String> getTokenCsrf() {
        return this.tokenCsrf;
    }

    public void setTokenCsrf(SimpleEntry<String, String> tokenCsrf) {
        this.tokenCsrf = tokenCsrf;
    }
}
