package com.jsql.view.swing.menubar;

import com.jsql.model.InjectionModel;

import javax.swing.*;
import java.util.prefs.Preferences;

public class ModelCheckboxMenu {

    protected final String i18n;
    protected final String keyPref;
    protected final Runnable runnableInsertTab;
    protected final Icon icon;
    protected final boolean isChecked;

    public ModelCheckboxMenu(String i18n, String keyPref, Runnable runnableInsertTab, Icon icon) {
        this(i18n, keyPref, runnableInsertTab, icon, true);
    }

    public ModelCheckboxMenu(String i18n, String keyPref, Runnable runnableInsertTab, Icon icon, boolean isChecked) {
        Preferences preferences = Preferences.userRoot().node(InjectionModel.class.getName());
        this.i18n = i18n;
        this.keyPref = keyPref;
        this.runnableInsertTab = runnableInsertTab;
        this.icon = icon;
        this.isChecked = preferences.getBoolean(keyPref, isChecked);  // must be in sync with preferences default value
    }
}