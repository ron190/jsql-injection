package com.jsql.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Utility class managing different text translations like English, Chinese and Arabic.
 * It retrieves text in the current language of the system and also the one choice
 * manually by user.
 * If the current system language is not supported then the user is proposed to use
 * the community translation protocol.
 */
public class I18nUtil {
    
    private static final Logger LOGGER = LogManager.getRootLogger();
    public static final String BASE_NAME = "i18n.jsql";

    /**
     * Bundle of standard i18n keys and translated text for root language English
     */
    public static final ResourceBundle BUNDLE_ROOT = ResourceBundle.getBundle(BASE_NAME, Locale.ROOT);
    
    /**
     * Bundle of i18n keys and translated text for the current system language
     */
    private static ResourceBundle currentBundle = ResourceBundle.getBundle(BASE_NAME, Locale.getDefault());

    private I18nUtil() {
        // Utility class
    }
    
    /**
     * Return the text corresponding to a i18n key in the properties.
     * @param key a i18n key in the properties
     * @return text corresponding to the key
     */
    public static String valueByKey(String key) {
        return I18nUtil.currentBundle.getString(key);
    }
    
    /**
     * Verify if there is a language properties file corresponding to the current system language.
     * If not then it invites the user to use the translation process.
     */
    public static void checkCurrentLanguage() {
        URL path = I18nUtil.class.getClassLoader().getResource("i18n/jsql_"+ Locale.getDefault().getLanguage() +".properties");
        if (!"en".equals(Locale.getDefault().getLanguage()) && path == null) {
            String languageHost = Locale.getDefault().getDisplayLanguage(Locale.ENGLISH);
            LOGGER.log(
                LogLevelUtil.CONSOLE_SUCCESS,
                () -> String.join(
                    StringUtils.EMPTY,
                    "Contribute and translate parts of jSQL Injection into ",
                    languageHost,
                    ": ",
                    "click on the top right button and open menu [Community], choose [I help translate jSQL into > another language...] and ",
                    "translate some text into ",
                    languageHost,
                    " then click on [Send]. Your translation will be integrated to the next release by the developer."
                )
            );
        }
    }
    
    
    // Getters and setters
    
    public static void setCurrentBundle(Locale newLocale) {
        I18nUtil.currentBundle = ResourceBundle.getBundle(BASE_NAME, newLocale);
    }
    
    public static Locale getCurrentLocale() {
        return I18nUtil.currentBundle.getLocale();
    }
}
