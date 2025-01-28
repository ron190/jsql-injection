package com.jsql.view.swing.util;

import com.jsql.util.I18nUtil;
import com.jsql.util.StringUtil;
import com.jsql.view.swing.dialog.DialogAbout;
import com.jsql.view.swing.text.*;
import com.jsql.view.swing.tree.model.NodeModelEmpty;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.util.*;

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
        for (String key: I18nViewUtil.keys()) {
            String textI18n = I18nViewUtil.valueByKey(key);
            for (Object componentSwing: I18nViewUtil.componentsByKey(key)) {  // instanceof because no common parent with setText()
                if (componentSwing instanceof JTextFieldPlaceholder) {  // Textfield does not need <html> tags for asian fonts
                    ((JTextFieldPlaceholder) componentSwing).setPlaceholderText(I18nUtil.valueByKey(key));
                } else if (componentSwing instanceof JTextAreaPlaceholder) {
                    ((JTextAreaPlaceholder) componentSwing).setPlaceholderText(I18nUtil.valueByKey(key));
                } else if (componentSwing instanceof JTextPanePlaceholder) {
                    ((JTextPanePlaceholder) componentSwing).setPlaceholderText(I18nUtil.valueByKey(key));
                } else if (componentSwing instanceof JPasswordFieldPlaceholder) {
                    ((JPasswordFieldPlaceholder) componentSwing).setPlaceholderText(I18nUtil.valueByKey(key));
                } else if (componentSwing instanceof SyntaxTextArea) {
                    ((SyntaxTextArea) componentSwing).setPlaceholderText(I18nUtil.valueByKey(key));
                } else if (componentSwing instanceof JToolTipI18n) {
                    ((JToolTipI18n) componentSwing).setText(textI18n);
                } else if (componentSwing instanceof JLabel) {
                    ((JLabel) componentSwing).setText(textI18n);
                } else if (componentSwing instanceof JMenuItem) {
                    ((JMenuItem) componentSwing).setText(textI18n);
                } else if (componentSwing instanceof JButton) {
                    ((JButton) componentSwing).setText(textI18n);
                } else if (componentSwing instanceof NodeModelEmpty) {
                    ((NodeModelEmpty) componentSwing).setText(textI18n);
                } else if (componentSwing instanceof DialogAbout) {  // not I18nViewUtil.valueByKey() to avoid html in diag title
                    ((DialogAbout) componentSwing).setTitle(I18nUtil.valueByKey(key) +" "+ StringUtil.APP_NAME);
                } else if (componentSwing instanceof JComboBox) {
                    ((JComboBox<?>) componentSwing).setToolTipText(textI18n);
                } else {
                    ((JTextComponent) componentSwing).setText(textI18n);
                }
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
        I18nViewUtil.componentsLocalized.get(key).add(component);
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
