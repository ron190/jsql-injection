package com.jsql.view.swing.menubar;

import com.jsql.view.swing.dialog.DialogTranslate;
import com.jsql.view.swing.dialog.translate.Language;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ActionTranslate implements ActionListener {

    private final DialogTranslate dialogTranslate;
    private final Language language;

    ActionTranslate(DialogTranslate dialogTranslate, Language language) {
        this.dialogTranslate = dialogTranslate;
        this.language = language;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        this.dialogTranslate.initializeDialog(this.language);
    }
}

