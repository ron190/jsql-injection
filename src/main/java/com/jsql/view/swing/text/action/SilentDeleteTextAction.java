package com.jsql.view.swing.text.action;

import java.awt.event.ActionEvent;
import java.util.Objects;

import javax.swing.Action;
import javax.swing.text.Caret;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.TextAction;

@SuppressWarnings("serial")
public class SilentDeleteTextAction extends TextAction {
    
    private final transient Action deleteAction;
    
    public SilentDeleteTextAction(String name, Action deleteAction) {
        super(name);
        this.deleteAction = deleteAction;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        JTextComponent target = this.getTextComponent(e);
        if (Objects.nonNull(target) && target.isEditable()) {
            Caret caret = target.getCaret();
            int dot  = caret.getDot();
            int mark = caret.getMark();
            if (DefaultEditorKit.deletePrevCharAction.equals(this.getValue(Action.NAME))) {
                // @see javax/swing/text/DefaultEditorKit.java DeletePrevCharAction
                if (dot == 0 && mark == 0) {
                    return;
                }
            } else {
                // @see javax/swing/text/DefaultEditorKit.java DeleteNextCharAction
                Document doc = target.getDocument();
                if (dot == mark && doc.getLength() == dot) {
                    return;
                }
            }
        }
        this.deleteAction.actionPerformed(e);
    }
    
}