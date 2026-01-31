package com.jsql.view.swing.util;

import com.jsql.util.StringUtil;
import org.apache.commons.lang3.StringUtils;

public class UiStringUtil {

    private UiStringUtil() {
        // Utility class
    }
    
    public static String detectUtf8Html(String text) {
        return UiStringUtil.detectUtf8Html(text, false);
    }
    
    public static String detectUtf8HtmlNoWrap(String text) {
        return UiStringUtil.detectUtf8Html(text, true);
    }
    
    public static String detectUtf8Html(String text, boolean nowrap) {
        // Fix #35217: NullPointerException on getBytes()
        if (text == null) {
            return StringUtils.EMPTY;
        }
        
        // Decode bytes for potentially UTF8 chars
        // Required by asian and hindi chars, otherwise wrong display in database tree
        String result = text;
        if (StringUtil.containsNonStandardScripts(text)) {
            result = I18nViewUtil.formatNonLatin(text, nowrap ? "white-space:nowrap;" : StringUtils.EMPTY);
        }
        return result;
    }
}
