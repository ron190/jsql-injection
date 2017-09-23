package com.jsql.view.swing.dialog;

import javax.swing.Icon;

import com.jsql.view.swing.HelperUi;

public enum Language {
    
    AR("Arabic", "ar", HelperUi.ICON_FLAG_AR),
    RU("Russian", "ru", HelperUi.ICON_FLAG_RU),
    ZH("Chinese", "zh", HelperUi.ICON_FLAG_ZH),
    TR("Turkish", "tr", HelperUi.ICON_FLAG_TR),
    EN("English", "en", HelperUi.ICON_FLAG_EN),
    FR("French", "fr", HelperUi.ICON_FLAG_FR),
    HI("Hindi", "hi", HelperUi.ICON_FLAG_HI),
    CS("Czech", "cs", HelperUi.ICON_FLAG_CS),
    DE("German", "de", HelperUi.ICON_FLAG_DE),
    NL("Dutch", "nl", HelperUi.ICON_FLAG_NL),
    IN_ID("Bahasa Indonesia", "in_ID", HelperUi.ICON_FLAG_IN_ID),
    IT("Italian", "it", HelperUi.ICON_FLAG_IT),
    ES("Spanish", "es", HelperUi.ICON_FLAG_ES),
    PT("Portuguese", "pt", HelperUi.ICON_FLAG_PT),
    PL("Polish", "pl", HelperUi.ICON_FLAG_PL),
    KO("Korean", "ko", HelperUi.ICON_FLAG_KO),
    OT("another language", null, null);

    private String stringLang;
    private Icon flag;
    private String nameLocale;
    
    private Language(String stringLang, String nameLocale, Icon flag) {
        this.stringLang = stringLang;
        this.flag = flag;
        this.nameLocale = nameLocale;
    }
    
    public Icon getFlag() {
        return this.flag;
    }
    
    @Override
    public String toString() {
        return this.stringLang;
    }
    
    public String getNameLocale() {
        return this.nameLocale;
    }
    
}
