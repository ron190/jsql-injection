package com.jsql.view.swing.menubar;

import com.jsql.view.swing.dialog.DialogTranslate;
import com.jsql.view.swing.dialog.translate.Language;
import com.jsql.view.swing.util.MediatorHelper;

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
    public void actionPerformed(ActionEvent arg0) {
        dialogTranslate.initializeDialog(this.language);
        if (!dialogTranslate.isVisible()) {  // Center the dialog
            dialogTranslate.setSize(640, 460);
            dialogTranslate.setLocationRelativeTo(MediatorHelper.frame());
            dialogTranslate.getRootPane().setDefaultButton(dialogTranslate.getButtonSend());
        }
        dialogTranslate.setVisible(true);
    }
}

