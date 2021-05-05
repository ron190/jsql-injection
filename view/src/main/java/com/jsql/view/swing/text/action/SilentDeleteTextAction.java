package com.jsql.view.swing.text.action;

import java.awt.event.ActionEvent;
import java.util.Objects;

import javax.swing.Action;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;
import javax.swing.text.TextAction;

/**
 * Action to cancel Beep sound when deleting last character.
 * Used on TextField.
 */
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
            
            var caret = target.getCaret();
            int dot = caret.getDot();
            int mark = caret.getMark();
            
            if (DefaultEditorKit.deletePrevCharAction.equals(this.getValue(Action.NAME))) {
                
                // @see javax/swing/text/DefaultEditorKit.java DeletePrevCharAction
                if (dot == 0 && mark == 0) {
                    
                    return;
                }
            } else {
                
                // @see javax/swing/text/DefaultEditorKit.java DeleteNextCharAction
                var doc = target.getDocument();
                
                if (dot == mark && doc.getLength() == dot) {
                    
                    return;
                }
            }
        }
        
        this.deleteAction.actionPerformed(e);
    }
}