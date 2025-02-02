package com.jsql.view.swing.dialog.translate;

import com.jsql.view.swing.util.I18nViewUtil;
import com.jsql.view.swing.util.UiUtil;

import javax.swing.*;
import java.util.Locale;

public enum Language {
    
    AR("Arabic", "ar", UiUtil.ICON_FLAG_AR, true, true),
    RU("Russian", "ru", UiUtil.ICON_FLAG_RU),
    ZH("Chinese", "zh", UiUtil.ICON_FLAG_ZH, true),
    TR("Turkish", "tr", UiUtil.ICON_FLAG_TR),
    EN("English", "en", UiUtil.ICON_FLAG_EN),
    FR("French", "fr", UiUtil.ICON_FLAG_FR),
    HI("Hindi", "hi", UiUtil.ICON_FLAG_HI, true),
    CS("Czech", "cs", UiUtil.ICON_FLAG_CS),
    DE("German", "de", UiUtil.ICON_FLAG_DE),
    NL("Dutch", "nl", UiUtil.ICON_FLAG_NL),
    IN("Indonesian", "in", UiUtil.ICON_FLAG_IN),
    IT("Italian", "it", UiUtil.ICON_FLAG_IT),
    ES("Spanish", "es", UiUtil.ICON_FLAG_ES),
    PT("Portuguese", "pt", UiUtil.ICON_FLAG_PT),
    PL("Polish", "pl", UiUtil.ICON_FLAG_PL),
    KO("Korean", "ko", UiUtil.ICON_FLAG_KO, true),
    JA("Japanese", "ja", UiUtil.ICON_FLAG_JA, true),
    RO("Romanian", "ro", UiUtil.ICON_FLAG_RO),
    TA("Tamil", "ta", UiUtil.ICON_FLAG_LK),
    SE("Swedish", "se", UiUtil.ICON_FLAG_SE),
    FI("Finnish", "fi", UiUtil.ICON_FLAG_FI),
    OT("another language", "unknown", new ImageIcon());

    private final String nameEnglish;  // required for default logging and english modal translate into
    private final ImageIcon flag;
    private final String languageTag;
    private final boolean isNonLatin;
    private final boolean isRightToLeft;

    Language(String nameEnglish, String languageTag, ImageIcon flag, boolean isNonLatin, boolean isRightToLeft) {
        this.nameEnglish = nameEnglish;
        this.flag = flag;
        this.languageTag = languageTag;
        this.isNonLatin = isNonLatin;
        this.isRightToLeft = isRightToLeft;
    }

    Language(String nameEnglish, String languageTag, ImageIcon flag, boolean isNonLatin) {
        this(nameEnglish, languageTag, flag, isNonLatin, false);
    }

    Language(String nameEnglish, String languageTag, ImageIcon flag) {
        this(nameEnglish, languageTag, flag, false, false);
    }

    public String getMenuItemLabel() {
        var label = Locale.forLanguageTag(this.languageTag).getDisplayLanguage(Locale.forLanguageTag(this.languageTag));
        return this.isNonLatin ? I18nViewUtil.formatNonLatin(label) : label;
    }

    public boolean isCurrentLanguage() {
        return Locale.forLanguageTag(this.languageTag).getLanguage().equals(Locale.getDefault().getLanguage());
    }

    public ImageIcon getFlag() {
        return this.flag;
    }

    public boolean isRightToLeft() {
        return this.isRightToLeft;
    }

    public String getLanguageTag() {
        return this.languageTag;
    }

    @Override
    public String toString() {
        return this.nameEnglish;
    }
}
