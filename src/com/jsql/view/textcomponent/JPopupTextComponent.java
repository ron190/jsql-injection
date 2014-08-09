/*******************************************************************************
 * Copyhacked (H) 2012-2014.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 *
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 *******************************************************************************/
package com.jsql.view.textcomponent;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import com.jsql.model.InjectionModel;
import com.jsql.view.popupmenu.JPopupTextMenu;

/**
 * A swing JTextComponent with Undo/Redo functionality.
 * @param <T> Component like JTextField or JTextArea to decorate
 */
@SuppressWarnings("serial")
public class JPopupTextComponent<T extends JTextComponent> extends JPopupComponent<T> implements DecoratorJComponent<T> {
    /**
     * Save the component to decorate, add the Undo/Redo.
     * @param proxy Swing component to decorate
     */
    public JPopupTextComponent(final T proxy) {
        super(proxy);

        this.getProxy().setComponentPopupMenu(new JPopupTextMenu(this.getProxy()));

        this.getProxy().setDragEnabled(true);

        final UndoManager undo = new UndoManager();
        Document doc = this.getProxy().getDocument();

        // Listen for undo and redo events
        doc.addUndoableEditListener(new UndoableEditListener() {
            public void undoableEditHappened(UndoableEditEvent evt) {
                undo.addEdit(evt.getEdit());
            }
        });

        // Create an undo action and add it to the text component
        this.getProxy().getActionMap().put("Undo",
            new AbstractAction("Undo") {
                public void actionPerformed(ActionEvent evt) {
                    try {
                        if (undo.canUndo()) {
                            undo.undo();
                        }
                    } catch (CannotUndoException e) {
                        InjectionModel.LOGGER.error(e, e);
                    }
                }
           }
        );

        // Bind the undo action to ctl-Z
        this.getProxy().getInputMap().put(KeyStroke.getKeyStroke("control Z"), "Undo");

        // Create a redo action and add it to the text component
        this.getProxy().getActionMap().put("Redo",
            new AbstractAction("Redo") {
                public void actionPerformed(ActionEvent evt) {
                    try {
                        if (undo.canRedo()) {
                            undo.redo();
                        }
                    } catch (CannotRedoException e) {
                        InjectionModel.LOGGER.error(e, e);
                    }
                }
            }
        );

        // Bind the redo action to ctl-Y
        this.getProxy().getInputMap().put(KeyStroke.getKeyStroke("control Y"), "Redo");
    }
}
