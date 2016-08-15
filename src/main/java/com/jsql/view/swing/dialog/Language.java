package com.jsql.view.swing.dialog;

import javax.swing.Icon;

import com.jsql.view.swing.HelperUi;

public enum Language {
    
    AR("Arabic", HelperUi.ICON_FLAG_AR),
    RU("Russian", HelperUi.ICON_FLAG_RU), 
    ZH("Chinese", HelperUi.ICON_FLAG_ZH), 
    TR("Turkish", HelperUi.ICON_FLAG_TR), 
    EN("English", HelperUi.ICON_FLAG_EN), 
    FR("French", HelperUi.ICON_FLAG_FR), 
    HI("Hindi", HelperUi.ICON_FLAG_HI), 
    CS("Czech", HelperUi.ICON_FLAG_CS), 
    OT("another language", null);

    String stringLang;
    Icon flag;
    
    private Language(String stringLang, Icon flag) {
        this.stringLang = stringLang;
        this.flag = flag;
    }
    
    public Icon getFlag() {
        return this.flag;
    }
    
    @Override
    public String toString() {
        return stringLang;
    }
}
