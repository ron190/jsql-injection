package com.jsql.view.swing.menubar;

import com.jsql.view.swing.dialog.translate.Language;

import javax.swing.*;

public class ModelItem {

    public JMenuItem menuItem;
    public final Language language;

    public ModelItem(Language language) {
        this.language = language;
    }
}