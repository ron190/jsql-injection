package com.jsql.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

public class I18n {
    static {
//        Locale.setDefault(Locale.FRENCH);
    }
    private static final ResourceBundle labels = ResourceBundle.getBundle("com.jsql.i18n.jsql", Locale.getDefault());
    
    public static final String selectAll = (String) labels.getObject("selectAll");
    public static final String copy = (String) labels.getObject("copy");
    public static final String copyPageURL = (String) labels.getObject("copyPageURL");
}
