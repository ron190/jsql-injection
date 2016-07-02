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
    public static ResourceBundle CURRENT_LOCALE = ResourceBundle.getBundle("com.jsql.i18n.jsql", Locale.getDefault());
    
    public static Map<String, List<Object>> componentsView = new HashMap<>();
    
    static {
        for (String keyI18n: LOCALE_ROOT.keySet()) {
            componentsView.put(keyI18n, new ArrayList<>());
        }
    }
    
    private I18n() {
        // Disable constructor
    }
    
    public static Set<String> getKeys() {
        return componentsView.keySet();
    }
    
    public static List<Object> getComponentsSwing(String key) {
        return componentsView.get(key);
    }
    
    public static String get(String key) {
        return (String) CURRENT_LOCALE.getObject(key);
    }
    
    public static void add(String key, Object component) {
        componentsView.get(key).add(component);
    }
}
