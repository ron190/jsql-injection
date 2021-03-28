package com.jsql.util;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Utility class managing different text translations like English, Chinese and Arabic.
 * It retrieves text in the current language of the system and also the one choice
 * manually by user.
 * If the current system language is not supported then the user is proposed to use
 * the community translation protocol.
 */
public class I18nUtil {
    
    private static final Logger LOGGER = LogManager.getRootLogger();
    
    /**
     * Bundle of standard i18n keys and translated text for root language English
     */
    private static final ResourceBundle LOCALE_ROOT = ResourceBundle.getBundle("i18n.jsql", Locale.ROOT);
    
    /**
     * Bundle of i18n keys and translated text for the current system language
     */
    private static ResourceBundle localeDefault = ResourceBundle.getBundle("i18n.jsql", Locale.getDefault());
    
    // Utility class
    private I18nUtil() {
        // not used
    }
    
    /**
     * Return the text corresponding to a i18n key in the properties.
     * @param key a i18n key in the properties
     * @return text corresponding to the key
     */
    public static String valueByKey(String key) {
        
        return (String) I18nUtil.localeDefault.getObject(key);
    }
    
    /**
     * Verify if there is a language properties file corresponding to the current system language.
     * If not then it invites the user to use the translation process.
     * @throws URISyntaxException
     */
    public static void checkCurrentLanguage() {
        
        URL path = I18nUtil.class.getClassLoader().getResource("i18n/jsql_"+ Locale.getDefault().getLanguage() +".properties");
        
        if (!"en".equals(Locale.getDefault().getLanguage()) && path == null) {
            
            String languageHost = Locale.getDefault().getDisplayLanguage(Locale.ENGLISH);
            
            LOGGER.log(
                LogLevel.CONSOLE_SUCCESS,
                () -> String.join(
                    "",
                    "Please contribute and translate parts of jSQL Injection into ",
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
    
    public static boolean isAsian(Locale newLocale) {
        return
            new Locale("zh").getLanguage().equals(newLocale.getLanguage())
            || new Locale("ko").getLanguage().equals(newLocale.getLanguage())
            || new Locale("ja").getLanguage().equals(newLocale.getLanguage());
    }
    
    
    // Getters and setters
    
    public static void setLocaleDefault(ResourceBundle localeDefault) {
        I18nUtil.localeDefault = localeDefault;
    }
    
    public static Locale getLocaleDefault() {
        return I18nUtil.localeDefault.getLocale();
    }

    public static ResourceBundle getLocaleRoot() {
        return LOCALE_ROOT;
    }
}
