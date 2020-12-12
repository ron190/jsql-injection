package com.jsql.util;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;
import java.util.Optional;
import java.util.AbstractMap.SimpleEntry;
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

    public Exception parseCsrf(Exception exception, StringBuilder pageSource, Map<String, String> mapResponse) {
        
        Optional<SimpleEntry<String, String>> optionalCookieCsrf = this.isCsrf(mapResponse);
        
        if (optionalCookieCsrf.isPresent()) {
            
            SimpleEntry<String, String> cookieCsrf = optionalCookieCsrf.get();
            LOGGER.warn(
                String
                .format(
                    "Found CSRF token in HTTP Cookie header: %s=%s",
                    cookieCsrf.getKey(),
                    cookieCsrf.getValue()
                )
            );
            
            // TODO Add each CSRF tokens to each header and request, like Spring param _csrf and header XSRF-TOKEN
            SimpleEntry<String, String> headerCsrf =
                new SimpleEntry<>(
                    "X-"+ cookieCsrf.getKey(),
//                    "_csrf",
                    cookieCsrf.getValue()
                );
            
            if (
                !this.injectionModel.getMediatorUtils().getPreferencesUtil().isNotProcessingCookies()
                && this.injectionModel.getMediatorUtils().getPreferencesUtil().isProcessingCsrf()
            ) {
                
                LOGGER.debug(
                    String.format(
                        "CSRF token added to querystring, request and header: X-%s=%s",
                        cookieCsrf.getKey(),
                        cookieCsrf.getValue()
                    )
                );
                this.tokenCsrf = headerCsrf;
                
            } else {
                
                LOGGER.info("Activate CSRF processing in Preferences if injection fails");
            }
        }
        
        Exception exceptionCsrf = exception;
        
        // TODO csrf in HTTP
        Optional<SimpleEntry<String, String>> optionalTokenCsrf =
            Jsoup
            .parse(pageSource.toString())
            .select("input")
            .select(
                String
                .join(
                    ",", 
                    "[name=csrf_token]",
                    "[name=csrfToken]",
                    "[name=user_token]",
                    "[name=csrfmiddlewaretoken]",
                    "[name=form_build_id]"
                )
            )
            .stream()
            .findFirst()
            .map(input -> new SimpleEntry<>(input.attr("name"), input.attr(INPUT_ATTR_VALUE)));
        
        if (optionalTokenCsrf.isPresent()) {
            
            SimpleEntry<String, String> tokenCsrfFound = optionalTokenCsrf.get();
            
            if (
                !this.injectionModel.getMediatorUtils().getPreferencesUtil().isNotProcessingCookies()
                && this.injectionModel.getMediatorUtils().getPreferencesUtil().isProcessingCsrf()
            ) {
                
                LOGGER.debug(
                    String.format(
                        "Found Csrf token %s=%s in HTML body, adding token to querystring, request and header",
                        tokenCsrfFound.getKey(),
                        tokenCsrfFound.getValue()
                    )
                );
                this.tokenCsrf = tokenCsrfFound;
                
            } else {
                
                LOGGER.warn(
                    String.format(
                        "Found Csrf token in HTML body: %s=%s",
                        tokenCsrfFound.getKey(),
                        tokenCsrfFound.getValue()
                    )
                );
                exceptionCsrf = new IOException("please activate Csrf processing in Preferences");
            }
        }
        
        return exceptionCsrf;
    }

    private Optional<SimpleEntry<String, String>> isCsrf(Map<String, String> mapResponse) {
        
        Optional<SimpleEntry<String, String>> countCsrfToken = Optional.empty();
        
        if (mapResponse.containsKey(SET_COOKIE)) {
            
            // Spring: Cookie XSRF-TOKEN => Header X-XSRF-TOKEN, GET/POST parameter _csrf
            // Laravel, Zend, Symfony, React, Vue, Angular
            
            String[] cookieValues = StringUtils.split(mapResponse.get(SET_COOKIE), ";");
            countCsrfToken =
                Stream
                .of(cookieValues)
                .filter(cookie -> cookie.trim().startsWith("XSRF-TOKEN"))
                .map(cookie -> {
                    
                    String[] cookieEntry = StringUtils.split(cookie, "=");

                    return new SimpleEntry<>(cookieEntry[0].trim(), cookieEntry[1].trim());
                })
                .findFirst();
        }
        
        return countCsrfToken;
    }
    
    public void addCsrfToken(HttpURLConnection connection) {
        
        if (this.tokenCsrf != null) {
            
            connection.setRequestProperty(
                this.tokenCsrf.getKey(),
                this.tokenCsrf.getValue()
            );
        }
        
        if (this.injectionModel.getMediatorUtils().getPreferencesUtil().isCsrfUserTag()) {

            connection.setRequestProperty(
                this.injectionModel.getMediatorUtils().getPreferencesUtil().csrfUserTag(),
                this.injectionModel.getMediatorUtils().getPreferencesUtil().csrfUserTagOutput()
            );
        }
    }
    
    public void addCsrfToken(DataOutputStream dataOut) throws IOException {

        if (this.tokenCsrf != null) {
            
            dataOut.writeBytes(
                String.format(
                    "X-XSRF-TOKEN=%s&",
                    this.tokenCsrf.getValue()
                )
            );
            dataOut.writeBytes(
                String.format(
                    "X-CSRF-TOKEN=%s&",
                    this.tokenCsrf.getValue()
                )
            );
        }
        
        if (this.injectionModel.getMediatorUtils().getPreferencesUtil().isCsrfUserTag()) {
            
            dataOut.writeBytes(
                String.format(
                    "%s=%s&",
                    this.injectionModel.getMediatorUtils().getPreferencesUtil().csrfUserTag(),
                    this.injectionModel.getMediatorUtils().getPreferencesUtil().csrfUserTagOutput()
                )
            );
        }
    }
    
    public String getCsrfTokenQueryString(String urlInjection) {
        
        String urlInjectionFixed = urlInjection;

        if (this.tokenCsrf != null) {

            urlInjectionFixed +=
                String.format(
                    "&_token=%s",
                    this.tokenCsrf.getValue()
                );
            
            urlInjectionFixed +=
                String.format(
                    "&_csrf=%s",
                    this.tokenCsrf.getValue()
                );
        }
        
        if (this.injectionModel.getMediatorUtils().getPreferencesUtil().isCsrfUserTag()) {

            urlInjectionFixed +=
                String.format(
                    "&%s=%s",
                    this.injectionModel.getMediatorUtils().getPreferencesUtil().csrfUserTag(),
                    this.injectionModel.getMediatorUtils().getPreferencesUtil().csrfUserTagOutput()
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
