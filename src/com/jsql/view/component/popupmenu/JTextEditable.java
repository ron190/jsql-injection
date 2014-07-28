package com.jsql.view.component.popupmenu;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import com.jsql.view.GUIMediator;

@SuppressWarnings("serial")
public class JTextEditable {

	public static void setEditable(final JTextComponent t) {
        t.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                // Left button will deselect text after selectAll, so only for right click
                if(SwingUtilities.isRightMouseButton(e))
                	t.requestFocusInWindow();
            }
        });

		t.setComponentPopupMenu(new JPopupTextMenu(t, true));
		
        t.setDragEnabled(true);
        
        final UndoManager undo = new UndoManager();
        Document doc = t.getDocument();
        
        // Listen for undo and redo events
        doc.addUndoableEditListener(new UndoableEditListener() {
            public void undoableEditHappened(UndoableEditEvent evt) {
                undo.addEdit(evt.getEdit());
            }
        });
        
        // Create an undo action and add it to the text component
        t.getActionMap().put("Undo",
            new AbstractAction("Undo") {
                public void actionPerformed(ActionEvent evt) {
                    try {
                        if (undo.canUndo()) {
                            undo.undo();
                        }
                    } catch (CannotUndoException e) {
                    	GUIMediator.model().sendDebugMessage(e);
                    }
                }
           });
        
        // Bind the undo action to ctl-Z
        t.getInputMap().put(KeyStroke.getKeyStroke("control Z"), "Undo");
        
        // Create a redo action and add it to the text component
        t.getActionMap().put("Redo",
            new AbstractAction("Redo") {
                public void actionPerformed(ActionEvent evt) {
                    try {
                        if (undo.canRedo()) {
                            undo.redo();
                        }
                    } catch (CannotRedoException e) {
                    	GUIMediator.model().sendDebugMessage(e);
                    }
                }
            });
        
        // Bind the redo action to ctl-Y
        t.getInputMap().put(KeyStroke.getKeyStroke("control Y"), "Redo");
	}

}
