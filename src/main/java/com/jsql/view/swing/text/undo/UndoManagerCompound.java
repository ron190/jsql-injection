package com.jsql.view.swing.text.undo;

import java.util.ArrayList;

import javax.swing.event.UndoableEditEvent;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

@SuppressWarnings("serial")
public class UndoManagerCompound extends UndoManager {
    
    private String lastEditName = null;
    private ArrayList<AggregateCompoundEdit> edits = new ArrayList<AggregateCompoundEdit>();
    private AggregateCompoundEdit current;
    private int pointer = -1;

    public void undoableEditHappened(UndoableEditEvent e) {
        UndoableEdit edit = e.getEdit();
        
        if (edit instanceof AbstractDocument.DefaultDocumentEvent) {
            AbstractDocument.DefaultDocumentEvent event = (AbstractDocument.DefaultDocumentEvent) edit;
            int start = event.getOffset();
            int len = event.getLength();
            String text;
            try {
                text = event.getDocument().getText(start, len);
            } catch (BadLocationException ez) {
                text = "";
            }
            
            boolean isNeedStart = false;
            if (this.current == null) {
                isNeedStart = true;
            } else if (text.contains("\n")) {
                isNeedStart = true;
            } else if (this.lastEditName == null || !this.lastEditName.equals(edit.getPresentationName())) {
                isNeedStart = true;
            }

            while (this.pointer < this.edits.size() - 1) {
                this.edits.remove(this.edits.size() - 1);
                isNeedStart = true;
            }
            
            if (isNeedStart) {
                this.createCompoundEdit();
            }

            this.current.addEdit(edit);
            this.lastEditName = edit.getPresentationName();
        }
    }

    public void createCompoundEdit() {
        if (this.current == null) {
            this.current = new AggregateCompoundEdit();
        }
        else if (this.current.getLength()>0) {
            this.current = new AggregateCompoundEdit();
        }

        this.edits.add(this.current);
        this.pointer++;
    }

    public void undo() throws CannotUndoException {
        if (this.canUndo()) {
            AggregateCompoundEdit u = this.edits.get(this.pointer);
            u.undo();
            this.pointer--;
        }
    }

    public void redo() throws CannotUndoException {
        if (this.canRedo()) {
            this.pointer++;
            AggregateCompoundEdit u = this.edits.get(this.pointer);
            u.redo();
        }
    }

    public boolean canUndo() {
        return this.pointer >= 0;
    }

    public boolean canRedo() {
        return this.edits.size() > 0 && this.pointer < this.edits.size() - 1;
    }

}