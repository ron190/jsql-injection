package com.jsql.view.swing.menubar;

import com.jsql.view.swing.dialog.translate.Language;

import javax.swing.*;

public class ModelItemTranslate {

    private JMenuItem menuItem;
    private final Language language;

    public ModelItemTranslate(Language language) {
        this.language = language;
    }

    public JMenuItem getMenuItem() {
        return this.menuItem;
    }

    public void setMenuItem(JMenuItem menuItem) {
        this.menuItem = menuItem;
    }

    public Language getLanguage() {
        return this.language;
    }
}