package com.jsql.view.swing.dialog.translate;

import javax.swing.Icon;

import com.jsql.view.swing.util.UiUtil;

public enum Language {
    
    AR("Arabic", "ar", UiUtil.ICON_FLAG_AR),
    RU("Russian", "ru", UiUtil.ICON_FLAG_RU),
    ZH("Chinese", "zh", UiUtil.ICON_FLAG_ZH),
    TR("Turkish", "tr", UiUtil.ICON_FLAG_TR),
    EN("English", "en", UiUtil.ICON_FLAG_EN),
    FR("French", "fr", UiUtil.ICON_FLAG_FR),
    HI("Hindi", "hi", UiUtil.ICON_FLAG_HI),
    CS("Czech", "cs", UiUtil.ICON_FLAG_CS),
    DE("German", "de", UiUtil.ICON_FLAG_DE),
    NL("Dutch", "nl", UiUtil.ICON_FLAG_NL),
    IN_ID("Bahasa Indonesia", "in_ID", UiUtil.ICON_FLAG_IN_ID),
    IT("Italian", "it", UiUtil.ICON_FLAG_IT),
    ES("Spanish", "es", UiUtil.ICON_FLAG_ES),
    PT("Portuguese", "pt", UiUtil.ICON_FLAG_PT),
    PL("Polish", "pl", UiUtil.ICON_FLAG_PL),
    KO("Korean", "ko", UiUtil.ICON_FLAG_KO),
    JA("Japanese", "ja", UiUtil.ICON_FLAG_JA),
    RO("Romanian", "ro", UiUtil.ICON_FLAG_RO),
    TA("Tamil", "ta", UiUtil.ICON_FLAG_LK),
    SE("Swedish", "se", UiUtil.ICON_FLAG_SE),
    FI("Finnish", "fi", UiUtil.ICON_FLAG_FI),
    OT("another language", null, null);

    private String nameEnglish;
    private Icon flag;
    private String labelLocale;
    
    private Language(String nameEnglish, String labelLocale, Icon flag) {
        
        this.nameEnglish = nameEnglish;
        this.flag = flag;
        this.labelLocale = labelLocale;
    }
    
    public Icon getFlag() {
        return this.flag;
    }
    
    @Override
    public String toString() {
        return this.nameEnglish;
    }
    
    public String getLabelLocale() {
        return this.labelLocale;
    }
}
