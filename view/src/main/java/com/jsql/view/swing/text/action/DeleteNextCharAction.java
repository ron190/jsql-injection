package com.jsql.view.swing.text.action;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;

/**
 * Action to cancel Beep sound when deleting last character.
 * Used on TextPane and TextArea.
 */
@SuppressWarnings("serial")
public class DeleteNextCharAction extends AbstractCharAction {
    
    /**
     * Create this object with the appropriate identifier.
     */
    public DeleteNextCharAction() {
        
        super(DefaultEditorKit.deleteNextCharAction);
    }

    @Override
    protected void delete(Document doc, int dot) throws BadLocationException {
        
        if (dot < doc.getLength()) {
            
            var delChars = 1;

            if (dot < doc.getLength() - 1) {
                
                String dotChars = doc.getText(dot, 2);
                var c0 = dotChars.charAt(0);
                var c1 = dotChars.charAt(1);

                if (c0 >= '\uD800' && c0 <= '\uDBFF' && c1 >= '\uDC00' && c1 <= '\uDFFF') {
                    
                    delChars = 2;
                }
            }

            doc.remove(dot, delChars);
        }
    }
}