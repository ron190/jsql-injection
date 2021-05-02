package com.jsql.view.swing.text.action;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;

/**
 * Action to cancel Beep sound when deleting last character.
 * Used on TextPane and TextArea.
 */
@SuppressWarnings("serial")
public class DeletePrevCharAction extends AbstractCharAction {
    
    /**
     * Creates this object with the appropriate identifier.
     */
    public DeletePrevCharAction() {
        
        super(DefaultEditorKit.deletePrevCharAction);
    }

    @Override
    protected void delete(Document doc, int dot) throws BadLocationException {
        
        if (dot > 0) {
            
            var delChars = 1;

            if (dot > 1) {
                
                String dotChars = doc.getText(dot - 2, 2);
                var c0 = dotChars.charAt(0);
                var c1 = dotChars.charAt(1);

                if (c0 >= '\uD800' && c0 <= '\uDBFF' && c1 >= '\uDC00' && c1 <= '\uDFFF') {
                    
                    delChars = 2;
                }
            }

            doc.remove(dot - delChars, delChars);
        }
    }
}