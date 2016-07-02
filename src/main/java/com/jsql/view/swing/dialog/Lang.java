package com.jsql.view.swing.dialog;

import javax.swing.Icon;

import com.jsql.view.swing.HelperGui;

public enum Lang {
    EN("English", HelperGui.FLAG_US), 
    ES("Spanish", HelperGui.FLAG_ES), 
    CN("Chinese", HelperGui.FLAG_CN), 
    FR("French", HelperGui.FLAG_FR), 
    IN("Hindi", HelperGui.FLAG_IN), 
    RU("Russian", HelperGui.FLAG_RU), 
    TR("Turkish", HelperGui.FLAG_TR), 
    AR("Arabic", HelperGui.FLAG_AR);

    String stringLang;
    Icon flag;
    
    private Lang(String stringLang, Icon flag) {
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
