package com.jsql.i18n;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

public class I18n {
    
    private static final ResourceBundle LOCALE_ROOT = ResourceBundle.getBundle("com.jsql.i18n.jsql", Locale.ROOT);
    
    private static ResourceBundle localeDefault = ResourceBundle.getBundle("com.jsql.i18n.jsql", Locale.getDefault());

    private static final Map<String, List<Object>> componentsLocalized = new HashMap<>();
    
    static {
        for (String keyI18n: LOCALE_ROOT.keySet()) {
            componentsLocalized.put(keyI18n, new ArrayList<>());
        }
    }
    
    private I18n() {
        // Disable constructor
    }
    
    public static Set<String> keys() {
        return componentsLocalized.keySet();
    }
    
    public static List<Object> componentsByKey(String key) {
        return componentsLocalized.get(key);
    }
    
    public static String valueByKey(String key) {
        return (String) localeDefault.getObject(key);
    }
    
    public static void addComponentForKey(String key, Object component) {
        componentsLocalized.get(key).add(component);
    }
    
    public static void setLocaleDefault(ResourceBundle localeDefault) {
        I18n.localeDefault = localeDefault;
    }
}
