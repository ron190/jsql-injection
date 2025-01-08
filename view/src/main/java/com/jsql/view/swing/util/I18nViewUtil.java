package com.jsql.view.swing.util;

import com.jsql.util.I18nUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class I18nViewUtil {

    /**
     * A list of graphical components for each i18n keys in the main properties
     */
    private static final Map<String, List<Object>> componentsLocalized = new HashMap<>();
    
    // Initialize the list of graphical components
    static {
        for (String keyI18n: I18nUtil.BUNDLE_ROOT.keySet()) {
            I18nViewUtil.componentsLocalized.put(keyI18n, new ArrayList<>());
        }
    }

    private I18nViewUtil() {
        // Utility class
    }
    
    /**
     * Return the i18n keys of components whose text is replaced
     * when the translation changes.
     * @return a set of key names of a i18n key in the properties
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
        return I18nViewUtil.isNonUbuntu(I18nUtil.getCurrentLocale())
            ? I18nViewUtil.formatNonLatin(I18nUtil.valueByKey(key))
            : I18nUtil.valueByKey(key);
    }

    public static String valueByKey(String key, Locale newLocale) {
        return I18nViewUtil.isNonUbuntu(newLocale)
            ? I18nViewUtil.valueByKey(key)
            : I18nUtil.valueByKey(key);
    }

    public static boolean isNonUbuntu(Locale locale) {
        return Locale.forLanguageTag("zh").getLanguage().equals(locale.getLanguage())
            || Locale.forLanguageTag("ko").getLanguage().equals(locale.getLanguage())
            || Locale.forLanguageTag("ja").getLanguage().equals(locale.getLanguage());
    }

    public static String formatNonLatin(String label) {
        return I18nViewUtil.formatNonLatin(label, StringUtils.EMPTY);
    }

    public static String formatNonLatin(String label, String custom) {
        return String.format(
            "<html><span style=\"font-family:'%s';%s\">%s</span></html>",
            UiUtil.FONT_NAME_MONO_ASIAN,
            custom,
            label
        );
    }
}
