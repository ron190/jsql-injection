package com.jsql.util;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.JSqlException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.AbstractMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CookiesUtil {

    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();

    private final InjectionModel injectionModel;

    public CookiesUtil(InjectionModel injectionModel) {
        this.injectionModel = injectionModel;
    }

    public boolean testParameters(boolean hasFoundInjection) {

        if (!hasFoundInjection) {
            if (!this.injectionModel.getMediatorUtils().getPreferencesUtil().isCheckingAllCookieParam()) {
                return false;
            }
            LOGGER.log(LogLevelUtil.CONSOLE_DEFAULT, "Checking cookies params...");
        } else {
            return true;
        }

        String rawHeader = this.injectionModel.getMediatorUtils().getParameterUtil().getRawHeader();

        List<AbstractMap.SimpleEntry<String, String>> cookies = this.injectionModel.getMediatorUtils().getParameterUtil().getListHeader()
            .stream()
            .filter(entry -> "cookie".equalsIgnoreCase(entry.getKey()))
            .findFirst()
            .map(cookieHeader -> cookieHeader.getValue().split(";"))
            .stream()
            .flatMap(Stream::of)
            .filter(cookie -> cookie != null && cookie.contains("="))
            .map(cookie -> cookie.split("=", 2))
            .map(arrayEntry -> new AbstractMap.SimpleEntry<>(
                arrayEntry[0].trim(),
                arrayEntry[1] == null ? "" : arrayEntry[1].trim()
            ))
            .collect(Collectors.toList());

        for (AbstractMap.SimpleEntry<String, String> cookie: cookies) {

            String keyValue = cookie.getKey() + "=" + cookie.getValue();
            String headerCookieWithStar = rawHeader.replace(keyValue, keyValue + InjectionModel.STAR);

            this.injectionModel.getMediatorUtils().getParameterUtil().initializeHeader(headerCookieWithStar);

            try {
                LOGGER.log(
                    LogLevelUtil.CONSOLE_DEFAULT,
                    "Checking cookie {}={}",
                    cookie::getKey,
                    () -> cookie.getValue().replace(InjectionModel.STAR, StringUtils.EMPTY)
                );

                if (this.injectionModel.getMediatorMethod().getHeader().testParameters()) {
                    return true;
                }
            } catch (JSqlException e) {
                LOGGER.log(
                    LogLevelUtil.CONSOLE_ERROR,
                    String.format(
                        "No Cookie injection for %s=%s",
                        cookie.getKey(),
                        cookie.getValue().replace(InjectionModel.STAR, StringUtils.EMPTY)
                    )
                );
            }
        }

        return false;
    }
}
