package com.jsql.view.swing.text.action;

import java.awt.event.ActionEvent;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.TextAction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jsql.util.LogLevel;

/**
 * Action to cancel Beep sound when deleting last character.
 * Used on TextPane and TextArea.
 */
@SuppressWarnings("serial")
public abstract class AbstractCharAction extends TextAction {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();

    /**
     * Create this object with the appropriate identifier.
     */
    protected AbstractCharAction(String deleteAction) {
        
        super(deleteAction);
    }

    protected abstract void delete(Document doc, int dot) throws BadLocationException;

    /**
     * The operation to perform when this action is triggered.
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        
        JTextComponent target = this.getTextComponent(event);

        if (target == null || !target.isEditable()) {
            
            return;
        }
        
        try {
            var doc = target.getDocument();
            var caret = target.getCaret();
            int dot = caret.getDot();
            int mark = caret.getMark();
            
            if (dot != mark) {
                
                doc.remove(Math.min(dot, mark), Math.abs(dot - mark));
                
            } else {
                
                this.delete(doc, dot);
            }
        } catch (BadLocationException e) {
            
            LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
        }
    }
}