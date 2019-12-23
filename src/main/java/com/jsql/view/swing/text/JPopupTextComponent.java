/*******************************************************************************
 * Copyhacked (H) 2012-2016.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 *
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 *******************************************************************************/
package com.jsql.view.swing.text;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.KeyStroke;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import org.apache.log4j.Logger;

import com.jsql.view.swing.popupmenu.JPopupMenuText;
import com.jsql.view.swing.text.action.SilentDeleteTextAction;

/**
 * A swing JTextComponent with Undo/Redo functionality.
 * @param <T> Component like JTextField or JTextArea to decorate
 */
@SuppressWarnings("serial")
public class JPopupTextComponent<T extends JTextComponent> extends JPopupComponent<T> implements DecoratorJComponent<T> {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();

    /**
     * Save the component to decorate, add the Undo/Redo.
     * @param proxy Swing component to decorate
     */
    public JPopupTextComponent(final T proxy) {
        super(proxy);

        this.getProxy().setComponentPopupMenu(new JPopupMenuText(this.getProxy()));

        this.getProxy().setDragEnabled(true);

        final UndoManager undo = new UndoManager();
        Document doc = this.getProxy().getDocument();

        // Listen for undo and redo events
        doc.addUndoableEditListener(undoableEditEvent -> undo.addEdit(undoableEditEvent.getEdit()));

        // Create an undo action and add it to the text component
        final String undoIdentifier = "Undo";
        this.getProxy().getActionMap().put(undoIdentifier,
            new AbstractAction(undoIdentifier) {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    try {
                        if (undo.canUndo()) {
                            undo.undo();
                        }
                    } catch (CannotUndoException e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                }
           }
        );

        // Bind the undo action to ctl-Z
        this.getProxy().getInputMap().put(KeyStroke.getKeyStroke("control Z"), undoIdentifier);

        // Create a redo action and add it to the text component
        final String redoIdentifier = "Redo";
        this.getProxy().getActionMap().put(redoIdentifier,
            new AbstractAction(redoIdentifier) {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    try {
                        if (undo.canRedo()) {
                            undo.redo();
                        }
                    } catch (CannotRedoException e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                }
            }
        );

        // Bind the redo action to ctl-Y
        this.getProxy().getInputMap().put(KeyStroke.getKeyStroke("control Y"), redoIdentifier);
        
        // Silent delete
        ActionMap am = this.getProxy().getActionMap();

        String key = DefaultEditorKit.deletePrevCharAction;
        am.put(key, new SilentDeleteTextAction(key, am.get(key)));

        key = DefaultEditorKit.deleteNextCharAction;
        am.put(key, new SilentDeleteTextAction(key, am.get(key)));
    }
    
}
