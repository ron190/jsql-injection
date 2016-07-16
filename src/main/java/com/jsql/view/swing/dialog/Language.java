package com.jsql.view.swing.dialog;

import javax.swing.Icon;

import com.jsql.view.swing.HelperUi;

public enum Language {
    
    DE("German", HelperUi.FLAG_DE), 
    EN("English", HelperUi.FLAG_US), 
    ES("Spanish", HelperUi.FLAG_ES), 
    CN("Chinese", HelperUi.FLAG_CN), 
    FR("French", HelperUi.FLAG_FR), 
    IN("Hindi", HelperUi.FLAG_IN), 
    RU("Russian", HelperUi.FLAG_RU), 
    TR("Turkish", HelperUi.FLAG_TR), 
    AR("Arabic", HelperUi.FLAG_AR),
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
