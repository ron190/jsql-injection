package com.jsql.view.swing.menubar;

import javax.swing.*;

public class ModelCheckboxMenu {

    protected final String i18n;
    protected final String keyPref;
    protected final Runnable runnableInsertTab;
    protected final Icon icon;

    public ModelCheckboxMenu(String i18n, String keyPref, Runnable runnableInsertTab, Icon icon) {
        this.i18n = i18n;
        this.keyPref = keyPref;
        this.runnableInsertTab = runnableInsertTab;
        this.icon = icon;
    }
}