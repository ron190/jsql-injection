package com.jsql.view.swing.util;

import com.jsql.util.I18nUtil;
import com.jsql.util.StringUtil;
import com.jsql.view.swing.dialog.DialogAbout;
import com.jsql.view.swing.text.JPlaceholder;
import com.jsql.view.swing.text.JToolTipI18n;
import com.jsql.view.swing.tree.model.NodeModelEmpty;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.util.*;
import java.util.function.BiConsumer;

public class I18nViewUtil {

    /**
     * A list of graphical components for each i18n keys in the main properties
     */
    private static final Map<String, Set<Object>> componentsLocalized = new HashMap<>();
    
    // Initialize the list of graphical components
    static {
        for (String keyI18n: I18nUtil.BUNDLE_ROOT.keySet()) {
            I18nViewUtil.componentsLocalized.put(keyI18n, new HashSet<>());
        }
    }

    private I18nViewUtil() {
        // Utility class
    }
    
    /**
     * Return the i18n keys of components whose text is replaced
     * when the translation changes.
     * @return a set of key names of an i18n key in the properties
     */
    public static Set<String> keys() {
        return I18nViewUtil.componentsLocalized.keySet();
    }
    
    /**
     * Get a list of graphical components whose text corresponds
     * to the i18n key in the properties.
     * @param key name of an i18n key in the properties
     * @return set of graphical components
     */
    public static Set<Object> componentsByKey(String key) {
        return I18nViewUtil.componentsLocalized.get(key);
    }

    public static void switchI18nComponents() {
        Map<Class<?>, BiConsumer<Object, String>> classHandlers = new LinkedHashMap<>();  // key order required
        classHandlers.put(JPlaceholder.class, (c, s) -> ((JPlaceholder) c).setPlaceholderText(I18nUtil.valueByKey(s)));
        classHandlers.put(DialogAbout.class, (c, s) -> ((DialogAbout) c).setTitle(I18nUtil.valueByKey(s) + " " + StringUtil.APP_NAME));
        classHandlers.put(JToolTipI18n.class, (c, s) -> ((JToolTipI18n) c).setText(I18nViewUtil.valueByKey(s)));
        classHandlers.put(NodeModelEmpty.class, (c, s) -> ((NodeModelEmpty) c).setText(I18nViewUtil.valueByKey(s)));
        classHandlers.put(JLabel.class, (c, s) -> ((JLabel) c).setText(I18nViewUtil.valueByKey(s)));
        classHandlers.put(JMenuItem.class, (c, s) -> ((JMenuItem) c).setText(I18nViewUtil.valueByKey(s)));
        classHandlers.put(JButton.class, (c, s) -> ((JButton) c).setText(I18nViewUtil.valueByKey(s)));
        classHandlers.put(JComboBox.class, (c, s) -> ((JComboBox<?>) c).setToolTipText(I18nViewUtil.valueByKey(s)));
        classHandlers.put(JTextComponent.class, (c, s) -> ((JTextComponent) c).setText(I18nViewUtil.valueByKey(s))); // fallback
        for (String key : I18nViewUtil.keys()) {
            for (Object component : I18nViewUtil.componentsByKey(key)) {
                classHandlers.entrySet().stream()
                .filter(entry -> entry.getKey().isInstance(component))
                .findFirst()
                .ifPresent(entry -> entry.getValue().accept(component, key));
            }
        }
    }
    
    /**
     * Add a graphical component to those whose text must be changed when
     * the language changes.
     * @param key name of an i18n key in the properties
     * @param component graphical component which will receive the translated text
     */
    public static void addComponentForKey(String key, Object component) {
        I18nViewUtil.componentsLocalized.get(key.replace(" ", "_")).add(component);  // e.g BIND BINARY
    }

    /**
     * Return the text corresponding to an i18n key in the properties.
     * @param key an i18n key in the properties
     * @return text corresponding to the key
     */
    public static String valueByKey(String key) {
        return I18nViewUtil.isNonUbuntu(I18nUtil.getCurrentLocale())
        ? I18nViewUtil.formatNonLatin(I18nUtil.valueByKey(key))
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
