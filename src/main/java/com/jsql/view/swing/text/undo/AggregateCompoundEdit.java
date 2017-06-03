package com.jsql.view.swing.text.undo;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CompoundEdit;

@SuppressWarnings("serial")
public class AggregateCompoundEdit extends CompoundEdit {

    private boolean isUnDone = false;

    public int getLength() {
        return this.edits.size();
    }

    public void undo() throws CannotUndoException {
        try {
            super.undo();
            this.isUnDone = true;
        } catch (CannotUndoException e) {
            e.printStackTrace();
        }
    }

    public void redo() throws CannotUndoException {
        try {
            super.redo();
            this.isUnDone = false;
        } catch (CannotRedoException e) {
            e.printStackTrace();
        }
    }

    public boolean canUndo() {
        return this.edits.size() > 0 && !this.isUnDone;
    }

    public boolean canRedo() {
        return this.edits.size() > 0 && this.isUnDone;
    }

}