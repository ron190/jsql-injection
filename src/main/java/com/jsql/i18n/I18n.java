package com.jsql.i18n;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

public class I18n {
    
    public static ResourceBundle LOCALE_ROOT = ResourceBundle.getBundle("com.jsql.i18n.jsql", Locale.ROOT);
    
    public static ResourceBundle LOCALE_DEFAULT = ResourceBundle.getBundle("com.jsql.i18n.jsql", Locale.getDefault());
    
    public static Map<String, List<Object>> componentsLocalized = new HashMap<>();
    
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
        return (String) LOCALE_DEFAULT.getObject(key);
    }
    
    public static void addComponentForKey(String key, Object component) {
        componentsLocalized.get(key).add(component);
    }
}
