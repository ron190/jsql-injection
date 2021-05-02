package com.jsql.view.swing.tab.dnd;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;

public class TabDropTargetAdapter extends DropTargetAdapter {
    
    private void clearDropLocationPaint(Component c) {
        
        if (c instanceof DnDTabbedPane) {
            
            DnDTabbedPane t = (DnDTabbedPane) c;
            t.setDropLocation(null, false);
            t.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }
    
    @Override
    public void drop(DropTargetDropEvent dtde) {
        
        var c = dtde.getDropTargetContext().getComponent();
        
        this.clearDropLocationPaint(c);
    }
    
    @Override
    public void dragExit(DropTargetEvent dte) {
        
        var c = dte.getDropTargetContext().getComponent();
        
        this.clearDropLocationPaint(c);
    }
    
    @Override
    public void dragEnter(DropTargetDragEvent dtde) {
        
        dtde.getDropTargetContext().getComponent();
    }
}