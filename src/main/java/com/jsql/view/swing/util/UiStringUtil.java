package com.jsql.view.swing.util;

import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.StringUtils;
import org.mozilla.universalchardet.UniversalDetector;

public class UiStringUtil {

    private UiStringUtil() {
        // Util
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
        
        UniversalDetector detector = new UniversalDetector(null);
        detector.handleData(text.getBytes(), 0, text.length() - 1);
        detector.dataEnd();
        String encoding = detector.getDetectedCharset();
        
        String result = text;
        if (encoding != null) {
            
            // TODO move to View, remove from model
            result =
                "<html><span style=\"font-family:'"
                + UiUtil.FONT_NAME_UBUNTU_REGULAR
                + "';"
                + ( nowrap ? "white-space:nowrap;" : StringUtils.EMPTY )
                + "\">"
                + new String(text.getBytes(), StandardCharsets.UTF_8)
                + "</span></html>";
        }
        
        return result;
    }
}
