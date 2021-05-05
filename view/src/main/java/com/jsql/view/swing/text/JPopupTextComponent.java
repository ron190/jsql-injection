/*******************************************************************************
 * Copyhacked (H) 2012-2020.
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
import javax.swing.KeyStroke;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jsql.util.LogLevel;
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
    private static final Logger LOGGER = LogManager.getRootLogger();

    /**
     * Save the component to decorate, add the Undo/Redo.
     * @param proxy Swing component to decorate
     */
    public JPopupTextComponent(final T proxy) {
        
        super(proxy);

        this.getProxy().setComponentPopupMenu(new JPopupMenuText(this.getProxy()));

        this.getProxy().setDragEnabled(true);

        var undoRedoManager = new UndoManager();
        var doc = this.getProxy().getDocument();

        // Listen for undo and redo events
        doc.addUndoableEditListener(undoableEditEvent -> undoRedoManager.addEdit(undoableEditEvent.getEdit()));

        this.initializeUndo(undoRedoManager);
        this.initializeRedo(undoRedoManager);
        this.makeDeleteSilent();
    }

    private void initializeUndo(final UndoManager undo) {
        
        // Create an undo action and add it to the text component
        final var undoIdentifier = "Undo";
        
        this.getProxy().getActionMap().put(undoIdentifier, new AbstractAction(undoIdentifier) {
            
            @Override
            public void actionPerformed(ActionEvent evt) {
                
                // Unhandled ArrayIndexOutOfBoundsException #92146 on undo()
                try {
                    if (undo.canUndo()) {
                        
                        undo.undo();
                    }
                    
                } catch (ArrayIndexOutOfBoundsException | CannotUndoException e) {
                    
                    LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
                }
            }
       });

        // Bind the undo action to ctl-Z
        this.getProxy().getInputMap().put(KeyStroke.getKeyStroke("control Z"), undoIdentifier);
    }

    private void initializeRedo(final UndoManager undo) {
        
        // Create a redo action and add it to the text component
        final var redoIdentifier = "Redo";
        
        this.getProxy().getActionMap().put(redoIdentifier, new AbstractAction(redoIdentifier) {
            
            @Override
            public void actionPerformed(ActionEvent evt) {
                
                try {
                    if (undo.canRedo()) {
                        
                        undo.redo();
                    }
                    
                } catch (CannotRedoException e) {
                    
                    LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
                }
            }
        });

        // Bind the redo action to ctl-Y
        this.getProxy().getInputMap().put(KeyStroke.getKeyStroke("control Y"), redoIdentifier);
    }

    private void makeDeleteSilent() {
        
        // Silent delete
        var actionMap = this.getProxy().getActionMap();

        String key = DefaultEditorKit.deletePrevCharAction;
        actionMap.put(key, new SilentDeleteTextAction(key, actionMap.get(key)));

        key = DefaultEditorKit.deleteNextCharAction;
        actionMap.put(key, new SilentDeleteTextAction(key, actionMap.get(key)));
    }
}
