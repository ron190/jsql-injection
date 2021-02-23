package com.jsql.view.swing.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.jsql.util.I18nUtil;

public class I18nViewUtil {

    /**
     * A list of graphical components for each i18n keys in the main properties
     */
    private static final Map<String, List<Object>> componentsLocalized = new HashMap<>();
    
    /**
     * Initialize the list of graphical components
     */
    static {
        
        for (String keyI18n: I18nUtil.getLocaleRoot().keySet()) {
            
            I18nViewUtil.componentsLocalized.put(keyI18n, new ArrayList<>());
        }
    }
    
    // Utility class
    private I18nViewUtil() {
        // Ignore
    }
    
    /**
     * Return the i18n keys of components whose text is replaced
     * when the translation changes.
     * @return a list of key names of a i18n key in the properties
     */
    public static Set<String> keys() {
        
        return I18nViewUtil.componentsLocalized.keySet();
    }
    
    /**
     * Get a list of graphical components whose text corresponds
     * to the i18n key in the properties.
     * @param key name of a i18n key in the properties
     * @return a list of graphical components
     */
    public static List<Object> componentsByKey(String key) {
        
        return I18nViewUtil.componentsLocalized.get(key);
    }
    
    /**
     * Add a graphical component to those whose text must be changed when
     * the language changes.
     * @param key name of a i18n key in the properties
     * @param component graphical component which will receive the translated text
     */
    public static void addComponentForKey(String key, Object component) {
        
        I18nViewUtil.componentsLocalized.get(key).add(component);
    }
    
    /**
     * Return the text corresponding to a i18n key in the properties.
     * @param key a i18n key in the properties
     * @return text corresponding to the key
     */
    public static String valueByKey(String key) {
        
        String result;
        
        if (I18nUtil.isAsian(I18nUtil.getLocaleDefault())) {
            
            result =
                String
                .format(
                    "<html><span style=\"font-family:'%s'\">%s</span></html>",
                    UiUtil.FONT_NAME_MONO_ASIAN,
                    I18nUtil.valueByKey(key)
                );
        } else {
            
            result = I18nUtil.valueByKey(key);
        }
        
        return result;
    }
    
    public static String valueByKey(String key, Locale newLocale) {
        
        if (I18nUtil.isAsian(newLocale)) {
            
            return I18nViewUtil.valueByKey(key);
            
        } else {
            
            return I18nUtil.valueByKey(key);
        }
    }
}
