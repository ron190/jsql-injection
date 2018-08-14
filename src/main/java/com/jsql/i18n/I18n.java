package com.jsql.i18n;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

/**
 * Utility class managing different text translations like english, chinese and arabic.
 * It retreives text in the current language of the system and also the one choosed
 * manually by user.
 * If the current system language is not supported then the user is proposed to use
 * the community translation protocol.
 */
public class I18n {
	
    /**
     * Using default log4j.properties from root /
     */
    private static final Logger LOGGER = Logger.getRootLogger();
    
//    static {
//        Locale.setDefault(new Locale("ja"));
//    }
    
    /**
     * Bundle of standard i18n keys and translated text for root language english
     */
    private static final ResourceBundle LOCALE_ROOT = ResourceBundle.getBundle("com.jsql.i18n.jsql", Locale.ROOT);
    
    /**
     * Bundle of i18n keys and translated text for the current system language
     */
    private static ResourceBundle localeDefault = ResourceBundle.getBundle("com.jsql.i18n.jsql", Locale.getDefault());
    
    // Utility class
    private I18n() {
        // not used
    }
    
    /**
     * Return the text corresponding to a i18n key in the properties.
     * @param key a i18n key in the properties
     * @return text corresponding to the key
     */
    public static String valueByKey(String key) {
    	return (String) I18n.localeDefault.getObject(key);
    }
    
    /**
     * Verify if there is a language properties file corresponding to the current system language.
     * If not then it invites the user to use the translation process.
     * @throws URISyntaxException
     */
    public static void checkCurrentLanguage() throws URISyntaxException {
        URL path = I18n.class.getResource("/com/jsql/i18n/jsql_"+ Locale.getDefault().getLanguage() +".properties");
        if (!"en".equals(Locale.getDefault().getLanguage()) && path == null) {
            String languageHost = Locale.getDefault().getDisplayLanguage(Locale.ENGLISH);
            LOGGER.debug(
                "Language "+ languageHost +" is not supported, "
                + "please contribute and translate pieces of jSQL into "+ languageHost +": "
                + "click on the top right button and open menu [Community], choose [I help translate jSQL into][another language...] and "
                + "translate some text into "+ languageHost +" then click on [Send]. Your translation will be integrated to the next release by the developer."
            );
        }
    }
    
    // Getters and setters
    
    public static void setLocaleDefault(ResourceBundle localeDefault) {
    	I18n.localeDefault = localeDefault;
    }
    
    public static Locale getLocaleDefault() {
    	return I18n.localeDefault.getLocale();
    }

    public static ResourceBundle getLocaleRoot() {
        return LOCALE_ROOT;
    }
    
}
