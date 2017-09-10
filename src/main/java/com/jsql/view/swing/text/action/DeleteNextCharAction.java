package com.jsql.view.swing.text.action;

import java.awt.event.ActionEvent;

import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.TextAction;

import org.apache.log4j.Logger;

/*
 * Deletes the character of content that follows the
 * current caret position.
 * @see DefaultEditorKit#deleteNextCharAction
 * @see DefaultEditorKit#getActions
 */
@SuppressWarnings("serial")
public class DeleteNextCharAction extends TextAction {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();

    /* Create this object with the appropriate identifier. */
    public DeleteNextCharAction() {
        super(DefaultEditorKit.deleteNextCharAction);
    }

    /** The operation to perform when this action is triggered. */
    @Override
    public void actionPerformed(ActionEvent e) {
        JTextComponent target = this.getTextComponent(e);

        if ((target != null) && (target.isEditable())) {
            try {
                Document doc = target.getDocument();
                Caret caret = target.getCaret();
                int dot = caret.getDot();
                int mark = caret.getMark();
                if (dot != mark) {
                    doc.remove(Math.min(dot, mark), Math.abs(dot - mark));
                } else if (dot < doc.getLength()) {
                    int delChars = 1;

                    if (dot < doc.getLength() - 1) {
                        String dotChars = doc.getText(dot, 2);
                        char c0 = dotChars.charAt(0);
                        char c1 = dotChars.charAt(1);

                        if (c0 >= '\uD800' && c0 <= '\uDBFF' &&
                            c1 >= '\uDC00' && c1 <= '\uDFFF') {
                            delChars = 2;
                        }
                    }

                    doc.remove(dot, delChars);
                }
            } catch (BadLocationException ble) {
                LOGGER.error(ble, ble);
            }
        }
    }
}