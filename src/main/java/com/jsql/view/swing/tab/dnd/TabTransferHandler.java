package com.jsql.view.swing.tab.dnd;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragSource;

import javax.activation.ActivationDataFlavor;
import javax.activation.DataHandler;
import javax.swing.JComponent;
import javax.swing.TransferHandler;

import org.apache.log4j.Logger;

import com.jsql.view.swing.tab.TabResults;

@SuppressWarnings("serial")
public class TabTransferHandler extends TransferHandler {
	
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();

    private final DataFlavor localObjectFlavor;
    
    private TabbedPaneDnD srcDnDTabbedPane = null;
    
    private static PanelGhostGlass glassPane;
    
    public TabTransferHandler() {
        localObjectFlavor = new ActivationDataFlavor(TabResults.class, DataFlavor.javaJVMLocalObjectMimeType, "RightPaneAdapter");
    }
    
    @Override
    protected Transferable createTransferable(JComponent c) {
        if (c instanceof TabbedPaneDnD) {
            srcDnDTabbedPane = (TabbedPaneDnD) c;
        }
        return new DataHandler(c, localObjectFlavor.getMimeType());
    }
    
    @Override
    public boolean canImport(TransferSupport support) {
        if (!support.isDrop() || !support.isDataFlavorSupported(localObjectFlavor)) {
            return false;
        }
        
        support.setDropAction(MOVE);
        DropLocation tdl = support.getDropLocation();
        Point pt = tdl.getDropPoint();
        TabbedPaneDnD target = (TabbedPaneDnD) support.getComponent();
        target.autoScrollTest(pt);
        TabbedPaneDnD.DropLocationDnD dl = target.dropLocationForPointLocal(pt);
        int idx = dl.getIndex();
        boolean isDropable = false;

        if (target == srcDnDTabbedPane) {
            isDropable = 
                target.getTabAreaBounds().contains(pt) && 
                idx >= 0 && 
                idx != target.dragTabIndex && 
                idx != target.dragTabIndex + 1
            ;
        } else {
            if (
                srcDnDTabbedPane != null && 
                target != srcDnDTabbedPane.getComponentAt(srcDnDTabbedPane.dragTabIndex)
            ) {
                isDropable = target.getTabAreaBounds().contains(pt) && idx >= 0;
            }
        }
        
        glassPane.setVisible(false);
        target.getRootPane().setGlassPane(glassPane);
        
        //Bug ID: 6700748 Cursor flickering during D&D when using CellRendererPane with validation
        //http://bugs.sun.com/view_bug.do?bug_id=6700748
        glassPane.setCursor(isDropable ? DragSource.DefaultMoveDrop : DragSource.DefaultMoveNoDrop);
        glassPane.setVisible(true);
        target.setCursor(isDropable ? DragSource.DefaultMoveDrop : DragSource.DefaultMoveNoDrop);

        if (isDropable) {
            support.setShowDropLocation(true);
            dl.setDropable(true);
            target.setDropLocationLocal(dl, true);
            return true;
        } else {
            support.setShowDropLocation(false);
            dl.setDropable(false);
            target.setDropLocationLocal(dl, false);
            return false;
        }
    }

    @Override
    public int getSourceActions(JComponent c) {
        TabbedPaneDnD src = (TabbedPaneDnD) c;
        if (glassPane == null) {
            glassPane = new PanelGhostGlass(src);
            c.getRootPane().setGlassPane(glassPane);
        }
        
        if (src.dragTabIndex < 0) {
            return NONE;
        }

        c.getRootPane().getGlassPane().setVisible(true);
        return MOVE;
    }
    
    @Override
    public boolean importData(TransferSupport support) {
        if (!canImport(support)) {
            return false;
        }

        TabbedPaneDnD target = (TabbedPaneDnD) support.getComponent();
        TabbedPaneDnD.DropLocationDnD dl = target.getDropLocation();
        try {
            TabbedPaneDnD src = (TabbedPaneDnD) support.getTransferable().getTransferData(localObjectFlavor);
            int index = dl.getIndex();
            if (target == src) {
                src.convertTab(src.dragTabIndex, index);
            } else {
                src.exportTab(src.dragTabIndex, target, index);
            }
            return true;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return false;
    }
    
    @Override 
    protected void exportDone(JComponent c, Transferable data, int action) {
        TabbedPaneDnD src = (TabbedPaneDnD) c;
        src.setDropLocationLocal(null, false);
        src.repaint();
        glassPane.setVisible(false);
        src.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
    
}
