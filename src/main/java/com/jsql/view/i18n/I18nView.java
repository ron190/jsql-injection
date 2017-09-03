package com.jsql.view.i18n;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jsql.i18n.I18n;

public class I18nView {

    /**
     * A list of graphical components for each i18n keys in the main properties
     */
    private static final Map<String, List<Object>> componentsLocalized = new HashMap<>();
    
    /**
     * Initialize the list of graphical components
     */
    static {
        for (String keyI18n: I18n.getLocaleRoot().keySet()) {
            I18nView.componentsLocalized.put(keyI18n, new ArrayList<>());
        }
    }
    
    // Utility class
    private I18nView() {
        // Ignore
    }
    
    /**
     * Return the i18n keys of components whose text is replaced
     * when the translation changes.
     * @return a list of key names of a i18n key in the properties
     */
    public static Set<String> keys() {
        return I18nView.componentsLocalized.keySet();
    }
    
    /**
     * Get a list of graphical components whose text corresponds
     * to the i18n key in the properties.
     * @param key name of a i18n key in the properties
     * @return a list of graphical components
     */
    public static List<Object> componentsByKey(String key) {
        return I18nView.componentsLocalized.get(key);
    }
    
    /**
     * Add a graphical component to those whose text must be changed when
     * the language changes.
     * @param key name of a i18n key in the properties
     * @param component graphical component which will receive the translated text
     */
    public static void addComponentForKey(String key, Object component) {
        I18nView.componentsLocalized.get(key).add(component);
    }

}
