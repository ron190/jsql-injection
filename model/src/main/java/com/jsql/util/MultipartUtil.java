package com.jsql.util;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.JSqlException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MultipartUtil {

    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();

    private final InjectionModel injectionModel;

    public MultipartUtil(InjectionModel injectionModel) {
        
        this.injectionModel = injectionModel;
    }

    public boolean testParameters() {
        
        var hasFoundInjection = false;

        String rawHeader = this.injectionModel.getMediatorUtils().getParameterUtil().getRawHeader();
        String rawRequest = this.injectionModel.getMediatorUtils().getParameterUtil().getRawRequest();

        Matcher matcherBoundary = Pattern.compile("boundary=([^;]*)").matcher(rawHeader);

        if (!matcherBoundary.find()) {
            return false;
        }
        
        String boundary = matcherBoundary.group(1);

        Matcher matcherFormDataParameters = Pattern
            .compile("Content-Disposition\\s*:\\s*form-data\\s*;\\s*name\\s*=\"(.*?)\"(.*?)--" + boundary, Pattern.DOTALL)
            .matcher(rawRequest);

        while (matcherFormDataParameters.find()) {
            if (isBoundaryInjectable(rawRequest, boundary, matcherFormDataParameters)) {
                return true;
            }
        }

        return hasFoundInjection;
    }

    private boolean isBoundaryInjectable(String rawRequest, String boundary, Matcher matcherFormDataParameters) {

        String nameParameter = matcherFormDataParameters.group(1);
        String valueParameter = matcherFormDataParameters.group(2);

        String rawRequestWithStar = rawRequest.replaceAll(
            "(?i)(Content-Disposition\\s*:\\s*form-data\\s*;\\s*name\\s*=\\s*\"" + nameParameter + "\".*?)([\\\\r\\\\n]*--" + boundary + ")",
            "$1" + InjectionModel.STAR + "$2"
        );

        this.injectionModel.getMediatorUtils().getParameterUtil().initializeRequest(rawRequestWithStar);

        try {
            LOGGER.log(
                LogLevelUtil.CONSOLE_INFORM,
                "Checking Multipart boundary injection for {}={}",
                () -> nameParameter,
                () -> valueParameter.replace(InjectionModel.STAR, StringUtils.EMPTY)
            );

            return this.injectionModel.getMediatorMethod().getRequest().testParameters();

        } catch (JSqlException e) {

            LOGGER.log(
                LogLevelUtil.CONSOLE_ERROR,
                String.format(
                    "No Multipart boundary injection for %s=%s",
                    nameParameter,
                    valueParameter.replace(InjectionModel.STAR, StringUtils.EMPTY)
                )
            );
        }

        return false;
    }
}
