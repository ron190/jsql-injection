package com.jsql.i18n;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * Utility class managing different text translations like english, chinese and arabic.
 * It retreives text in the current language of the system and also the one choosed
 * manually by user. 
 * If the current system language is not supported then the user is proposed to use
 * the community translation protocol.  
 * TODO: add graphical component methods to the view
 */
public class I18n {
	
    /**
     * Using default log4j.properties from root /
     */
    private static final Logger LOGGER = Logger.getRootLogger();
    
//    static {
//        Locale.setDefault(new Locale("ar"));
//    }
    
    /**
     * Bundle of standard i18n keys and translated text for root language english
     */
    private static final ResourceBundle LOCALE_ROOT = ResourceBundle.getBundle("com.jsql.i18n.jsql", Locale.ROOT);
    
    /**
     * Bundle of i18n keys and translated text for the current system language
     */
    private static ResourceBundle localeDefault = ResourceBundle.getBundle("com.jsql.i18n.jsql", Locale.getDefault());

    /**
     * A list of graphical components for each i18n keys in the main properties
     * TODO: add to the view
     */
    private static final Map<String, List<Object>> componentsLocalized = new HashMap<>();
    
    /**
     * Initialize the list of graphical components
     * TODO: add to the view
     */
    static {
        for (String keyI18n: I18n.LOCALE_ROOT.keySet()) {
        	I18n.componentsLocalized.put(keyI18n, new ArrayList<>());
        }
    }
    
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
     * Return the i18n keys of components whose text is replaced
     * when the translation changes.
     * @return a list of key names of a i18n key in the properties
     * TODO: add to the view
     */
    public static Set<String> keys() {
        return I18n.componentsLocalized.keySet();
    }
    
    /**
     * Get a list of graphical components whose text corresponds
     * to the i18n key in the properties.
     * @param key name of a i18n key in the properties
     * @return a list of graphical components
     * TODO: add to the view
     */
    public static List<Object> componentsByKey(String key) {
        return I18n.componentsLocalized.get(key);
    }
    
    /**
     * Add a graphical component to those whose text must be changed when
     * the language changes.
     * @param key name of a i18n key in the properties
     * @param component graphical component which will receive the translated text
     * TODO: add to the view
     */
    public static void addComponentForKey(String key, Object component) {
    	I18n.componentsLocalized.get(key).add(component);
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
                + "click on the top right button and open menu [Community], choose a language using [I help translate jSQL] and "
                + "translate some text into "+ languageHost +" then click on [Send]. The developer will add your translation "
                + "to the next release."
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
    
}
